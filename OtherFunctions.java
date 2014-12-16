

public class OtherFunctions 
{
	
	public String xorStringWithString(String str, String s)
	{
		//System.out.println("--"+str+"---"+s);
		int x= Integer.parseInt(str,16)^Integer.parseInt(s,16);
		String rS= Integer.toHexString(x);
		if(rS.length()<2)
			rS="0"+rS;
		//System.out.println("-----"+rS);
		return rS;
		
	}
	
	
	//Rijndeal Multiplication 
	public int rM(String s,int b1)
	{
		//System.out.println("value of S initial "+s);
		int in=Integer.parseInt(s, 16);
		//System.out.println("value of S int "+in);
		String a=eightBitString(in);
		//System.out.println(a);
		String b=eightBitString(b1);
		
		char p=0;
		byte carry;
		byte IB=27;
		
		for(int i=1;i<=8;i++)
		{
			
			if(Integer.parseInt(a, 2)==0 || Integer.parseInt(b, 2)==0)
				break;
			
			//System.out.println("product before xored with a "+(int)p);
			
			if((Integer.parseInt(b, 2) & 1) == 1)
			{
				p^=Integer.parseInt(a, 2);
				//System.out.println("product after xored with a "+(int)p);
			}
			
			
			b= eightBitString((Integer.parseInt(b, 2) >>> 1));
			//System.out.println(b);
			//System.out.println("value of b after one right shift "+b+" "+" "+Integer.parseInt(b, 2));
			
			carry=0;
			if(Integer.parseInt(a, 2)>>>7 == 1)
				carry=1;
			
			a=eightBitString((Integer.parseInt(a, 2) << 1));
			
			//System.out.println("value of a after one left shift "+a+" "+Integer.parseInt(a, 2));
			
			int as=Integer.parseInt(a, 2);
			//System.out.println("a parsed - "+as);
			if(carry==1)
			{
				//System.out.println("a parsed - "+as);
				as^= IB;
				a=eightBitString(as);
				//System.out.println("a xored with IB "+as);
			}	
			
			
		}
		
		return p;
		
	}
	
	String eightBitString(int in)
	{
		String a= Integer.toBinaryString(in);
		//System.out.println("initial binary "+a);
		int l = a.length();
		if (l<8)
		  {
			for(int i=0;i<8-l;i++)
			a="0"+a;
		  }
		else if (l>8)
		{
			a=a.substring(l-8);
		}
		//System.out.println("final binary "+a);
		return a;
	}
	
}
