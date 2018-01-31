package Homework2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class homework {

	private Scanner s;
	String str = "";
	static int resultIndex,sum = 0;
	static char result[][];
	static int fruitCount = 0;
	static float time = 0;
	static float optime = 0;

	public void openfile(String fileName){
		try{
			s = new Scanner(new File(fileName));
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public char[][] readfile(){

		int rows = s.nextInt();
		int columns = rows;
		int numFruits = s.nextInt();
		time = s.nextFloat();

//		System.out.println(rows);
//		System.out.println(numFruits);
//		System.out.println(time);

		int[] fruitType = new int[11];

		char gameBoard[][] = new char[rows][columns];
		int row = 0;
		int column = 0;
		for(int j = 0;j<rows;j++){
			String str = s.next();
			column = 0;
			for(int i = 0;i<str.length();i++){
				gameBoard[row][column] = str.charAt(i);
				fruitType[Character.getNumericValue(gameBoard[row][column])+1] = 1;
				column++;
			}
			row++;
		}

		for(int i=0;i<fruitType.length;i++){
			if(fruitType[i]==1)
				fruitCount++;
		}
		s.close();
		return gameBoard;
	}

	public static void display_matrix(char[][] matrix, int resultIndex, String output) throws IOException{

		//String output = "output" + fileName + ".txt";
		File fout = new File(output);
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		int row = resultIndex/matrix.length;
		int col = resultIndex%matrix.length;
		char val = (char) (col + 'A');
		int pos = row+1;
		bw.write(val + "" + pos);
		bw.newLine();
		for(int i = 0;i<matrix.length;i++){
			for(int j = 0;j<matrix.length;j++){
				bw.write(matrix[i][j]);
			}
			bw.newLine();
		}
		bw.close();
	}

	public static int DFS(char[][] gameBoard, int row, int col, boolean[][] visited, char value){
		int rowNbr[] = {-1,0,0,1}; //up, left, right, down
		int colNbr[] = {0,-1,1,0};

		visited[row][col] = true;
		sum++;
		for (int k = 0; k < 4; k++){
			int r = row + rowNbr[k], c = col + colNbr[k];
			if((r >= 0) && (r < gameBoard.length) && (c >= 0) && (c < gameBoard.length)){
				if(gameBoard[r][c]==value && !visited[r][c]){
					DFS(gameBoard, r, c, visited, value);

				}
			}
		}
		return sum;
	}

	public static void countClusters(char[][] gameBoard, ArrayList<int[]> clusterInfo) throws IOException{
		boolean visited[][] = new boolean[gameBoard.length][gameBoard.length];
		int count = 0;
		for (int i = 0; i < gameBoard.length; ++i){
			for (int j = 0; j < gameBoard.length; ++j){
				if (gameBoard[i][j]>='0' && gameBoard[i][j]<='9' && !visited[i][j]){
					sum = 0;
					int sizeOfClusters = DFS(gameBoard, i, j, visited,gameBoard[i][j]);
					int info[] = new int[3];
					info[0] = Character.getNumericValue(gameBoard[i][j]);
					info[1] = i*gameBoard.length+j;
					info[2] = sizeOfClusters;
					clusterInfo.add(info);
					Collections.sort(clusterInfo, new Comparator<int[]>() {
						@Override
						public int compare(int[] o1, int[] o2) {
							return (o1[2]>=o2[2] ? -1 : 1);
						}
					});
					count++;
				}
			}
		}
	}

	public static char[][] applyGravity(char[][] placedStar){

		int col = 0;
		while(col<placedStar.length){
			int index = placedStar.length-1;
			for(int i = placedStar.length-1;i>=0;i--){
				if(placedStar[i][col]!='*')
					placedStar[index--][col] = placedStar[i][col];
			}
			for(int i = 0;i<=index;i++){
				placedStar[i][col] = '*';
			}
			col++;
		}

		return placedStar;	
	}

	public static char[][] placeClusterStar(char[][] gameBoard, char value, int row, int col) {

		int rowNbr[] = {-1,0,0,1}; //up, left, right, down
		int colNbr[] = {0,-1,1,0};

		gameBoard[row][col] = '*';

		for (int k = 0; k < 4; k++){
			int r = row + rowNbr[k], c = col + colNbr[k];
			if((r >= 0) && (r < gameBoard.length) && (c >= 0) && (c < gameBoard.length)){
				if(gameBoard[r][c]==value){
					placeClusterStar(gameBoard, value, r, c);

				}
			}
		}
		return gameBoard;
	}

	public static int MaxValue(char[][] gameBoard, int level, int One, int Two, int alpha, int beta, boolean callF) throws IOException{

		if(level==0){
			return One - Two;
		}

		ArrayList<int[]> clusterInfo = new ArrayList<int[]>();
		int playerOne = 0;
		int temp = 0;

		countClusters(gameBoard, clusterInfo);
		if(clusterInfo.size()==0)
			return One - Two;

		for(int i = 0;i<clusterInfo.size();i++){
			char value = Character.forDigit(clusterInfo.get(i)[0], 10);
			int position = clusterInfo.get(i)[1];
			int row = position/gameBoard.length;
			int col = position%gameBoard.length;
			int clusterSize = clusterInfo.get(i)[2];

			playerOne = One + clusterSize*clusterSize;

			char[][] state = new char[gameBoard.length][gameBoard.length];

			for(int k = 0;k<gameBoard.length;k++){
				for(int j = 0;j<gameBoard.length;j++){
					state[k][j] = gameBoard[k][j];
				}
			}

			char[][] placedStar = placeClusterStar(state,value,row,col);
			//display_matrix(placedStar);

			char[][] playedMove = applyGravity(placedStar);
			//display_matrix(playedMove);

			temp = MinValue(playedMove,level-1,playerOne, Two, alpha,beta);
			if(alpha<temp){
				if(callF){
					result=playedMove;	
					resultIndex=position;
				}	
				alpha=temp;
			}
			if(alpha>=beta){
				return beta;
			}

		}

		return alpha;
	}

	public static int MinValue(char[][] gameBoard,int level, int One, int Two, int alpha, int beta) throws IOException{

		ArrayList<int[]> clusterInfo = new ArrayList<int[]>();
		int playerTwo = 0;

		if(level==0){
			return One - Two;
		}

		countClusters(gameBoard, clusterInfo);

		if(clusterInfo.size()==0)
			return One - Two;

		for(int i = 0;i<clusterInfo.size();i++){
			char value = Character.forDigit(clusterInfo.get(i)[0], 10);
			int position = clusterInfo.get(i)[1];
			int row = position/gameBoard.length;
			int col = position%gameBoard.length;
			int clusterSize = clusterInfo.get(i)[2];

			playerTwo = Two + clusterSize*clusterSize;

			char[][] state = new char[gameBoard.length][gameBoard.length];

			for(int k = 0;k<gameBoard.length;k++){
				for(int j = 0;j<gameBoard.length;j++){
					state[k][j] = gameBoard[k][j];
				}
			}

			char[][] placedStar = placeClusterStar(state,value,row,col);
			//			display_matrix(placedStar);

			char[][] playedMove = applyGravity(placedStar);
			//			display_matrix(playedMove);

			beta = Math.min(beta, MaxValue(playedMove,level-1,One, playerTwo, alpha,beta,false));
			if(beta<=alpha){
				return alpha;
			}

		}
		return beta;
	}

	public static long getCpuTime( ) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
		return bean.isCurrentThreadCpuTimeSupported( ) ?
				bean.getCurrentThreadCpuTime( ) : 0L;
	}

	public static double leftTime(int fruits, int clusters, float time, int operations, int level){

		double calculated = Math.pow((fruits*2 + (double)clusters/(fruits*fruits)), level) * time * operations;

		return calculated/1000000;
	}

	public static int leftDepth(int fruits, int clusters, float time, int operations,float availableTime){

		float timeForMove= availableTime;

		if(timeForMove > (leftTime(fruits, clusters, time, operations, 4)))return 4;

		else if(timeForMove > (leftTime(fruits, clusters, time, operations, 3)))return 3;

		else return 2;			
	}

	public void open(String calibrateFile){
		try{
			s = new Scanner(new File(calibrateFile));
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void read(){
		float t = s.nextFloat();
		optime = t;
		s.close();
	}

	public static void main(String[] args) throws IOException {
		
		System.out.println(System.currentTimeMillis()/1000);

		homework hw = new homework();
		ArrayList<int[]> clusterInfo = new ArrayList<int[]>();

//		String fileName = "input" + args[0] + ".txt";
		String fileName = "input1.txt";
		//int level = Integer.parseInt(args[1]);
		
		String calibrateFile = "calibrate.txt";

		ArrayList<int[]> clusterInformation = new ArrayList<int[]>();

		hw.openfile(fileName);
		char[][] gameBoard = hw.readfile();
		
		countClusters(gameBoard, clusterInformation);
		
		hw.open(calibrateFile); 
		hw.read();
		
		int level = leftDepth(fruitCount, clusterInformation.size(), optime, gameBoard.length*gameBoard.length,(time*1000)/2);
		System.out.println("level is " + level);
//		String output = "output" + args[0] +"_"+ level + ".txt";
		String output = "output.txt";
		int x = MaxValue(gameBoard, level, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE,true);
		display_matrix(result,resultIndex,output);
//		System.out.println(getCpuTime()/10000000);
		System.out.println(System.currentTimeMillis()/1000);


	}

}

