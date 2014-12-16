

import java.io.*;
import java.net.URL;

public class AES 
{
	String state[][] =new String[4][4];
	OtherFunctions of=new OtherFunctions();
	SBox sb=new SBox();
	InvSBox isb=new InvSBox();
	KeySchedule ks=new KeySchedule();
	
//--------------------------------------------------------------------		
	public static void main(String []rana)
	{
		AES aes=new AES();
		
		if(rana.length!=3)
		{
			System.out.println("\n\nUsage :  java AES [e|d] keyFile inputFile.\n\nfor more details please read the README file  \n\nTerminating the program.......\n\n");
			System.exit(0);
		}
		
		String inputFileName=rana[2];
		String encFileName=rana[2];
		String keyfile = rana[1];
		System.out.println(rana[0]);
		if(rana[0].equals("e"))
		aes.encryption(inputFileName,keyfile);
		else if(rana[0].equals("d"))
		aes.decryption(encFileName,keyfile);
		else
		{
		System.out.println("\n\nWrong parameter used. Please either Encrypt(e) or decrypt(d) the file\n\nTerminating the Program.......\n\n");	
		System.exit(0);
		}
	}
//--------------------------------------------------------------------		
	void encryption(String inputFileName,String keyfile)
	{
		ks.keySchedule(keyfile); // Generate the key-full from cipher key
		
		try
	    {	   
		
	    URL url = AES.class.getResource(inputFileName);  //for relative path 
	    String path = url.getPath();
	    File file = new File(path);
	    System.out.println(url.getPath());
		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		
		//creating an output text file.
		path=path.substring(0, path.length()-inputFileName.length());
    	System.out.println(path);
        String outputFileName=path+inputFileName+".enc";
        PrintWriter writer=null;
		try
		{
		 writer = new PrintWriter(outputFileName, "UTF-8");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Error in creating the new PrintWriter");
		}
		
		
			while ((strLine = br.readLine()) != null)  
			{   
				int stringLength = strLine.length();
				//System.out.println (stringLength);
				// making length equal to 32 with padding or removing extra chars
				if(stringLength<32)
				{
					for(int i=0;i<(32-stringLength);i++)
						strLine=strLine+"0";
				}
				else if(stringLength>32)
				{
					strLine=strLine.substring(0,32);
				}
				
				System.out.println (strLine);
				//converting the line read into column major matrix i.e. state.
				stringToColumnMajor(strLine);
				
				//Initial AddRoundKey. Pass the round number as integer. 
				addRoundKey(0);
				//Next 13 rounds
				System.out.println("After addRoundKey(0)");
				printStateLine();
				for(int i=1;i<=14;i++)
				{
					subByte();
					System.out.println("After subByte");
					printStateLine();
					shiftRow();
					System.out.println("After shiftRow");
					printStateLine();
					if(i!=14)
					{
					mixColumn();
					System.out.println("After mixColumn");
					printStateLine();
					}
					addRoundKey(i);
					System.out.println("After addRoundKey("+i+")");
					printStateLine();
				}
				
				System.out.println("The Cipher Text is : ");
				printState();
				
				//Writing to output file
				String enc=columnMajorToString();
				//System.out.println("The Cipher Text is : "+enc);
				writer.write(enc+"\n");
				
			}
		writer.close();
		br.close();
		}
	    
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Please check if all filenames are correct. Files should exist in the same folder.");
		}
		
		
		
		
	}//Encryption function ends here.

//--------------------------------------------------------------------	
	void stringToColumnMajor(String str)
	{
		int k=0;
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{
				state[j][i]=str.charAt(k++)+""+str.charAt(k++);
			}
		}	
	}
//--------------------------------------------------------------------		
	void printState()
	{
		//System.out.println("Printing State -------");
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{
				System.out.print(state[i][j].toUpperCase()+" ");
			}
			System.out.print("\n");
		}
		
	}
//--------------------------------------------------------------------	
	
	void printStateLine()
	{		
		
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{
				System.out.print(state[j][i].toUpperCase());
			}			
		}
		System.out.println();
	}
	
	
	
	String columnMajorToString()
	{		
		String str="";
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{
				str=str+state[j][i].toUpperCase();
				//System.out.print(str);
				
			}			
		}
		//System.out.println();
		return str;
	}
//--------------------------------------------------------------------		
	void addRoundKey(int round)
	{
		
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{				
				state[i][j]=of.xorStringWithString(state[i][j], ks.expandedKey[i][j+(round*4)])	;		
			}			
		}		
	}

//--------------------------------------------------------------------		
	void subByte()
	{
		
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{				
				state[i][j]=sb.sBox(state[i][j]);				
			}			
		}		
	}
//--------------------------------------------------------------------		
    void shiftRow()
    {
    	String t=state[1][0];
    	state[1][0]=state[1][1];
    	state[1][1]=state[1][2];
    	state[1][2]=state[1][3];
    	state[1][3]=t;
    	
    	 t=state[2][0];
    	state[2][0]=state[2][2];
    	state[2][2]=t;
    	 t=state[2][1];
    	state[2][1]=state[2][3];
    	state[2][3]=t;
    	
    	 t=state[3][3];
    	state[3][3]=state[3][2];
    	state[3][2]=state[3][1];
    	state[3][1]=state[3][0];
    	state[3][0]=t;
 	
    }
//--------------------------------------------------------------------
    void mixColumn()
    {
    	int matrix[][]=new int[][]
    			{
    				{2,3,1,1},
    				{1,2,3,1},
    				{1,1,2,3},
    				{3,1,1,2},					
    			};
    	
    	for(int i=0;i<4;i++)
    	{   String temp[]=new String[4];
    		for(int j=0;j<4;j++)
    		{
    			int a = of.rM(state[0][i], matrix[j][0]);
    			//System.out.println("---------------"+Integer.toHexString(a));
    			int b = of.rM(state[1][i], matrix[j][1]);
    			//System.out.println("---------------"+Integer.toHexString(b));
    			int c = of.rM(state[2][i], matrix[j][2]);
    			//System.out.println("---------------"+Integer.toHexString(c));
    			int d = of.rM(state[3][i], matrix[j][3]);
    			//System.out.println("---------------"+Integer.toHexString(d));
    			int e = a^b^c^d;
    			String s=Integer.toHexString(e);
    			if (s.length()==1)
    				s="0"+s;
    			//System.out.println(s);
    			temp[j]=s;
    		}
    		state[0][i]=temp[0];
    		state[1][i]=temp[1];
    		state[2][i]=temp[2];
    		state[3][i]=temp[3];
    	}
    	
    }
	
//--------------------------------------------------------------------
//--------------------------------------------------------------------
    //Decryption function starts here.
    
	void decryption(String encFileName,String keyfile)
	{
		ks.keySchedule(keyfile); // Generate the key-full from cipher key
		
		try
	    {	   
		
	    URL url = AES.class.getResource(encFileName);  //for relative path 
	    String path = url.getPath();
	    File file = new File(path);
	    //System.out.println(url.getPath());
		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		
		//creating an output text file.
		path=path.substring(0, path.length()-encFileName.length());
    	//System.out.println(path);
        String outputFileName=path+encFileName+".dec";
        PrintWriter writer=null;
		try
		{
		 writer = new PrintWriter(outputFileName, "UTF-8");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
			while ((strLine = br.readLine()) != null)  
			{   
				int stringLength = strLine.length();
				//System.out.println (stringLength);
				// making length equal to 32 with padding or removing extra chars
				if(stringLength<32)
				{
					for(int i=0;i<(32-stringLength);i++)
						strLine=strLine+"0";
				}
				else if(stringLength>32)
				{
					strLine=strLine.substring(0,32);
				}
				
				System.out.println (strLine);
				//converting the line read into column major matrix i.e. state.
				stringToColumnMajor(strLine);
				
				
				//reverse 14 rounds
				
				for(int i=14;i>=1;i--)
				{
					addRoundKey(i);
					System.out.println("After addRoundKey("+i+")");
					printStateLine();
					
					if(i!=14)
					{
					invMixColumn();
					System.out.println("After mixColumn");
					printStateLine();
					}
					
					invShiftRow();
					System.out.println("After shiftRow");
					printStateLine();
					
					invSubByte();
					System.out.println("After subByte");
					printStateLine();
					
					
					
				}
				//Initial AddRoundKey. Pass the round number as integer. 
				addRoundKey(0);
				System.out.println("After addRoundKey(0)");
				printStateLine();
				System.out.println("The Plain Text is : ");
				printState();
				
				//Writing to output file
				String dec=columnMajorToString();
				//System.out.println("The Cipher Text is : "+dec);
				writer.write(dec+"\n");
				
			}
		writer.close();
		br.close();
		}
	    
		catch(Exception e)
		{
			e.printStackTrace();
		}	
	}
	
	
//------------------------------------------------------------------------------------------------
	void invSubByte()
	{
		
		for(int i=0;i<4;i++)
		{
			for(int j=0;j<4;j++)
			{				
				state[i][j]=isb.sBox(state[i][j]);				
			}			
		}		
	}
//--------------------------------------------------------------------		
    void invShiftRow()
    {
    	String t=state[1][3];
    	state[1][3]=state[1][2];
    	state[1][2]=state[1][1];
    	state[1][1]=state[1][0];
    	state[1][0]=t;
    	
    	 t=state[2][3];
    	state[2][3]=state[2][1];
    	state[2][1]=t;
    	 t=state[2][2];
    	state[2][2]=state[2][0];
    	state[2][0]=t;
    	
    	 t=state[3][0];
    	state[3][0]=state[3][1];
    	state[3][1]=state[3][2];
    	state[3][2]=state[3][3];
    	state[3][3]=t;
 	
    }
//--------------------------------------------------------------------
    void invMixColumn()
    {
    	int matrix[][]=new int[][]
    			{
    				{14,11,13,9},
    				{9,14,11,13},
    				{13,9,14,11},
    				{11,13,9,14},					
    			};
    	
    	for(int i=0;i<4;i++)
    	{   String temp[]=new String[4];
    		for(int j=0;j<4;j++)
    		{
    			int a = of.rM(state[0][i], matrix[j][0]);
    			//System.out.println("---------------"+Integer.toHexString(a));
    			int b = of.rM(state[1][i], matrix[j][1]);
    			//System.out.println("---------------"+Integer.toHexString(b));
    			int c = of.rM(state[2][i], matrix[j][2]);
    			//System.out.println("---------------"+Integer.toHexString(c));
    			int d = of.rM(state[3][i], matrix[j][3]);
    			//System.out.println("---------------"+Integer.toHexString(d));
    			int e = a^b^c^d;
    			String s=Integer.toHexString(e);
    			if (s.length()==1)
    				s="0"+s;
    			//System.out.println(s);
    			temp[j]=s;
    		}
    		state[0][i]=temp[0];
    		state[1][i]=temp[1];
    		state[2][i]=temp[2];
    		state[3][i]=temp[3];
    	}
    	
    }
}
