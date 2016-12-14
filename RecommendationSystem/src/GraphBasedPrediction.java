// Lee, Woonghee
// Deadline: Dec, 23rd
// Given: user-item matrix, item data, user data
// Goal: prediction user-item
// Output: user id <tab> item id <tab> predicted rating

import java.util.*;
import java.io.*;
import org.jgrapht.graph.*;
import Jama.Matrix;

public class GraphBasedPrediction {
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
	
	// top-2 cosine similar
	private static String getTopSimilar(Matrix mat, int row) {
		double sim = 0d;
		int item = 0;
		double sim2 = 0d;
		int item2 = 0;
		
		for(int i = 0; i < mat.getRowDimension(); i++) {
			double temp = 0d;
			double AB = 0d;
			double A = 0d;
			double B = 0d;
			
			if(i != row) {
				for(int j = 0; j < mat.getColumnDimension(); j++) {
					AB +=  mat.get(row, j) * mat.get(i, j);
					A += Math.pow(mat.get(row, j), 2);
					B += Math.pow(mat.get(i, j), 2);
				}
				
				temp = AB / (Math.sqrt(A) * Math.sqrt(B));
				
				if(temp > sim) {
//					sim2 = sim;
//					item2 = item;
					sim = temp;
					item = i+1;
				}
			}	
		}
		
		return item+","+sim;//+","+item2+","+sim2;
	}
	
	// top-2 minimum distance 
	private static String getMinDist(Matrix mat, int row) {
		double sim = Double.MAX_VALUE;
		int item = 0;
		
//		double sim2 = 0d;
//		int item2 = 0;
		
		for(int i = 0; i < mat.getRowDimension(); i++) {
			double temp = 0d;
			if(i != row) {
				for(int j = 0; j < mat.getColumnDimension(); j++) {
					temp += Math.pow(mat.get(row, j) - mat.get(i, j), 2);
				}
				temp = Math.sqrt(temp);
//				System.out.println(i + ": "+temp);
				
				
				if(temp < sim) {
//					sim2 = sim;
//					item2 = item;
					
					sim = temp;
					item = i+1;
//					System.out.println("item: "+item+"\tsim = "+sim);
				}
			}
		}
		
//		System.out.println("The most similar item with "+row+" is " + item +", and the similarity is " +sim);
//		System.out.println();
		
		return item+","+sim;//+","+item2+","+sim2;
	}
	
	private static void printMinDist(Matrix mat) {
		for(int i = 0; i < mat.getRowDimension(); i++) {
			String minDist = getMinDist(mat, i);
			String[] arr = minDist.split(",");
			System.out.println((i+1)+"-th row is similar with "+arr[0]+", and the similarity is "+arr[1]+".");
		}
	}
	
	private static void printTopSimilar(Matrix mat) {
		for(int i = 0; i < mat.getRowDimension(); i++) {
			String minDist = getTopSimilar(mat, i);
			String[] arr = minDist.split(",");
			System.out.println((i+1)+"-th row is similar with "+arr[0]+", and the similarity is "+arr[1]+".");
		}
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
	
	// to get user graph which node is user and weight is similarity between users
	// top two similar users are neighbor
	private static SimpleWeightedGraph<Double,DefaultWeightedEdge> getUserGraph(Matrix userItem) {
		SimpleWeightedGraph<Double,DefaultWeightedEdge> userGraph = new SimpleWeightedGraph<Double,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		
		for(int i = 0; i < userItem.getRowDimension(); i++) {
//			System.out.println(getMinDist(userItem,i));
			String[] arr = getTopSimilar(userItem,i).split(",");
			
			//top-1 similar neighbor
			Double v1 = (double)(i+1);
			Double v2 = Double.parseDouble(arr[0]);
//			if(v1==v2)System.exit(1);
			Double w1 = Double.parseDouble(arr[1]); // cosine similarity
			userGraph.addVertex(v1);
			userGraph.addVertex(v2);
			userGraph.addEdge(v1, v2);
			DefaultWeightedEdge e = userGraph.getEdge(v1, v2);
			userGraph.setEdgeWeight(e, w1);
			
//			//top-2 similar neighbor
//			Double v3 = Double.parseDouble(arr[2]);
//			Double w2 = Double.parseDouble(arr[3]); // cosine similarity
//			userGraph.addVertex(v3);
//			userGraph.addEdge(v1, v3);
//			e = userGraph.getEdge(v1, v3);
//			userGraph.setEdgeWeight(e, w2);
		}
		
		return userGraph;
	}
	
	private static SimpleWeightedGraph<Double,DefaultWeightedEdge> getGenreGraph(Matrix genre) {
		SimpleWeightedGraph<Double,DefaultWeightedEdge> genreGraph = new SimpleWeightedGraph<Double,DefaultWeightedEdge>(DefaultWeightedEdge.class);

		for(int i = 0; i < genre.getRowDimension(); i++) {
			String[] arr = getMinDist(genre,i).split(",");
			
			Double v1 = (double)(i+1);
			Double v2 = Double.parseDouble(arr[0]);
			Double w1 = 1.0/Double.parseDouble(arr[1]); // distance
			genreGraph.addVertex(v1);
			genreGraph.addVertex(v2);
			genreGraph.addEdge(v1, v2);
			DefaultWeightedEdge e = genreGraph.getEdge(v1, v2);
			genreGraph.setEdgeWeight(e, w1);
			
//			Double v3 = Double.parseDouble(arr[2]);
//			Double w2 = 1.0/Double.parseDouble(arr[3]); // distance
//			genreGraph.addVertex(v3);
//			genreGraph.addEdge(v1, v3);
//			e = genreGraph.getEdge(v1, v3);
//			genreGraph.setEdgeWeight(e, w2);
		}
		
		return genreGraph;
	}
		
	private static void outputGraph(SimpleWeightedGraph<Double,DefaultWeightedEdge> userGraph, String fileName) {
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName+".csv"));
			
			Iterator<DefaultWeightedEdge> itr = userGraph.edgeSet().iterator();
			while(itr.hasNext()) {
				DefaultWeightedEdge e = itr.next();
				String v1 = userGraph.getEdgeSource(e).toString();
				String v2 = userGraph.getEdgeTarget(e).toString();
				
				bw.write(v1+","+v2+"\n");
				bw.flush();
			}
			bw.close();			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private static HashSet<Double> getNeighbor(Double node, SimpleWeightedGraph<Double,DefaultWeightedEdge> graph) {
		HashSet<Double> neighbor = new HashSet<Double>();
		
		Iterator<DefaultWeightedEdge> itr3 = graph.edgesOf(node).iterator();
		while(itr3.hasNext()) {
			DefaultWeightedEdge e = itr3.next();
//			System.out.println(graph.getEdgeSource(e)+" "+graph.getEdgeTarget(e));
			if(Double.parseDouble(graph.getEdgeSource(e).toString()) == node) {
				neighbor.add(graph.getEdgeTarget(e));
//				System.out.println(node+" "+graph.getEdgeTarget(e));
			} 

			if(Double.parseDouble(graph.getEdgeTarget(e).toString()) == node){
				neighbor.add(graph.getEdgeSource(e));
//				System.out.println(node+" "+graph.getEdgeSource(e));
			}
		}
		
		if(neighbor.contains(node)) {
			System.out.println(node+" "+neighbor);
			System.exit(1);
		}
		
		return neighbor;
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
		
		Matrix matrix = getMatrix(matrixDat);
		Matrix userItem = getUserItem(matrix);
		SimpleWeightedGraph<Double,DefaultWeightedEdge> userGraph = getUserGraph(userItem);
		
		Matrix test = getMatrix(testInput);
		TreeMap<Integer,ArrayList<String>> item = getData(itemDat);
		TreeMap<Integer,ArrayList<String>> user = getData(userDat);
		
		Matrix genre  = getGenre(item);
		SimpleWeightedGraph<Double,DefaultWeightedEdge> genreGraph = getGenreGraph(genre);
		
//		printMatrix(matrix);
//		printMatrix(userItem);
//		printMatrix(test);
//		printData(item);
//		printGenre(item);
//		printData(user);
//		printMatrix(genre);
//		printMinDist(genre);
//		printTopSimilar(userItem);
		
		// to file output userGraph and genreGraph
//		outputGraph(userGraph, "userGraph");
//		outputGraph(genreGraph, "genreGraph");
		
//		Iterator<Double> itr = userGraph.vertexSet().iterator();
//		int count = 0;
//		while(itr.hasNext()) {
//			Double node = itr.next();
//			if(userGraph.degreeOf(node)>0) {
////				System.out.println(node+"\t"+userGraph.degreeOf(node));
//				System.out.println(node+" "+getNeighbor(node, userGraph));
//				count++;
//			}
//		}
////		System.out.println(count);
//		System.out.println();
		
//		Iterator<Double> itr = genreGraph.vertexSet().iterator();
//		while(itr.hasNext()) {
//			Double node = itr.next();
//			if(genreGraph.degreeOf(node)==4) {
//				System.out.println(node+" "+genreGraph.degreeOf(node));
//			}
//		}
		
		System.out.println("userGraph has "+userGraph.vertexSet().size()+" vertices.");
		System.out.println("userGraph has "+userGraph.edgeSet().size()+" edgs.");
		System.out.println("userGraph's density is "+((double)userGraph.edgeSet().size() / (double) userGraph.vertexSet().size())+".");
		System.out.println();
		System.out.println("genreGraph has "+genreGraph.vertexSet().size()+" vertices.");
		System.out.println("genreGraph has "+genreGraph.edgeSet().size()+" edges.");
		System.out.println("genreGraph's density is "+((double)genreGraph.edgeSet().size() / (double)genreGraph.vertexSet().size())+".");
		
		
		for(int i = 0; i < test.getRowDimension(); i++) {
			Double activeUser = test.get(i,0);
			Double queryItem = test.get(i, 1);
			getNeighbor(activeUser, userGraph);
			System.out.println(activeUser+"\t"+getNeighbor(activeUser,userGraph));
//			if(! userGraph.containsVertex(activeUser) ) {
//				System.out.println(activeUser+" "+userGraph.containsVertex(activeUser));
//			}
		}
		
	}
}