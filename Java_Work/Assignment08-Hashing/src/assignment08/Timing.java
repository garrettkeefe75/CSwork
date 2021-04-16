package assignment08;

import java.util.Random;

/**
 * Timing class for timing different operations of the `HashTableSet` class
 * 
 * @author David Harrington and Ethan Pippin
 *
 */
public class Timing
{

	private static final int TIMES_TO_LOOP = 300;
	private static final int[] powersOfTen =
	{ 10, 100, 1000, 10000, 100000, 1000000 };
	private static final int[] bucketSizes =
	{ 101, 151, 379, 1325, 5965, 32809 };

	public static void main(String[] args)
	{
		timeContainsDefault();
		timeContainsVariableBuckets();
	}

	public static void timeContainsDefault()
	{
		System.out.println("N\tContains Default");

		String outputArray = "[";

		for (int currentPower = 0; currentPower < powersOfTen.length; currentPower += 1)
		{

			Random rng = new Random(12345);
			HashTableSet<Integer> hashTable = new HashTableSet<Integer>();

			for (int i = 0; i < powersOfTen[currentPower]; i++)
			{
				int num = rng.nextInt();
				while (hashTable.contains(num))
				{
					num = rng.nextInt();
				}
				hashTable.add(num);
			}

			long stopTime, midpointTime, startTime = System.nanoTime();

			while (System.nanoTime() - startTime < 1000000000)
			{ // empty block
			}

			startTime = System.nanoTime();

			for (int i = 0; i < TIMES_TO_LOOP; i++)
			{
				hashTable.contains(rng.nextInt());
			}

			midpointTime = System.nanoTime();

			for (int i = 0; i < TIMES_TO_LOOP; i++)
			{
				rng.nextInt();
			}

			stopTime = System.nanoTime();

			long average = ((midpointTime - startTime) - (stopTime - midpointTime)) / TIMES_TO_LOOP;

			System.out.println(powersOfTen[currentPower] + "\t" + average);

			outputArray += average + ",";
		}

		outputArray += "]";

		System.out.println(outputArray);
	}

	public static void timeContainsVariableBuckets()
	{
		for (int currentBucketSize = 0; currentBucketSize < bucketSizes.length; currentBucketSize++)
		{
			int currentSize = bucketSizes[currentBucketSize];

			System.out.println("N\tContains Variable");
			System.out.println(String.format("%s", currentSize));

			String outputArray = "[";

			for (int currentPower = 0; currentPower < powersOfTen.length; currentPower += 1)
			{

				Random rng = new Random(12345);
				HashTableSet<Integer> hashTable = new HashTableSet<Integer>(currentSize);

				for (int i = 0; i < powersOfTen[currentPower]; i++)
				{
					int num = rng.nextInt();
					while (hashTable.contains(num))
					{
						num = rng.nextInt();
					}
					hashTable.add(num);
				}

				long stopTime, midpointTime, startTime = System.nanoTime();

				while (System.nanoTime() - startTime < 1000000000)
				{ // empty block
				}

				startTime = System.nanoTime();

				for (int i = 0; i < TIMES_TO_LOOP; i++)
				{
					hashTable.contains(rng.nextInt());
				}

				midpointTime = System.nanoTime();

				for (int i = 0; i < TIMES_TO_LOOP; i++)
				{
					rng.nextInt();
				}

				stopTime = System.nanoTime();

				long average = ((midpointTime - startTime) - (stopTime - midpointTime)) / TIMES_TO_LOOP;

				System.out.println(powersOfTen[currentPower] + "\t" + average);

				outputArray += average + ",";
			}

			outputArray += "]";

			System.out.println(outputArray);
		}
	}

}
