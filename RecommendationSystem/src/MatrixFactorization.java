
import java.util.*;
import java.io.*;
import Jama.Matrix;


public class MatrixFactorization {

	private static Matrix initialMatrix(Matrix P) {
//		System.out.println("Row Col: "+P.getRowDimension()+ " " + P.getColumnDimension());
		double[][] arrMat = new double[P.getRowDimension()][P.getColumnDimension()];
		
		for(int i = 0; i < P.getRowDimension(); i++) {
			for(int j = 0; j < P.getColumnDimension(); j++) {
//				System.out.println(i+" "+j);
				double ran = Math.random();
				
				// because real R is very sparse! Real matrix is only 5% rated matrix.
//				if(ran < 0.05) {
					arrMat[i][j] = Math.random();
//				}
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
	
	private static Matrix getErrorSquare(double beta, Matrix tempE, Matrix P, Matrix Q) {
		double[][] arr = new double[tempE.getRowDimension()][tempE.getColumnDimension()];
		
		double norm = (beta/2.0) * (P.norm2() + Q.norm2());
		
		for(int i = 0; i < tempE.getRowDimension(); i++) {
			for(int j = 0; j < tempE.getColumnDimension(); j++) {
//				System.out.println(i+" " +j);
				arr[i][j] = tempE.get(i, j) + norm;
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
	
	private static Matrix updateP(double alpha, double beta, Matrix sqrtE, Matrix P, Matrix Q, Matrix R) {
		double[][] tempP = new double[P.getRowDimension()][P.getColumnDimension()];

		for(int i = 0; i < P.getRowDimension(); i++) {
			for(int j = 0; j < sqrtE.getColumnDimension(); j++) {
				for(int k = 0; k < P.getColumnDimension(); k++) {
					if(R.get(i, j) > 0d) {
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
	
	public static void main(String[] args) {
		// Matrix Factorization parameters
		int k = 10; 
		double alpha = 0.00002;
		double beta = 0.000002;
		
		String matrixDat = "matrix.dat";	// "\t"
//		String itemDat = "item.dat";	// "|"
//		String userDat = "user.dat";	// "|"
//		String testInput = "test.input";	// "\t"
		String testAnswer = "real.test";	// "\t"
		
		Matrix matrix = GetFile.getMatrix(matrixDat);
		Matrix R = GetFile.getUserItem(matrix); // user-item matrix
		
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
		
		double errorSum = Double.MAX_VALUE;
		R_ = P.times(Q);
		int count = 0;

		do {
			// to get differences
			Matrix tempE = getErrorSquare1(R, R_);
			Matrix E = getErrorSquare(beta, tempE, P, Q);

			Matrix sqrtE = getSqrtE(E);

			// to update p, q
			Matrix newP = updateP(alpha, beta, sqrtE, P, Q, R);
			Matrix newQ = updateQ(alpha, beta, sqrtE, P, Q, R);
			
			P = setMatrix(newP);
			Q = setMatrix(newQ);
			
			double oldErrorSum = errorSum;
			errorSum = getError(R, E);
			System.out.println(count+":\t"+errorSum);
			
			if(oldErrorSum < errorSum) {
				System.out.println("count: "+count);
				System.out.println("old E: "+oldErrorSum+"\tE: "+errorSum);
				GetFile.writeFile(R_, "matrixFactorization");
				System.out.println("writing done");
//				System.exit(1);
				break;
			}
			
			R_ = P.times(Q);
			
			if(count > 5000) {
				System.out.println("count: "+count);
				System.out.println("old E: "+oldErrorSum+"\tE: "+errorSum);
				GetFile.writeFile(R_, "matrixFactorization");
				System.out.println("writing done");
				break;
			}
			
			count++;
		} while(errorSum > 0.0001);
		
//		F.writeFile(R_);

		
		// prediction and to measure algorithm
		double rmse = 0d;
		double mape = 0d;
		matrixDat = "matrixFactorization";
		Matrix mat = GetFile.getMatrix(matrixDat);
		Matrix test = GetFile.getMatrix(testAnswer);
		double[][] resultArr = new double[test.getRowDimension()][2];
		Matrix result = new Matrix(resultArr);
		for(int i = 0; i < test.getRowDimension(); i++) {
			int user = (int)test.get(i, 0) - 1;
			int item = (int)test.get(i, 1) - 1;
			
			double est = mat.get(user,item);
			
			resultArr[i][0] = est;
			resultArr[i][1] = test.get(i, 2);
			
//			System.out.println(est+" " + test.get(i, 2));
			rmse += Math.pow(est-test.get(i, 2), 2);
			mape += Math.abs(est-test.get(i, 2)) / (double)test.get(i, 2);
		}
		
		rmse /= (double) test.getRowDimension();
		rmse = Math.sqrt(rmse);
		mape /= (double) test.getRowDimension();
		mape *= 100.0;
		System.out.println("RMSE: "+rmse);
		System.out.println("MAPE: "+mape);
		
		GetFile.writeFile(result, "result");
	}
}
