package org.xh.xs.lucene_42;

import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;

public class XhTokenizerFactory extends TokenizerFactory{
	
	@Override
	public Tokenizer create(Reader input) {
		return new XhTokenizer(input);
	}

}
