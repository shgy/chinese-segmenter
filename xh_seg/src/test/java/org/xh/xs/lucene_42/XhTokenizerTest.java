package org.xh.xs.lucene_42;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;


public class XhTokenizerTest {
	@Test
	public void test() throws IOException{
		
		Analyzer analyzer=new XhAnalyzer();
		TokenStream ts=analyzer.tokenStream("", new StringReader("  这是一个测试文件."));
		while(ts.incrementToken()){
			System.out.println(ts.getAttribute(CharTermAttribute.class));
			System.out.println(ts.getAttribute(OffsetAttribute.class).startOffset());
		}
	}
}
