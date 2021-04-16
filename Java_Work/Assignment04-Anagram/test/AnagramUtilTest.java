package assignment04;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

/**
 * 
 * @author David Harrington && Garrett Keefe
 *
 */
public class AnagramUtilTest
{

	@Test
	public void testSort1()
	{
		assertEquals("ahtu", AnagramUtil.sort("utah"));
	}

	@Test
	public void testSort2()
	{
		assertEquals("ahtu", AnagramUtil.sort("UTAH"));
	}

	@Test
	public void testSort3()
	{
		assertEquals("ahtu", AnagramUtil.sort("UtAh"));
	}

	@Test
	public void testAreAnagrams1()
	{
		assertTrue(AnagramUtil.areAnagrams("nonuniversalist", "involuntariness"));
	}

	@Test
	public void testAreAnagramsFalse()
	{
		assertFalse(AnagramUtil.areAnagrams("Blueberry", "Muffin"));
	}

	@Test
	public void testBiggestAnagramGroup()
	{
		List<String> list = AnagramUtil.getLargestAnagramGroup("data/words.txt");

		assertTrue(list.contains("carets"));
		assertTrue(list.contains("crates"));
		assertTrue(list.contains("caster"));
		assertTrue(list.contains("Reacts"));
		assertTrue(list.contains("recast"));
		assertTrue(list.contains("traces"));
	}

	@Test
	public void testBiggestAnagramGroup2()
	{
		List<String> list = AnagramUtil.getLargestAnagramGroup("data/TERMINATED.txt");

		assertFalse(list.contains("TERMINATED"));
		assertTrue(list.contains("zqxsyu"));
		assertTrue(list.contains("yuxqsz"));
		assertTrue(list.contains("syqzxu"));
	}

}
