package org.xh.xs.core;


import java.util.ArrayList;

import org.xh.xs.dic.DATrie;
import org.xh.xs.util.CharUtil;

/*
 * 分词器，对文本进行分词：算法为逆向向最大匹配
 * */
public class XhSeg {
	private static DATrie dic=new DATrie(DATrie.InitType.NOT_Empty);
	/*
	 * 这里输入的是已经经过预处理的干净文本，
	 * 逆向最大匹配分词
	 * 先在词典中查询，如果不能解决
	 * 则对英文字符和数字进行处理
	 * 英文字符与数字进行分开
	 * @return 输入文本的位置是否切分
	 * */
	private TermInfo[] invertedMaxMatch(final char[] text,int start){
		final ArrayList<TermInfo> list=new ArrayList<TermInfo>();
		while(start>=0){
			int maxLen=dic.maxMatch(text, start);
			if(maxLen<=1){
				char ch=text[start];
				int tmp=start;
				boolean handle=false;
				//处理英文
				while(tmp>=0&&CharUtil.isEnglish(text[tmp])){
					--tmp;handle=true;
				}
				if(start>tmp){
					list.add(new TermInfo(text,tmp+1,start-tmp));//start位置需要分开
					start=tmp;
				}
				//处理数字
				while(tmp>=0&&CharUtil.isNumber(text[tmp])){
					--tmp;handle=true;
				}
				if(start!=tmp){
					list.add(new TermInfo(text,tmp+1,start-tmp));//start位置需要分开
					start=tmp;
				}
				//如果前面没有英文，也没有数字，执行下面的逻辑
				if(!handle){
					if(CharUtil.isCJKCharacter(ch))
						list.add(new TermInfo(text,start,1));
					--start;
				}
			}else{
				list.add(new TermInfo(text,start-maxLen+1,maxLen));//start位置需要分开
				start-=maxLen;
			}
		}
		
		TermInfo[] tis=new TermInfo[list.size()];
		for(int i=0;i<tis.length;i++){
			tis[i]=list.get(tis.length-1-i);
		}
		return tis;
	}
	
	public TermInfo[] segment(char[] text,int len){
		CharUtil.sw(text);//全角转半角，大写转小写
		return invertedMaxMatch(text,len-1);
	}
	
	public String[] segments(char[] text,int len){
		TermInfo[] pos=segment(text,len);
		String[] ss=new String[pos.length];
		
		for (int i = 0; i < ss.length; i++)
			ss[i]=pos[i].term;
		return ss;
	}
	/*
	 * 进行分词处理
	 * */
	public String[]  segment(String text){
		return segments(text.toCharArray(),text.length());
	}
}
