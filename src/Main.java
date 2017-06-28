import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
	
	private final static double SIGMA = 2;
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
        List<List<Double>> instances = readFile();
        Double[][] affinityMatrix = buildAffinityMatrix(instances);
        for(int i =0; i < affinityMatrix.length; i++) {
        	for(int j =0; j < affinityMatrix.length; j++ ) {
        		if (affinityMatrix[i][j].doubleValue() != affinityMatrix[j][i].doubleValue()) {
        			System.out.println("DIFERENTES " + affinityMatrix[i][j] + " " + affinityMatrix[j][i]);
        		}
        	}
        }
        
	}

	private static Double[][] buildAffinityMatrix(List<List<Double>> instances) {
		Double[][] affinityMatrix = new Double[instances.size()][instances.size()];
		for(int i = 0; i < instances.size(); i++) {
			for(int j = 0; j < instances.size(); j++) {
				affinityMatrix[i][j] = i == j ? 0 : calculateAffinity(instances.get(i), instances.get(j));
			}
			
		}
		return affinityMatrix;
	}

	private static Double calculateAffinity(List<Double> s1, List<Double> s2) {
		double similarity = 0;
		for(int i = 0; i < s1.size(); i++ ) {
			double value = Math.pow(s1.get(i) - s2.get(i), 2);
			similarity += value;
		}
		similarity = Math.sqrt(similarity);
		
		return Math.pow(Math.E, similarity / (2 * Math.pow(SIGMA, 2)));
	}

	private static List<List<Double>> readFile() {
		String csvFile = "winequality-white.csv";
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        List<List<Double>> instances = new ArrayList<>();
        
		try {
            br = new BufferedReader(new FileReader(csvFile));
            br.readLine();
            while ((line = br.readLine()) != null) {

                String[] tokens = line.split(cvsSplitBy);
                List<Double> instance = new ArrayList<>();
                for(String token : tokens) {
                	instance.add(Double.valueOf(token));
                }
                instances.add(instance);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return instances;
	}
}
