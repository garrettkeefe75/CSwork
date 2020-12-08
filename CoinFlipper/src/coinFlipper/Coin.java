package coinFlipper;

import java.util.Random;

public class Coin {
	public static void coinFlip()
	{
		Random rand = new Random();
		int num = 0;
		for(int i = 0; i < 20; i++) 
		{
			int num1 = rand.nextInt() % 2;
			num += Math.abs(num1);
		}
		
		System.out.println(num);
	}

	public static void main(String[] args) 
	{
		for(int i = 0; i < 20; i++) 
		{
			coinFlip();
		}
		
	}

}
