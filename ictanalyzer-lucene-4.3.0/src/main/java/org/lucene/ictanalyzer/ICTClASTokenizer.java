package org.lucene.ictanalyzer;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.Version;
import org.nlpir.ictclas.ICTToken;
import org.nlpir.ictclas.NLPIR_ICTCLAS;

public class ICTClASTokenizer extends Tokenizer{
	
	  private NLPIR_ICTCLAS ictclas;
	  private int maxTokenLength = ICTCLASAnalyzer.DEFAULT_MAX_TOKEN_LENGTH;

	  /** Set the max allowed token length.  Any token longer
	   *  than this is skipped. */
	  public void setMaxTokenLength(int length) {
	    this.maxTokenLength = length;
	  }

	  /** @see #setMaxTokenLength */
	  public int getMaxTokenLength() {
	    return maxTokenLength;
	  }
	public ICTClASTokenizer(Version matchVersion, Reader input) {
	    super(input);
	    ictclas = new NLPIR_ICTCLAS(input);
	    init(matchVersion);
	  }
	
	public ICTClASTokenizer(Version matchVersion, AttributeFactory factory, Reader input) {
		super(factory, input);
		ictclas = new NLPIR_ICTCLAS(input);
		 init(matchVersion);
	}
	
	private void init(Version matchVersion) {
		
	}
	
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	//private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
	private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
	private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
	
	@Override
	public boolean incrementToken() throws IOException {
		clearAttributes();
		int posIncr = 1;
		ICTToken tok =null; 
		while(true){
			tok = ictclas.next();
			if(tok==null) return false;
			if(tok.getTermLen()<=this.maxTokenLength){
				posIncrAtt.setPositionIncrement(posIncr);
				termAtt.setEmpty();
				termAtt.append(tok.getTerm());
				termAtt.setLength(tok.getTermLen());
				//offsetAtt.setOffset(tok.getOffset(), tok.getOffset()+tok.getTermLen());
				typeAtt.setType(tok.getType());
				return true;
			}else{
				posIncr++;
			}
		}
	}
	
	@Override
	public void close() throws IOException {
		super.close();
		ictclas.exit();
	}
	
}
