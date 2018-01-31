package Homework2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class calibrate {
	
	public static void main(String[] args) throws IOException {
		
		int [][] gameboard = new int[][]{{0,1,2,3,4,5,6,7,8,9},{0,1,2,3,4,5,6,7,8,9},
				{0,1,2,3,4,5,6,7,8,9},{0,1,2,3,4,5,6,7,8,9},{0,1,2,3,4,5,6,7,8,9},
				{0,1,2,3,4,5,6,7,8,9},{0,1,2,3,4,5,6,7,8,9},{0,1,2,3,4,5,6,7,8,9},
				{0,1,2,3,4,5,6,7,8,9},{0,1,2,3,4,5,6,7,8,9},
				};
				
		int m = 0;
		
		while(m<10000000){
			for(int i = 0; i < 10; i++)
			{
				for(int j = 0; j < 10; j++)
					
					gameboard[i][j]=1+2;
			}
			m++;
		}
		
		File fout = new File("calibrate.txt");
		
		FileOutputStream fos = new FileOutputStream(fout);
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		
		bw.write(""+(getCpuTime()/10000000));
		
		bw.close();
	}	
	
	public static long getCpuTime( ) {
		
	    ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
	    
	    return bean.isCurrentThreadCpuTimeSupported( ) ?
	    		
	        bean.getCurrentThreadCpuTime( ) : 0L;
	}	

}