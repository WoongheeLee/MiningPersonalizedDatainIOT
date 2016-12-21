import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

import Jama.Matrix;

public class Predict{
	final private static int k = 250;
	final private static double ran = 0.00001d;
	
	private static Matrix initialMatrix(Matrix M) {
		double[][] arrMat = new double[M.getRowDimension()][M.getColumnDimension()];
		
		for(int i = 0; i < M.getRowDimension(); i++) {
			for(int j = 0; j < M.getColumnDimension(); j++) {
//				System.out.println(i+" "+j);
				
				double pm = Math.random();
				if(pm > 0.5) {
					arrMat[i][j] = Math.random() * ran;
				} else {
					arrMat[i][j] = Math.random() * ran * (-1.0);
				}
				
			}
		}
		
		Matrix mat = new Matrix(arrMat);
		return mat;
	}
	
	private static Matrix getErrorSquare1(Matrix R, Matrix R_) {
//		System.out.println(R.getRowDimension()+" "+R.getColumnDimension());
		double[][] arr = new double[R.getRowDimension()][R.getColumnDimension()];
				
		for(int i = 0; i < R.getRowDimension(); i++) {
			for(int j = 0; j < R.getColumnDimension(); j++) {
//				System.out.println(i+ " " + j);
				arr[i][j] = Math.pow(R.get(i, j) - R_.get(i, j),2);
			}
		}
		
		Matrix mat = new Matrix(arr);
		return mat;
	}
	
	private static Matrix getErrorSquare(double beta, Matrix tempE, Matrix P, Matrix Q, Double[] uBias, Double[] iBias) {
		double[][] arr = new double[tempE.getRowDimension()][tempE.getColumnDimension()];
		
		double norm = (beta/2.0) * (P.norm2() + Q.norm2());
		
		for(int i = 0; i < tempE.getRowDimension(); i++) {
			for(int j = 0; j < tempE.getColumnDimension(); j++) {
//				System.out.println(i+" " +j);
				arr[i][j] = tempE.get(i, j) + norm + (beta/2.0) * (Math.pow(uBias[i], 2) + Math.pow(iBias[j], 2));
			}
		}
		
		Matrix mat = new Matrix(arr);
		return mat;
	}
	
	private static double getError(Matrix R, Matrix E) {
		double error = 0d;
		
		for(int i = 0; i < E.getRowDimension(); i++) {
			for(int j = 0; j < E.getColumnDimension(); j++) {
				if(R.get(i, j) > 0d) {
					error += E.get(i, j);
				}
			}
		}
		
		return error;
	}
	
	private static Matrix getSqrtE(Matrix E) {
		double[][] arr = new double[E.getRowDimension()][E.getColumnDimension()];
		
		for(int i = 0; i < E.getRowDimension(); i++) {
			for(int j = 0; j < E.getColumnDimension(); j++) {
				arr[i][j] = Math.sqrt(E.get(i, j));
			}
		}
		
		Matrix mat = new Matrix(arr);
		return mat;
	}
	
	private static double getGlobalAverage(Matrix R) { 
		double mu = 0d;
		int count = 0;
		
		for(int i = 0; i < R.getRowDimension(); i++) {
			for(int j = 0; j < R.getColumnDimension(); j++) {
				if(R.get(i, j) > 0d) {
					count++;
					mu += R.get(i, j);
				}
			}
		}
		
		mu /= (double) count;
		
		return mu;
	}
	
	private static Matrix updateP(double alpha, double beta, Matrix sqrtE, Matrix P, Matrix Q, Matrix R) {
		double[][] tempP = new double[P.getRowDimension()][P.getColumnDimension()];

		for(int i = 0; i < P.getRowDimension(); i++) {
			for(int j = 0; j < sqrtE.getColumnDimension(); j++) {
				if(R.get(i, j) > 0d) {
					for(int k = 0; k < P.getColumnDimension(); k++) {
						tempP[i][k] = P.get(i, k) + alpha * (2 * sqrtE.get(i,j) * Q.get(k,j) - beta * P.get(i, k));
					}
				}
			}
		}
		
		Matrix newP = new Matrix(tempP);
		return newP;
	}
	
	private static Matrix updateQ(double alpha, double beta, Matrix sqrtE, Matrix P, Matrix Q, Matrix R) {
		double[][] tempQ = new double[Q.getRowDimension()][Q.getColumnDimension()];
		
		
		for(int j = 0; j < Q.getColumnDimension(); j++) {
			for(int i = 0; i < sqrtE.getRowDimension(); i++) {
				if(R.get(i, j) > 0d) { 
					for(int k = 0; k < Q.getRowDimension(); k++) {
						tempQ[k][j] = Q.get(k, j) + alpha * (2 * sqrtE.get(i, j) * P.get(i, k) - beta * Q.get(k, j));
					}
				}				
			}
		}
		
		Matrix newQ = new Matrix(tempQ);
		return newQ;
	}
	
	private static Matrix setMatrix(Matrix M) {
		double[][] arr = new double[M.getRowDimension()][M.getColumnDimension()];
		
		for(int i = 0; i < M.getRowDimension(); i++) {
			for(int j = 0; j < M.getColumnDimension(); j++) {
				arr[i][j] = M.get(i, j);
			}
		}
		
		Matrix mat = new Matrix(arr);
		return mat;
	}
	
	private static Double[] initArray(Double[] arr) {
		for(int i = 0; i < arr.length; i++) {
			arr[i] = 0d;
		}
		
		return arr;
	}
	
	private static Double[] getUserBias(Matrix R, double globalMu) {
		Double[] uTemp = new Double[R.getRowDimension()];
		uTemp = initArray(uTemp);
		
		for(int i = 0; i < R.getRowDimension(); i++) {
			int count = 0;
			for(int j = 0; j < R.getColumnDimension(); j++) {
				if(R.get(i, j)>0d) {
					uTemp[i] += R.get(i, j);
					count++;
				}
			}
			uTemp[i] /= (double) count;
			uTemp[i] -= globalMu;
		}
		
		return uTemp;
	}
	
	private static Double[] getItemBias(Matrix R, double globalMu) {
		Double[] iTemp = new Double[R.getColumnDimension()];
		iTemp = initArray(iTemp);
		
		for(int j = 0; j < R.getColumnDimension(); j++) {
			int count = 0;
			for(int i = 0; i < R.getRowDimension(); i++) {
				if(R.get(i, j)>0d) {
					iTemp[j] += R.get(i, j);
					count++;
				}
			}
			iTemp[j] /= (double) count;
			iTemp[j] -= globalMu;
			if(count==0d) {
//				System.out.println("count is " + count);
//				System.exit(1);
				iTemp[j] = 0d;
			}
		}
		
		return iTemp;
	}
	
	private static void printArray(Object[] arr) {
		for(int i = 0; i < arr.length; i++) {
			System.out.println(arr[i]);
		}
	}
	
	private static Matrix addBias(Matrix R_, Double[] uBias, Double[] iBias, double globalMu) {
		double[][] temp = new double[R_.getRowDimension()][R_.getColumnDimension()];
		
		for(int i = 0; i < R_.getRowDimension(); i++) {
			for(int j = 0; j < R_.getColumnDimension(); j++) {
				temp[i][j] += R_.get(i,j) + uBias[i] + iBias[j] + globalMu;
//				System.out.println(R_.get(i, j)+"\t"+uBias[i]+"\t"+iBias[j]+"\t"+globalMu);
			}
		}
		
		Matrix updatedR_ = new Matrix(temp);
		return updatedR_;
	}
	
	private static Matrix getRoundMatrix(Matrix R_) {
		double[][] temp = new double[R_.getRowDimension()][R_.getColumnDimension()];
		
		for(int i = 0; i < R_.getRowDimension(); i++) {
			for(int j = 0; j < R_.getColumnDimension(); j++) {
				if(R_.get(i, j) < 0d) {
					temp[i][j] = 0d;
				} else {
					temp[i][j] = R_.get(i, j);
				}
			}
		}
		
		Matrix newR_ = new Matrix(temp);
		return newR_;
	}
	
	private static String[][] doubleToString(double[][] arr) {
		String[][] str = new String[arr.length][arr[0].length];
		
		for(int i = 0; i < arr.length; i++) {
			str[i][0] = Integer.toString((int)arr[i][0]);
			str[i][1] = Integer.toString((int)arr[i][1]);
			str[i][2] = Double.toString(arr[i][2]);
		}
		
		return str;
	}
	
	public static void main(String[] args) {
		if(args.length != 6) {
			System.out.println("Input arguments errror!");
			System.out.println("Usage: <# of users> <# of items> <matrix data file> <item info file> <user info file> <test file>");
			System.exit(1);
		}
		
		// required arguments
		String numOfUsers = args[0];
		String numOfItems = args[1];
		String matrixDat = args[2];
		String itemInfoFile = args[3];
		String userInfoFile = args[4];
		String testAnswer = args[5];
		
		double time = System.currentTimeMillis();
		
		// Matrix Factorization parameters
//		int k = 80; 
		double alpha = 0.00002;
		double beta = 0.002;
		
//		String matrixDat = "matrix.dat";	// "\t"
//		String itemDat = "item.dat";	// "|"
//		String userDat = "user.dat";	// "|"
//		String testInput = "test.input";	// "\t"
//		String testAnswer = "real.test";	// "\t"
		
		Matrix matrix = GetFile.getMatrix(matrixDat);
		Matrix R = GetFile.getUserItem(matrix); // user-item matrix
		
		double globalMu = getGlobalAverage(R);
		Double[] uBias = getUserBias(R, globalMu);
		Double[] iBias = getItemBias(R, globalMu);
//		System.out.println(globalMu);
//		printArray(uBias);
//		printArray(iBias);
//		System.exit(1);
		
//		int U = Integer.parseInt(numOfUsers);
//		int I = Integer.parseInt(numOfItems);
		int U = R.getRowDimension();
		int I = R.getColumnDimension();
//		System.out.println(U+" "+I);

		//initialize matrix P and Q
		double[][] arrP = new double[U][k];	// matrix for users
		double[][] arrQ = new double[k][I];	// matrix for items
		
		Matrix P = new Matrix(arrP);
		P = initialMatrix(P);
		
		Matrix Q = new Matrix(arrQ);
		Q = initialMatrix(Q);
		
		Matrix R_ = P.times(Q);
		R_ = addBias(R_, uBias, iBias, globalMu);
		double errorSum = Double.MAX_VALUE;
		
		int count = 0;
		
		double diff = Double.MAX_VALUE;

		do {
			
			// to get differences
			Matrix tempE = getErrorSquare1(R, R_);
			Matrix E = getErrorSquare(beta, tempE, P, Q, uBias, iBias);

			Matrix sqrtE = getSqrtE(E);

			// to update p, q
			Matrix newP = updateP(alpha, beta, sqrtE, P, Q, R);
			Matrix newQ = updateQ(alpha, beta, sqrtE, P, Q, R);
			
			P = setMatrix(newP);
			Q = setMatrix(newQ);
			double oldErrorSum = errorSum;
			errorSum = getError(R, E);
//			System.out.println(count+":\t"+errorSum);
			
			if(oldErrorSum < errorSum) {
//				System.out.println("count: "+count);
//				System.out.println("old E: "+oldErrorSum+"\tE: "+errorSum);
//				GetFile.writeFile(R_, "matrixFactorization");
//				System.out.println("writing done");
//				System.exit(1);
//				System.out.println("done by codition 1");
				break;
			}
			
			R_ = P.times(Q);
			R_ = addBias(R_, uBias, iBias, globalMu);
			
			if(count > 5000) {
//				System.out.println("count: "+count);
//				System.out.println("old E: "+oldErrorSum+"\tE: "+errorSum);
//				GetFile.writeFile(R_, "matrixFactorization");
//				System.out.println("writing done");
//				System.out.println("done by condition 2");
				break;
			}
			
			count++;
			diff = Math.abs(oldErrorSum - errorSum);
		} while(diff > 0.00001);
		
//		F.writeFile(R_);
		

		// for cosine similarity
		String matrixDat2 = matrixDat;	// "\t"
		Matrix matrix2 = CosinePrediction.getMatrix(matrixDat2);
		Matrix userItem = CosinePrediction.getUserItem(matrix2); // user-item matrix
		R_ = getRoundMatrix(R_);
		
		// prediction and to measure algorithm
		double rmse = 0d;
		double mape = 0d;
//		matrixDat = "matrixFactorization";
//		Matrix mat = GetFile.getMatrix(matrixDat);
		Matrix mat = R_;
		Matrix test = GetFile.getMatrix(testAnswer);
//		double[][] resultArr = new double[test.getRowDimension()][2];
		double[][] outputTemp = new double[test.getRowDimension()][3];
		String[][] output = new String[test.getRowDimension()][3];
//		Matrix result = new Matrix(resultArr);
		int missing = 0;
		for(int i = 0; i < test.getRowDimension(); i++) {
			int user = (int)test.get(i, 0) - 1;
			int item = (int)test.get(i, 1) - 1;
			
//			System.out.println(user+"\t"+item);
//			System.out.println(mat.getRowDimension()+"\t"+(user-2));
//			System.out.println(mat.getColumnDimension()+"\t"+(item));
			if(mat.getColumnDimension() <= item) {
				missing++;
			} else {

				double est = mat.get(user,item);
				if(est == 0d) {
	//				System.out.print(est+"\t");
					est = CosinePrediction.prediction((int)test.get(i,0), (int)test.get(i,1), userItem);
	//				System.out.println(est);
				} else if(est > 6.0) {
	//				System.out.print(est+"\t");
					est = CosinePrediction.prediction((int)test.get(i,0), (int)test.get(i,1), userItem);
	//				System.out.println(est);
				}
				
	//			resultArr[i][0] = est;
	//			resultArr[i][1] = test.get(i, 2);
				
				outputTemp[i][0] = test.get(i, 0);
				outputTemp[i][1] = test.get(i, 1);
				outputTemp[i][2] = est;
				
				output[i][0] = Integer.toString((int)test.get(i, 0));
				output[i][1] = Integer.toString((int)test.get(i, 1));
				output[i][2] = Double.toString(est);
				
				if(test.getColumnDimension()>2) {
		//			System.out.println(test.get(i, 2)+"\t"+est);
					rmse += Math.pow(est-test.get(i, 2), 2);
		//			System.out.println(rmse);
					mape += Math.abs(est-test.get(i, 2)) / (double)test.get(i, 2);
				}
			}
		}
		
//		GetFile.printMatrix(R_);
		
		rmse /= (double) (test.getRowDimension()-missing);
		rmse = Math.sqrt(rmse);
		mape /= (double) test.getRowDimension();
		mape *= 100.0;
		System.out.println("RMSE: "+rmse);
//		System.out.println("MAPE: "+mape);
		
//		GetFile.writeFile(result, "result");
		if(missing > 0) {
			output = doubleToString(outputTemp);
		}
		GetFile.writeFile(output, "output");
//		System.out.println("program done. "+(System.currentTimeMillis()-time)+" ms");
	}
}
