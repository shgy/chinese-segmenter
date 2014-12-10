package org.lucene.ictanalyzer;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public class ICTCLASAnalyzer extends Analyzer {
	  /** Default maximum allowed token length */
	  public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

	  private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;
	  protected final Version matchVersion;
	
	public ICTCLASAnalyzer(Version version){
		this.matchVersion = version;
	}
	
	public void setMaxTokenLength(int len){
		this.maxTokenLength = len;
	}
	
	@Override
	protected TokenStreamComponents createComponents(String fieldName,
			Reader reader) {
		 final ICTClASTokenizer src = new ICTClASTokenizer(matchVersion, reader);
		 src.setMaxTokenLength(this.maxTokenLength);
		 
		 TokenStream tok = new PunctuationFilter(matchVersion, src);
		 return new TokenStreamComponents(src,tok) {
		      @Override
		      protected void setReader(final Reader reader) throws IOException {
		        src.setMaxTokenLength(ICTCLASAnalyzer.this.maxTokenLength);
		        super.setReader(reader);
		      }
		    };
	}

}
