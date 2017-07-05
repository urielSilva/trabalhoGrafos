import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import ca.pjer.ekmeans.EKmeans;

public class Main {
	
	private final static double SIGMA = 2;
	private final static int ROWS_SIZE = 4898;
	private final static int COLUMNS_SIZE= 12;
	
	public static void main(String[] args) {
		double[][] instances = readFile();
        double[][] affinityMatrix = buildAffinityMatrix(instances);
        for(int i =0; i < affinityMatrix.length; i++) {
        	for(int j =0; j < affinityMatrix.length; j++ ) {
	        	if (affinityMatrix[i][j] != affinityMatrix[j][i]) {
	        		System.out.println("DIFERENTES " + affinityMatrix[i][j] + " " + affinityMatrix[j][i]);
	        	}
        	}
        }
        Matrix laplacian = buildLaplacian(affinityMatrix);
        EigenvalueDecomposition evd = laplacian.eig();  
        double[][] eigenVectors = evd.getV().getArray();
        Matrix yMatrix = buildYMatrix(eigenVectors);
        EKmeans result = runKMeans(yMatrix);
        
        
	}
	
	public static EKmeans runKMeans(Matrix yMatrix) {
		int n = ROWS_SIZE; 
        int k = 6; 
        Random random = new Random(System.currentTimeMillis());
        
        double[][] centroids = new double[n][k];
        
        for (int i = 0; i < n; i++) {
			centroids[i][0] = Math.abs(1);
		    centroids[i][1] = Math.abs(2);
		    centroids[i][2] = Math.abs(3);
			centroids[i][3] = Math.abs(4);
		    centroids[i][4] = Math.abs(5);
		    centroids[i][5] = Math.abs(6);
		}
        
        EKmeans kmeans = new EKmeans(centroids, yMatrix.getArray());
        kmeans.run();
        return kmeans;
	}
	
	public static Matrix buildYMatrix(double[][] eigenVectors) {
		double[][] Y = new double[ROWS_SIZE][6];
		for (int i = 0; i < eigenVectors.length; i++) {
            for (int j = 0; j < 6; j++) {
            	
            	double sumValue = 0;
            	for(int k = 0; k < 6; k++ ) {
            		sumValue += Math.pow(eigenVectors[i][k], 2);
            	}
            	double sqrtValue = Math.sqrt(sumValue);
            	double finalValue = eigenVectors[i][j] / sqrtValue;
            	System.out.println(finalValue);
            	Y[i][j] = finalValue;
            }
        }
		return new Matrix(Y);
	}

	private static Matrix buildLaplacian(double[][] affinityMatrix) {
		System.out.println("cheguei no laplaciano");
		double[][] diagonalMatrix = getDiagonalMatrix(affinityMatrix);
		Matrix diagonalSqrt = new Matrix(getDiagonalSqrt(diagonalMatrix));
		
		Matrix inverse = diagonalSqrt.inverse();
		Matrix affinity = new Matrix(affinityMatrix);
		System.out.println("calculei a inversa");
		Matrix laplacian = inverse.times(affinity).times(inverse);
		return laplacian;
	}
	
	private static double[][] getDiagonalSqrt(double[][] diagonalMatrix) {
		for(int i = 0; i < diagonalMatrix.length; i++ ) {
			diagonalMatrix[i][i] = Math.sqrt(diagonalMatrix[i][i]);
		}	
		return diagonalMatrix;
	}
	
	private static double[][] getDiagonalMatrix(double[][] affinityMatrix) {
		double[][] diagonalMatrix = new double[affinityMatrix.length][affinityMatrix.length];
		for(int i = 0; i < affinityMatrix.length; i++) {
			double value = 0;
			for(int j = 0; j < affinityMatrix.length; j++) {
				value += affinityMatrix[i][j];
				diagonalMatrix[i][i] = value;	
			}
		}
		return diagonalMatrix;
	}

	private static double[][] buildAffinityMatrix(double[][] instances) {
		double[][] affinityMatrix = new double[ROWS_SIZE][ROWS_SIZE];
		for(int i = 0; i < instances.length; i++) {
			for(int j = 0; j < instances.length; j++) {
				affinityMatrix[i][j] = (i == j ? 0 : calculateAffinity(instances, i, j));
			}
		}
		return affinityMatrix;
	}

	private static double calculateAffinity(double[][] instances, int i, int j) {
		double similarity = 0;
		for(int k = 0; k < instances[1].length; k++ ) {
			double value = Math.pow((instances[i][k] - instances[j][k]), 2);
			similarity += value;
		}
		similarity = Math.sqrt(similarity);
		
		return Math.pow(Math.E, similarity / (2 * Math.pow(SIGMA, 2)));
	}

	private static double[][] readFile() {
		String csvFile = "winequality-white.csv";
		BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        double[][] instances = new double[ROWS_SIZE][COLUMNS_SIZE];
        
		try {
            br = new BufferedReader(new FileReader(csvFile));
            br.readLine();
            int i = 0;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(cvsSplitBy);
                for(int j = 0; j < tokens.length; j++) {
                	instances[i][j] = Double.valueOf(tokens[j]);
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return instances;
	}
}
