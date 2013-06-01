package beit.skn.pingmeserver;


public class EncryptionStub
{
	public static String encrypt(String plaintext)
	{
		return new StringBuilder(plaintext).reverse().toString();
	}
	
	public static String decrypt(String ciphertext)
	{
		return new StringBuilder(ciphertext).reverse().toString();
	}
}