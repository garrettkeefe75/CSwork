package assignment08;

/**
 * 
 * @author David Harrington &&
 *
 */
public class A08String
{
	private String str;

	/**
	 * 
	 */
	public A08String( )
	{
		this.str = "";
	}

	/**
	 * 
	 * @param str
	 */
	public A08String(String str)
	{
		this.str = str;
	}

	@Override
	public String toString()
	{
		return str;
	}

	@Override
	public boolean equals(Object other)
	{
		if (other == null)
			return false;

		if (this == other)
			return true;

		if (!(other instanceof A08String))
			return false;

		A08String that = (A08String) other;
		return this.str.equals(that.str);
	}

	@Override
	public int hashCode()
	{
		int value = 0;
		for (char currentChar: this.str.toCharArray()) {
			value += currentChar;
		}
		return value;
	}
}
