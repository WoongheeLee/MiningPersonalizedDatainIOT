// Lee, Woonghee
// Deadline: Dec, 23rd
// Given: user-item matrix, item data, user data
// Goal: prediction user-item
// Output: user id <tab> item id <tab> predicted rating

import java.util.*;
import Jama.Matrix;
import java.io.*;

public class Predict {
	private static Matrix getMatrix(String fName) {
		TreeMap<Integer, ArrayList<String>> temp = new TreeMap<Integer, ArrayList<String>>();
		int key = 0;
		try {
			String line;
			BufferedReader reader = new BufferedReader(new FileReader(fName));
			while((line = reader.readLine()) != null) {
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
		for(int i = 0; i < matrix.getRowDimension(); i++) {
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
	
	private static double getDist(Matrix mat, int row) {
		double sim = Double.MAX_VALUE;
		int item = 0;
		
		for(int i = 0; i < mat.getRowDimension(); i++) {
			double temp = 0d;
			if(i != row) {
				for(int j = 0; j < mat.getColumnDimension(); j++) {
					temp += Math.pow(mat.get(row, j) - mat.get(i, j), 2);
				}
				temp = Math.sqrt(temp);
//				System.out.println(i + ": "+temp);
				
				
				if(temp < sim) {
					sim = temp;
					item = i+1;
//					System.out.println("item: "+item+"\tsim = "+sim);
				}
			}
		}
		
		System.out.println("The most similar item with "+row+" is " + item +", and the similarity is " +sim);
//		System.out.println();
		
		return sim;
	}
	
	public static void main(String[] args) {
//		if(args.length != 6) {
//			System.out.println("Usage: <# of users> <# of items> <matrix data file> <item info file> <user info file> <test file>");
//			System.exit(1);
//		}
		
		String file1 = "matrix.dat";	// "\t"
		String file2 = "item.dat";	// "|"
		String file3 = "user.dat";	// "|"
		String file4 = "test.input";	// "\t"
		
		Matrix matrix = getMatrix(file1);
		Matrix test = getMatrix(file4);
		TreeMap<Integer,ArrayList<String>> item = getData(file2);
		TreeMap<Integer,ArrayList<String>> user = getData(file3);
		
		Matrix genre  = getGenre(item);
		
		printMatrix(matrix);
//		printMatrix(test);
//		printData(item);
//		printGenre(item);
//		printData(user);
//		printMatrix(genre);
		
		for(int i = 0; i < genre.getRowDimension(); i++) {
//			getDist(genre, i);
		}
	}
}
