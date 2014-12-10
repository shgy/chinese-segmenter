package org.xh.xs.lucene_42;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.xh.xs.core.TermInfo;
import org.xh.xs.core.XhSeg;
/*
 * 分词器的接口
 * */
public final class XhTokenizer extends Tokenizer{
   private final static int BUF_SIZE=256;
   private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
   private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
   private final PositionIncrementAttribute posIncrAtt = addAttribute(PositionIncrementAttribute.class);
   private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

   private XhSeg xs=new XhSeg();
   private char[] buffer=new char[BUF_SIZE];
   //由于这里采用缓存的方式来读取input的内容，故在计算位置偏移上会有一些出入
   private int rLen,lbLen;
   private TermInfo[] segs;//分词的结果
   private int pos;
	public XhTokenizer(Reader input) {
		super(input);
		pos=-1;
	}

	
	@Override
	public void close() throws IOException {
		super.close();
	}

	@Override
	public void end() throws IOException {
		// TODO Auto-generated method stub
		super.end();
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		setReader(input);
		pos=-1;
		lbLen=rLen=0;
	}
	
	@Override
	public boolean incrementToken() throws IOException {
		clearAttributes();
		try {
			if(pos==-1){
				int bLen=input.read(buffer);
				if(bLen==-1)return false;
				rLen+=lbLen;lbLen=bLen;
				segs=xs.segment(buffer,bLen);
				pos=0;
			}
			termAtt.append(segs[pos].term);
			offsetAtt.setOffset(rLen+segs[pos].s, rLen+segs[pos].s+segs[pos].c);
			posIncrAtt.setPositionIncrement(segs[pos].c);
		
			++pos;
			if(pos==segs.length)pos=-1;
			return true;
		} catch (IOException e) {
			throw new IllegalArgumentException("reader cannot read!");
		}
	}

}
