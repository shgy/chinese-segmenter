package org.lucene.ictanalyzer;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.FilteringTokenFilter;
import org.apache.lucene.util.Version;

/**
 * @author shuaiguangying
 *	过滤标点符号
 */
public class PunctuationFilter extends FilteringTokenFilter{
	  private final Version matchVersion;
	  private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

	  protected PunctuationFilter(Version matchVersion, TokenStream in) {
		  super(true,in);
		  this.matchVersion = matchVersion;
	 }
	

	@Override
	protected boolean accept() throws IOException {
		String type = typeAtt.type();
		if(type.startsWith("w"))
			return false;
		return true;
	}

}
