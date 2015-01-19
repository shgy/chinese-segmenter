package opennlp.maxent;

import junit.framework.TestCase;

import org.junit.Test;

public class TestCharUtil extends TestCase{
	@Test
	public void testStrq2b(){
		String line="aｓｄｆａ";
		assertEquals(CharUtil.strq2b(line),"asdfa");
	}
}
