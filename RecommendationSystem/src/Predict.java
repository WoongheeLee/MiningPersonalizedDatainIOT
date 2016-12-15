// Lee, Woonghee
// Deadline: Dec, 23rd
// Given: user-item matrix, item data, user data
// Goal: prediction user-item
// Output: user id <tab> item id <tab> predicted rating

import java.util.*;
import java.io.*;
import org.jgrapht.graph.*;
import Jama.Matrix;

public class Predict {
	private static Matrix getMatrix(String fName) {
		TreeMap<Integer, ArrayList<String>> temp = new TreeMap<Integer, ArrayList<String>>();
		int key = 0;
		try {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(fName));
			while((line = reader.readLine()) != null) {
				if(line.length() < 2) continue;
				StringTokenizer st = new StringTokenizer(line, "\t");
				ArrayList<String> value = new ArrayList<String>();
				while(st.hasMoreTokens()) {
					value.add(st.nextToken());
				}
				temp.put(key++, value);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		double[][] array = new double[temp.keySet().size()][temp.get(0).size()];
		for(int i = 0; i < key; i++) {
			for(int j = 0; j < temp.get(0).size(); j++) {
				array[i][j] = Double.parseDouble(temp.get(i).get(j));
//				System.out.print(array[i][j]+"\t");
			}
//			System.out.println();
		}
		Matrix mat = new Matrix(array);
		return mat;
	}
	
	private static TreeMap<Integer,ArrayList<String>> getData(String fName) {
		TreeMap<Integer,ArrayList<String>> data = new TreeMap<Integer,ArrayList<String>>();
		
		try {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(fName));
			while((line = reader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line, "|");
				int key = Integer.parseInt(st.nextToken());
				
				ArrayList<String> value = new ArrayList<String>();
				while(st.hasMoreTokens()) {
					value.add(st.nextToken());
				}
				
				data.put(key, value);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return data;
	}

	private static void printMatrix(Matrix matrix) {
		System.out.println(matrix.getRowDimension()+"\t"+matrix.getColumnDimension());
		for(int i = 0; i < matrix.getRowDimension(); i++) {
			System.out.print(i+":\t");
			for(int j = 0; j < matrix.getColumnDimension(); j++) {
				System.out.print(matrix.get(i, j)+"\t");
			}
			System.out.println();
		}
		System.out.println("This matrix has "+matrix.getRowDimension()+" rows and "+matrix.getColumnDimension()+" columns.");
	}
	
	private static void printData(TreeMap<Integer,ArrayList<String>> data) {
		Iterator<Integer> itr = data.keySet().iterator();
		while(itr.hasNext()) {
			int key = itr.next();
			System.out.println(key+"\t"+data.get(key));
		}
		System.out.println("This data has "+data.keySet().size()+" tuples.");
	}
	
	private static void printGenre(TreeMap<Integer,ArrayList<String>> data) {
		Iterator<Integer> itr = data.keySet().iterator();
		while(itr.hasNext()) {
			int key = itr.next();
			ArrayList<String> list = data.get(key);
			for(int i = 3; i < list.size(); i++) {
				System.out.print(list.get(i)+"\t");
			}
			System.out.println();
		}
	}
	
	private static Matrix getGenre(TreeMap<Integer,ArrayList<String>> data) {
		Iterator<Integer> itr = data.keySet().iterator();
		double[][] temp = new double[data.keySet().size()][data.get(1).size()-3];
//		System.out.println(data.get(1).size()-3);
		int count = 0;
		while(itr.hasNext()) {
			int key = itr.next();
			ArrayList<String> list = data.get(key);
			for(int i = 3; i < list.size(); i++) {
//				System.out.print(temp[count][i-3]+"\t");
//				System.out.print(i-3+"\t");
				temp[count][i-3] = Double.parseDouble(list.get(i));
			}
//			System.out.println();
			count++;
		}
		
		Matrix genre = new Matrix(temp);
		return genre;
	}
	
	private static Matrix getReleaseYear(TreeMap<Integer,ArrayList<String>> data) {
		Iterator<Integer> itr = data.keySet().iterator();
		double[][] temp = new double[data.keySet().size()][2];
		int count = 0;
		while(itr.hasNext()) {
			int key = itr.next();
			ArrayList<String> list = data.get(key);
			
			String str = list.get(0);
//			System.out.println(count+":\t"+str);
			if(str.equals("unknown")) {
				temp[count][0] = 0d;
			} else {
				String[] arr = str.split("\\(");
				String year = "";
				for(int i = 0; i < arr.length; i++) {
					String[] arr2 = arr[i].split("\\)");
					for(int j = 0; j < arr2.length; j++) {
						if(arr2[j].length()==4) {
							year = arr2[j];
						}
					}
				}
				temp[count][0] = Double.parseDouble(year);
//				System.out.println(temp[count][0]);
			}
		
			str = list.get(1);
			if (str.length() < 2) {
				
			} else {
				String[] arr = str.split("-");
				temp[count][1] = Double.parseDouble(arr[2]);
//				System.out.println(temp[count][1]);
			}
			count++;
		}
		
		Matrix releaseYear = new Matrix(temp);
		return releaseYear;
	}
	
	private static Matrix getUserItem(Matrix mat) {
		double M = 0;
		double N = 0;
		for(int i = 0; i < mat.getRowDimension(); i++) {
			if(M < mat.get(i, 0)) {
				M = mat.get(i, 0);
			}
			if(N < mat.get(i, 1)) {
				N = mat.get(i, 1);
			}
		}
		
		double[][] temp = new double[(int)M][(int)N];
		for(int i = 0; i < mat.getRowDimension(); i++) {
			temp[(int)mat.get(i,0)-1][(int)mat.get(i,1)-1] = mat.get(i, 2);
//			System.out.println((mat.get(i, 0)-1) + "\t"+(mat.get(i, 1)-1)+"\t"+mat.get(i, 2));
		}
		
		Matrix userItem = new Matrix(temp);
//		System.out.println(userItem.getRowDimension()+ "\t"+ userItem.getColumnDimension());
		return userItem;
	}
	
	private static Matrix getMatrixMerge(Matrix releaseYear, Matrix genre) {
		double[][] temp = new double[releaseYear.getRowDimension()+genre.getRowDimension()][releaseYear.getRowDimension()+genre.getColumnDimension()];

		for(int i = 0; i < genre.getRowDimension(); i++) {
			for(int j = 0; j < releaseYear.getColumnDimension(); j++) {
				temp[i][j] = releaseYear.get(i, j);
			}
			for(int j = 0; j < genre.getColumnDimension(); j++) {
//				System.out.print(genre.get(i, j)+"\t");
				temp[i][j+2] = genre.get(i, j);
			}
//			System.out.println();
		}
		
		Matrix mat = new Matrix(temp);
		return mat;
	}

	private static double getCosineSimilarity(int activeUser, int dataUser, Matrix mat) {
		double similarity = 0d;
		
		double AB = 0d;
		double A = 0d;
		double B = 0d;
		
		int count = 0;
		for(int i = 0; i < mat.getColumnDimension(); i++) {
			if(mat.get(activeUser-1, i)>0d && mat.get(dataUser, i)>0d) {
				AB += mat.get(activeUser-1, i)*mat.get(dataUser, i);
				A += Math.pow(mat.get(activeUser-1, i), 2);
				B += Math.pow(mat.get(dataUser, i),2);
				count++;
			}
		}
		
		A = Math.sqrt(A);
		B = Math.sqrt(B);
		
		similarity = AB / (A*B);
		if(count==0)similarity=0d;
		return similarity;
	}
	
	private static double getAdjCosSim(int activeItem, int dataItem, Matrix mat) {
		double similarity = 0d;
		
		double AB = 0d;
		double A = 0d;
		double B = 0d;
		
		for(int i = 0; i < mat.getRowDimension(); i++) {
			AB += mat.get(i, activeItem-1)*mat.get(i, dataItem);
			A += Math.pow(mat.get(i, activeItem-1), 2);
			B += Math.pow(mat.get(i, dataItem), 2);
		}
		
		A = Math.sqrt(A);
		B = Math.sqrt(B);
		
		similarity = AB / (A*B);
		
		return similarity;
	}
	
	
//	private static void prediction(Matrix test, Matrix userItem) {
//		for(int i = 0; i < test.getRowDimension(); i++) {
//			int activeUser = (int)test.get(i, 0);
//			int itemID = (int)test.get(i, 1);
//						
//			double avgRatingA = 0d;
//			int count = 0;
//			for(int j = 0; j < userItem.getColumnDimension(); j++) {
//				if(userItem.get(activeUser-1, j)!=0d) {
//					avgRatingA += userItem.get(activeUser-1,j);
//					count++;
//				}
//			}
//			avgRatingA /= count;
//			
//			double sumSimilarity = 0d;
//			double sumSimRatingB = 0d;
//			for(int j = 0; j < userItem.getRowDimension(); j++) {
//				int dataUser = j;
//				if(activeUser-1 != j) {
////					System.out.println(getCosineSimilarity(activeUser, dataUser, userItem));
//					sumSimilarity += getCosineSimilarity(activeUser, dataUser, userItem);
//					
//					double avgRatingB = 0d;
//					int countB = 0;
//					for(int k = 0; k < userItem.getColumnDimension(); k++) {
//						if(userItem.get(j, k)==0d)continue;
//						avgRatingB += userItem.get(j, k);
//						countB++;
//					}
//					avgRatingB /= countB; // userItem.getColumnDimension();
//					if(userItem.get(j, itemID-1) > 0d) {		
//						sumSimRatingB += getCosineSimilarity(activeUser, dataUser, userItem)*(avgRatingB-userItem.get(j, itemID-1));
//					}
//				}
//			}
//			
//			double pred = avgRatingA + sumSimRatingB / sumSimilarity;
//
//			System.out.println(activeUser+"\t"+itemID+"\t"+pred);
//		}
//	}
	
//	private static void prediction(int activeUser, Matrix test, Matrix userItem) {
//		int itemID = (int)test.get(activeUser-1, 1);
//		
//		double avgRatingA = 0d;
//		int count = 0;
//		for(int j = 0; j < userItem.getColumnDimension(); j++) {
//			if(userItem.get(activeUser-1, j)!=0d) {
//				avgRatingA += userItem.get(activeUser-1,j);
//				count++;
//			}
//		}
//		avgRatingA /= count;
//		
//		double sumSimilarity = 0d;
//		double sumSimRatingB = 0d;
//		for(int j = 0; j < userItem.getRowDimension(); j++) {
//			int dataUser = j;
//			if(activeUser-1 != j) {
////				System.out.println(getCosineSimilarity(activeUser, dataUser, userItem));
//				sumSimilarity += getCosineSimilarity(activeUser, dataUser, userItem);
//				
//				double avgRatingB = 0d;
//				for(int k = 0; k < userItem.getColumnDimension(); k++) {
//					avgRatingB += userItem.get(j, k);
//				}
//				avgRatingB /= userItem.getColumnDimension();
//				
//				sumSimRatingB += getCosineSimilarity(activeUser, dataUser, userItem)*(avgRatingB-userItem.get(j, itemID-1));
//			}
//		}
//		
//		double pred = avgRatingA + sumSimRatingB / sumSimilarity;
//
//		System.out.println(activeUser+"\t"+itemID+"\t"+pred);
//	}
	
//	private static int prediction(int activeUser, int itemID, Matrix userItem) {
//		double avgRatingA = 0d;
//		int count = 0;
//		for(int j = 0; j < userItem.getColumnDimension(); j++) {
//			if(userItem.get(activeUser-1, j)!=0d) {
//				avgRatingA += userItem.get(activeUser-1,j);
//				count++;
//			}
//		}
//		avgRatingA /= count;
//		
//		double sumSimilarity = 0d;
//		double sumSimRatingB = 0d;
//		for(int j = 0; j < userItem.getRowDimension(); j++) {
//			int dataUser = j;
//			if(activeUser-1 != j) {
////				System.out.println(getCosineSimilarity(activeUser, dataUser, userItem));
//				sumSimilarity += getCosineSimilarity(activeUser, dataUser, userItem);
//				
//				double avgRatingB = 0d;
//				for(int k = 0; k < userItem.getColumnDimension(); k++) {
//					avgRatingB += userItem.get(j, k);
//				}
//				avgRatingB /= userItem.getColumnDimension();
//				
//				sumSimRatingB += getCosineSimilarity(activeUser, dataUser, userItem)*(avgRatingB-userItem.get(j, itemID-1));
//			}
//		}
//		
//		double pred = avgRatingA + sumSimRatingB / sumSimilarity;
//
//		int result = Math.round((float)pred);
////		System.out.println(activeUser+"\t"+itemID+"\t"+result);
//		return result;
//	}
	
//	private static double predictionItemD(int activeUser, int itemID, Matrix adjUserItem, Matrix userItem) {
//		double pred = 0d;
//		double sumSim = 0d;
//		double sumRatingSim = 0d;
//		
//		for(int i = 0; i < adjUserItem.getColumnDimension(); i++) {
//			if(userItem.get(activeUser-1,i) > 0d) {
////				System.out.println(i+1+": "+userItem.get(activeUser-1,i));
//				double sim = getAdjCosSim(itemID, i, adjUserItem);
//				if(i != itemID-1) {
//					sumRatingSim = (sim*userItem.get(activeUser-1, i));
//					sumSim += sim;
//				}
//			}
//		}
//		
//		pred = sumRatingSim / sumSim;
//		return pred;
//	}
	
	private static double predictionD(int activeUser, int itemID, Matrix userItem) {
		double avgRatingA = 0d;
		int count = 0;
		for(int j = 0; j < userItem.getColumnDimension(); j++) {
			if(userItem.get(activeUser-1, j)!=0d) {
				avgRatingA += userItem.get(activeUser-1,j);
				count++;
			}
		}
		avgRatingA /= count;
//		System.out.println(avgRatingA);
		
		double sumSimilarity = 0d;
		double sumSimRatingB = 0d;
		for(int j = 0; j < userItem.getRowDimension(); j++) {
			int dataUser = j;
			if(activeUser-1 != j) {
//				System.out.println("cosine similarity:\t"+getCosineSimilarity(activeUser, dataUser, userItem));
				if(getCosineSimilarity(activeUser, dataUser, userItem) > 0.9999) {
//				if(userItem.get(dataUser, itemID-1) > 0d) {	// useless!!!
					sumSimilarity += getCosineSimilarity(activeUser, dataUser, userItem);

					double avgRatingB = 0d;
					for(int k = 0; k < userItem.getColumnDimension(); k++) {
						avgRatingB += userItem.get(j, k);
					}
					avgRatingB /= userItem.getColumnDimension();
					
					sumSimRatingB += getCosineSimilarity(activeUser, dataUser, userItem)*(avgRatingB-userItem.get(j, itemID-1));
				}
			}
		}
		
		if(sumSimRatingB == 0d) {
			return Math.random() * 5d;
		}
		
		double pred = avgRatingA + sumSimRatingB / sumSimilarity;

		return Math.round(pred);
//		return pred;
	}
	
	private static Matrix getAdjustedMatrix(Matrix mat) {
		double[][] temp = new double[mat.getRowDimension()][mat.getColumnDimension()];
		
		for(int i = 0; i < mat.getRowDimension(); i++) {
			double avg = 0d;
			for(int j = 0; j < mat.getColumnDimension(); j++) {
				avg += mat.get(i, j);
			}
			avg /= mat.getColumnDimension();
			
			double test = 0d;
			for(int j = 0; j < mat.getColumnDimension(); j++) {
				temp[i][j] = mat.get(i, j) - avg;
				test += temp[i][j];
			}
			if(test > 0.0000000001)
				System.out.println(test);
		}
		
		Matrix adjMat = new Matrix(temp);
		return adjMat;
	}

	public static void main(String[] args) {
//		if(args.length != 6) {
//			System.out.println("Usage: <# of users> <# of items> <matrix data file> <item info file> <user info file> <test file>");
//			System.exit(1);
//		}
		
		String matrixDat = "matrix.dat";	// "\t"
		String itemDat = "item.dat";	// "|"
		String userDat = "user.dat";	// "|"
		String testInput = "test.input";	// "\t"
		String testAnswer = "real.test";	// "\t"
		
		Matrix matrix = getMatrix(matrixDat);
		Matrix userItem = getUserItem(matrix); // user-item matrix
		
		Matrix test = getMatrix(testInput);
		TreeMap<Integer,ArrayList<String>> item = getData(itemDat);
		TreeMap<Integer,ArrayList<String>> user = getData(userDat);
		
		Matrix genre  = getGenre(item);
		Matrix releaseYear = getReleaseYear(item);
		
		Matrix itemFeature = getMatrixMerge(releaseYear, genre);
		
		Matrix adjUserItem = getAdjustedMatrix(userItem);
		
		Matrix testCorrect = getMatrix(testAnswer);
		
//		printMatrix(adjUserItem);
//		printMatrix(matrix);
//		printMatrix(userItem);
//		printMatrix(test);
//		printData(item);
//		printGenre(item);
//		printMatrix(releaseYear);
//		printMatrix(itemFeature);
//		printData(user);
//		printMatrix(genre);
//		printMinDist(genre);
		
		// prediction by user-based similarity
		// measurement: MAPE, RMSE
		// DO THAT WITH ADJUSTED USER-ITEM MATRIX!!!!!!!!!!!!!!!!!!!!!!!!!
		double mape = 0d;
		double rmse = 0d;
		for(int i = 0; i < testCorrect.getRowDimension(); i++) {
			int real = (int)testCorrect.get(i, 2);
			double pred = predictionD((int)testCorrect.get(i,0), (int)testCorrect.get(i,1), userItem);
			mape += Math.abs(pred-(double)real)/(double)real;
			rmse += Math.pow(pred-(double)real, 2);
			
//			System.out.println((i+1)+"\t"+(int)matrix.get(i,1)+"\t"+real+"\t"+pred);
			System.out.println(i+": mape now: "+Math.round((mape/(double)(i+1)*100))+"%");
		}
		mape /= testCorrect.getRowDimension();
		rmse /= testCorrect.getRowDimension();
		rmse = Math.sqrt(rmse);
		
//		System.out.println((double)count / (double)matrix.getRowDimension());
		System.out.println("mape: " + (mape*100.0)+"%");
		System.out.println("rmse: "+ rmse);

	}
}