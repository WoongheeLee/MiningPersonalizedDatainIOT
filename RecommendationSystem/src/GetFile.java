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

public class GetFile {
	public GetFile() {}

	public static void writeFile(String[][] arr, String fileName) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			
			for(int i = 0; i < arr.length; i++) {
				for(int j = 0; j < arr[i].length; j++) {
					if(j == 0) {
						bw.write(arr[i][j]);
						bw.flush();
					} else {
						bw.write("\t"+arr[i][j]);
						bw.flush();
					}
				}
				bw.write("\n");
				bw.flush();
			}
			
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void writeFile(Matrix mat, String fileName) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			
			for(int i = 0; i < mat.getRowDimension(); i++) {
				for(int j = 0; j < mat.getColumnDimension(); j++) {
					if(j == 0) {
						bw.write(Double.toString(mat.get(i,j)));
						bw.flush();
					} else {
						bw.write("\t"+mat.get(i, j));
						bw.flush();
					}
				}
				bw.write("\n");
				bw.flush();
			}
			
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public static Matrix getMatrix(String fName) {
		TreeMap<Integer, ArrayList<String>> temp = new TreeMap<Integer, ArrayList<String>>();
		int key = 0;
		try {
			String line;
			@SuppressWarnings("resource")
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
		
//		System.out.println(temp.keySet().size()+"\t"+temp.get(0).size());
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
	
	public static TreeMap<Integer,ArrayList<String>> getData(String fName) {
		TreeMap<Integer,ArrayList<String>> data = new TreeMap<Integer,ArrayList<String>>();
		
		try {
			String line;
			@SuppressWarnings("resource")
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

	public static void printMatrix(Matrix matrix) {
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
	
	public static void printData(TreeMap<Integer,ArrayList<String>> data) {
		Iterator<Integer> itr = data.keySet().iterator();
		while(itr.hasNext()) {
			int key = itr.next();
			System.out.println(key+"\t"+data.get(key));
		}
		System.out.println("This data has "+data.keySet().size()+" tuples.");
	}
	
	public static Matrix getUserItem(Matrix mat) {
		double M = 0d;
		double N = 0d;
		for(int i = 0; i < mat.getRowDimension(); i++) {
			if(M < mat.get(i, 0)) {
				M = mat.get(i, 0);
//				System.out.println("M: "+M);
			}
			if(N < mat.get(i, 1)) {
				N = mat.get(i, 1);
//				System.out.println("N: "+N);
			}
		}
		double[][] temp = new double[(int)M][(int)N];
//		System.out.println(temp.length+"\t"+temp[0].length);
		for(int i = 0; i < mat.getRowDimension(); i++) {
			temp[(int)mat.get(i,0)-1][(int)mat.get(i,1)-1] = mat.get(i, 2);
//			System.out.println((mat.get(i, 0)-1) + "\t"+(mat.get(i, 1)-1)+"\t"+mat.get(i, 2));
		}
		
		Matrix userItem = new Matrix(temp);
//		System.out.println(userItem.getRowDimension()+ "\t"+ userItem.getColumnDimension());
		return userItem;
	}
}
