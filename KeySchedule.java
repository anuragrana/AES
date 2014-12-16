

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class KeySchedule {
	
	SBox sb=new SBox();
	OtherFunctions of=new OtherFunctions();
	String expandedKey[][]=new String[4][60];
	String temp[][]=new String[4][1];
	void keySchedule(String keyfile)
	{
		try
		{
			//reading key from file
		URL url = AES.class.getResource(keyfile);  //for relative path 
	    File file = new File(url.getPath());
		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine =  br.readLine();
		if(strLine.length()!=64)
			{
			System.out.println("Key length is not 256 bit. Program is terminating......");
			System.exit(0);
			}
		// converting the stream of key into a matrix
		readKeyIntoMatrix(strLine);
		
		/*Printing cipher key matrix
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<8;j++)
			{
				System.out.print(expandedKey[i][j]+" ");
			}
			System.out.println();
		}
		*/	
		
		//expanding the key
		expandTheKey();

		br.close();
		}//try closing here
		
		catch(Exception e)
		{
			System.out.println("Exception in KeySchedule.");
			e.printStackTrace();
		}
		
	}
	
	void readKeyIntoMatrix(String str)
	{
		int k=0;
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<4;j++)
			{
				expandedKey[j][i]=str.charAt(k++)+""+str.charAt(k++);
			}
		}
	}
	
	void expandTheKey()
	{
		try
		{
		for(int i=8;i<60;i++)
		 {
			
			//Assigning the W[i-1] to temp
			temp[0][0]=expandedKey[0][i-1];
			temp[1][0]=expandedKey[1][i-1];
			temp[2][0]=expandedKey[2][i-1];
			temp[3][0]=expandedKey[3][i-1];
			
			//printTemp("After temp = w[i-1] \n");
			
			if(i%8==0)
			{
				rotWord();
				//printTemp("After rotWord "+i+"\n");
				subWord();
				//printTemp("After subWord "+i+"\n");
				xorWithRcon((i/8)-1);
				//printTemp("After xorWithRcon "+i+" \n");
			}
			
			if(i%8==4)
			{
				subWord();
				//printTemp("After subWord "+i+"\n");
			}
			
			xorWithW(i-8);
			//printTemp("After xorWithW "+i+" \n");
			
			expandedKey[0][i]=temp[0][0];
			expandedKey[1][i]=temp[1][0];
			expandedKey[2][i]=temp[2][0];
			expandedKey[3][i]=temp[3][0];	
		 }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//printKeyFull();
		
	}
	
	void rotWord()
	{
		String t=temp[3][0];
		temp[3][0]=temp[0][0];
		temp[0][0]=temp[1][0];
		temp[1][0]=temp[2][0];
		temp[2][0]=t;
	}
	
	void subWord()
	{
		temp[0][0]=sb.sBox(temp[0][0]);
		temp[1][0]=sb.sBox(temp[1][0]);
		temp[2][0]=sb.sBox(temp[2][0]);
		temp[3][0]=sb.sBox(temp[3][0]);
	}
	
	void xorWithRcon(int i)
	{
		String rCon[][] = new String[][]
			{
				{"01","02","04","08","10","20","40"},
				{"00","00","00","00","00","00","00"},
				{"00","00","00","00","00","00","00"},
				{"00","00","00","00","00","00","00"},
			};
		
		temp[0][0]=of.xorStringWithString(temp[0][0], rCon[0][i]);
		temp[1][0]=of.xorStringWithString(temp[1][0], rCon[1][i]);
		temp[2][0]=of.xorStringWithString(temp[2][0], rCon[2][i]);
		temp[3][0]=of.xorStringWithString(temp[3][0], rCon[3][i]);	
	}
	
	void xorWithW(int i)
	{
		temp[0][0]=of.xorStringWithString(temp[0][0], expandedKey[0][i]);
		temp[1][0]=of.xorStringWithString(temp[1][0], expandedKey[1][i]);
		temp[2][0]=of.xorStringWithString(temp[2][0], expandedKey[2][i]);
		temp[3][0]=of.xorStringWithString(temp[3][0], expandedKey[3][i]);	
		//printTemp(" "+(i+8)+"---");
	}
	
	void printKeyFull()
	{
		System.out.println("-----------printing key -----------------------");
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<60;j++)
			{
				if(j%4==0)
					System.out.print("  ");
				System.out.print(expandedKey[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	void printTemp(String str)
	{
		System.out.println(str+temp[0][0]+" "+temp[1][0]+" "+temp[2][0]+" "+temp[3][0]);
	}

}
