package beit.skn.classes;

import java.math.BigInteger;
import java.util.Random;

public class RSAEncryptorClass 
{
	private static int []primeArray;
	private static int top = 0;
	private static int MAX_PRIME_NUMBERS = 20;
	
	private static int privateKey;
	private static int publicKey;
	private static int modulus;
	
	
	public static int getModulus() 
	{
		return modulus;
	}

	public static int getPublicKey() 
	{
		return publicKey;
	}

	static
	{
		int i, j;
		primeArray = new int[MAX_PRIME_NUMBERS];		
		top=0;		
		for(i=13;top<MAX_PRIME_NUMBERS;i++)
		{
			for(j=2;j<i;j++)
			{
				if(i%j==0)
					break;
			}
			if(j==i)
				primeArray[top++] = i;
		}
		Random randomNumberGenerator = new Random();
		i = randomNumberGenerator.nextInt(MAX_PRIME_NUMBERS);
		do
		{
			j = randomNumberGenerator.nextInt(MAX_PRIME_NUMBERS);				
		}while(i==j);
		
		modulus = primeArray[i]*primeArray[j];
		int pn = (primeArray[i]-1)*(primeArray[j]-1);
		
		for(publicKey=2;publicKey<pn;publicKey++)
		{
			if(euclideanGCD(publicKey, pn) == 1)
				break;
		}				
		privateKey = 2;
		do
		{
			privateKey++;
		}while((privateKey*publicKey)%pn!=1);		
	}
	
	public static int []encryptText(String plaintext, int modulus, int pubKey)
	{
		int len = plaintext.length();
		int []c = new int[len];
		int []m = new int[len];
		char []str = plaintext.toCharArray();
		int i = 0;
		
		for(i=0;i<len;i++)
			m[i] = (int)str[i];
		for(i=0;i<len;i++)
		{
			c[i] = poweredMod(m[i], pubKey, modulus);
		}
		return c;		
	}
	
	public static String decryptText(int []ciphertext)
	{	
		int len = ciphertext.length;
		int []m = new int[len];
		char []str = new char[len];		
		int i = 0;
		
		for(i=0;i<len;i++)
		{
			m[i] = poweredMod(ciphertext[i], privateKey, modulus);
		}
		for(i=0;i<len;i++)
			str[i] = (char)m[i];		
		String plaintext = String.valueOf(str);		
		return plaintext;		
	}
	
	public static void listPrimes()
	{
		int i = 0;
		for(i=0;i<top;i++)
		{
			System.out.println(primeArray[i]);
		}
	}
	
	private static int euclideanGCD(int a, int b)
	{
	   if (b==0) return a;
	   return euclideanGCD(b,a%b);
	}

	private static int poweredMod(int base, int power, int mod)
	{
		BigInteger n = new BigInteger(""+base);
		BigInteger a = new BigInteger(""+power);
		BigInteger b = new BigInteger(""+mod);
		BigInteger x;
		x = n.modPow(a, b);
		return x.intValue();		
	}
}
