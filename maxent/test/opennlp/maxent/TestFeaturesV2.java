package opennlp.maxent;

import junit.framework.TestCase;

import org.junit.Test;

public class TestFeaturesV2 extends TestCase {
	FeaturesV2 f = new FeaturesV2("asdfgh");
	@Test
	public void testGetFeatures(){
		String[] feas = f.getFeatures(3);
		for (String string : feas) {
			System.out.println(string);
		}
	}
}
