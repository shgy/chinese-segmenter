package org.xh.xs.lucene_42;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;

public class XhAnalyzer  extends Analyzer {
	
	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		final XhTokenizer src=new XhTokenizer(reader);
		return new TokenStreamComponents(src) {
		      @Override
		      protected void setReader(final Reader reader) throws IOException {
		        super.setReader(reader);
		      }
		    };
	}

}
