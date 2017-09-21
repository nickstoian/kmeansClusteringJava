//Nicolas Stoian
//This program needs 3 command line arguments
//args[0] "input1" for text file representing the list of points in x-y coordinates
//args[1] "input2" for integer representing the number of K clusters
//args[2] "output1" to write the 2-D image

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Random;

public class KMeansClustering {
	public static void main(String args[]){
		try{
			Scanner inFile = new Scanner(new FileReader(args[0]));
			int numRows = inFile.nextInt();
		    int numCols = inFile.nextInt();

		    int[][] pointSet = new int[numRows][numCols];
		    for(int row = 0; row < numRows; row++){
		        for(int col = 0; col < numCols; col++){
		            pointSet[row][col] = -1;
		        }
		    }
		    loadPointSet(pointSet, inFile);
		    int k = Integer.parseInt(args[1]);
		    int[] numPoints = new int [k];
		    double[] centroidX = new double [k];
		    double[] centroidY = new double [k];
		    for(int i = 0; i < k; i++){
		        numPoints[i] = 0;
		        centroidX[i] = 0;
		        centroidY[i] = 0;
		    }
		    partitionPointSet(pointSet, numRows, numCols, numPoints, k);
		    int numChangedLabel = 1;
		    int iteration = 0;
		    PrintWriter outFile = new PrintWriter(new FileWriter(args[2]));
		    outFile.println("iteration --> " + iteration);
		    outFile.println("original partition of points, k = " + k);
		    outputPointSet(pointSet, numRows, numCols, outFile);
		    while(numChangedLabel != 0){
		        computeCentroids(pointSet, numRows, numCols, numPoints, centroidX, centroidY, k);
		        numChangedLabel = checkDistances(pointSet, numRows, numCols, numPoints, centroidX, centroidY, k);
		        iteration++;
		        outFile.println("iteration --> " + iteration);
		        outFile.println("numChangedLabel --> " + numChangedLabel);
		        outputPointSet(pointSet, numRows, numCols, outFile);
		    }
		    inFile.close();
		    outFile.close();
		}
		catch(NoSuchElementException e){
			System.err.println("Error in input file format, check the input file and try again.");
            return;
		}
		catch(FileNotFoundException e){
			System.err.println("File not found exception, check arguements and try again.");
            return;
		}
		catch(IOException e){
			System.err.println("IO exception, check arguements and try again.");
            return;
		}
	}

	public static void loadPointSet(int[][] pointSet, Scanner inFile){
	    int row;
	    int col;
	    while(inFile.hasNext()){
	    	row = inFile.nextInt();
	    	col = inFile.nextInt();
	        pointSet[row][col] = 0;
	    }
	}

	public static void partitionPointSet(int[][] pointSet, int numRows, int numCols, int[] numPoints, int k){
		Random r = new Random();
	    int numPointsTotal = 0;
	    for(int row = 0; row < numRows; row++){
	        for(int col = 0; col < numCols; col++){
	            if(pointSet[row][col] == 0){
	                numPointsTotal++;
	            }
	        }
	    }
	    int[] pointsLimit = new int [k];
	    for(int i = 0; i < k; i++){
	        pointsLimit[i] = numPointsTotal / k;
	    }
	    for(int i = 0; i < numPointsTotal % k; i++){
	        pointsLimit[i]++;
	    }
	    for(int row = 0; row < numRows; row++){
	        for(int col = 0; col < numCols; col++){
	            if(pointSet[row][col] == 0){
	                int group = r.nextInt(k);
	                while(numPoints[group] == pointsLimit[group]){
	                    group = r.nextInt(k);
	                }
	                pointSet[row][col] = group + 1;
	                numPoints[group]++;
	            }
	        }
	    }
	}

	public static void displayPointSet(int[][] pointSet, int numRows, int numCols){
	    for(int row = 0; row < numRows; row++){
	        for(int col = 0; col < numCols; col++){
	            if(pointSet[row][col] == -1){
	            	System.out.print(" ");
	            }
	            else{
	            	System.out.print(pointSet[row][col]);
	            }
	        }
	        System.out.println();
	    }
	}

	public static void outputPointSet(int[][] pointSet, int numRows, int numCols, PrintWriter outFile){
	    for(int row = 0; row < numRows; row++){
	        for(int col = 0; col < numCols; col++){
	            if(pointSet[row][col] == -1){
	                outFile.print(" ");
	            }
	            else{
	            	outFile.print(pointSet[row][col]);
	            }
	        }
	        outFile.println();
	    }
	}

	public static void computeCentroids(int[][] pointSet, int numRows, int numCols, int[] numPoints, double[] centroidX, double[] centroidY, int k ){
	    displayPointSet(pointSet, numRows, numCols);
	    int[] sumX = new int [k];
	    int[] sumY = new int [k];
	    for(int i = 0; i < k; i++){
	        sumX[i] = 0;
	        sumY[i] = 0;
	    }
	    for(int row = 0; row < numRows; row++){
	        for(int col = 0; col < numCols; col++){
	            if(pointSet[row][col] > 0){
	               sumX[pointSet[row][col] - 1] += row;
	               sumY[pointSet[row][col] - 1] += col;
	            }
	        }
	    }
	    for(int i = 0; i < k; i++){
	        centroidX[i] = 0;
	        centroidY[i] = 0;
	    }
	    for(int i = 0; i < k; i++){
	        if(numPoints[i] > 0){
	            centroidX[i] = (double)sumX[i]/numPoints[i];
	            centroidY[i] = (double)sumY[i]/numPoints[i];
	        }
	    }
	}

	public static int checkDistances(int[][] pointSet, int numRows, int numCols, int[] numPoints, double[] centroidX, double[] centroidY, int k ){
	    double[] distances = new double [k];
	    for(int i = 0; i < k; i++){
	        distances[i] = 0;
	    }
	    int numChangedLabel = 0;
	    for(int row = 0; row < numRows; row++){
	        for(int col = 0; col < numCols; col++){
	            if(pointSet[row][col] > 0){
	                for(int i = 0; i < k; i++){
	                	if (centroidX[i] != 0 && centroidY[i] != 0){
	                		distances[i] = computeDistance(row, col, centroidX[i], centroidY[i]);
	                	}
	                }
	                int min_i = findMinI(distances, k);
	                if(pointSet[row][col] != (min_i + 1)){
	                    numPoints[pointSet[row][col] - 1]--;
	                    numPoints[min_i]++;
	                    pointSet[row][col] = min_i + 1;
	                    numChangedLabel++;
	                }
	            }
	        }
	    }
	    return numChangedLabel;
	}

	public static double computeDistance(int x1, int y1, double x2, double y2){
	    return Math.sqrt(((x1-x2)*(x1-x2))+((y1-y2)*(y1-y2)));
	}

	public static int findMinI(double[] distances, int k){
	    double minDistance = Double.MAX_VALUE;
	    int min_i = 0;
	    for(int i = 0; i < k; i++){
	        if(distances[i] < minDistance && distances[i] != 0){
	            minDistance = distances[i];
	            min_i = i;
	        }
	    }
	    return min_i;
	}
}
