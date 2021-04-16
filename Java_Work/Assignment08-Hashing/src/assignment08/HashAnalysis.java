package assignment08;

import java.io.File;

import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * 
 * @author David Harrington && Ethan Pippin
 *
 */
@SuppressWarnings("unused")
public class HashAnalysis
{
	public static void main(String[] args)
	{
		SimpleWriter out = new SimpleWriter1L();

		final int numBuckets = 30;
		HashTableSet<A08String> hash = new HashTableSet<>(numBuckets);

		String currentDirectory = System.getProperty("user.dir");
		currentDirectory = currentDirectory + "/src/assignment08/data/";
		currentDirectory = currentDirectory + "a08-startend.txt";
//		File file = new File(currentDirectory);
		
		SimpleReader file = new SimpleReader1L(currentDirectory);
//    SimpleReader file = new SimpleReader1L("data/a08-length8.txt");
//    SimpleReader file = new SimpleReader1L("data/a08-mod30.txt");
//    SimpleReader file = new SimpleReader1L("data/a08-startend.txt");

		while (!file.atEOS())
		{
			A08String element = new A08String(file.nextLine());
			if (!hash.contains(element))
				hash.add(element);
		}
		file.close();

		out.println(file.name());
		out.println("Bucket\tHits\tCount");
		out.println("------\t----\t-----");
		for (int i = 0; i < hash.numBuckets(); i++)
		{
			out.println(i + "\t" + hash.bucketSize(i) + "\t" + stars(hash.bucketSize(i)));
		}
		out.close();
	}

	/**
	 * Returns a string with the given number of asterisks.
	 * 
	 * @param  count number of asterisks
	 * @return       strings with the given number of asterisks
	 */
	private static String stars(int count)
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++)
			sb.append("*");
		return sb.toString();
	}
}
