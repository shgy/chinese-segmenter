package org.sgy.tokenizer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shuaiguangying
 *	用于对评论内容进行分词的分词器
 *	采用MMSeg算法[正向匹配+规则]
 *	参考源MMseg4j系统的源码定制
 */
/**
 * @author shuaiguangying
 *	处理的逻辑如下：
 *  1、将字符按照Unicode标准进行分类，只对其中的other_letter类别进行分词处理。
 *    这意味着，本文的分词器不仅能够对中文进行分词
 *  2、对于数字，读取全部连续的数字后，如果最后面是单位，则数字联合单位形成一个词；
 *    如果数字的后面是字母，则读取所有的字母及数字，组合一个词。
 */
public class Tokenizer {
	private Dictionary dic = Dictionary.getInstance();
	private MMSeg mmseg = new MMSeg(dic);
	/**
	 * @param sen
	 * @param terms
	 * 用MMseg算法对字符数组进行分词
	 */
	private void segment(char[] sen,List<String> terms){
		int end = 0;
		do{
			Chunk chunk = mmseg.segment(sen, end);
			Word[] word = chunk.getWords();
			for(int i=0,c= chunk.getCount();i<c;i++){
				terms.add(word[i].getText());
			}
			end += chunk.getLen();
		}while(end<sen.length);
	}
	/**
	 * @param str
	 */
	public String[] segment(String str){
		List<String> list = new ArrayList<String>();
		
		char[] sen = str.toCharArray();
		int offset ,pointer,type,end;
		pointer = offset = 0;
		while(pointer<sen.length){
			offset = pointer;
			type = Character.getType(sen[pointer++]);
			switch(type){
				/******************************************************
				 * 情形一：字符的类型是OTHER_LETTER; 可能是中文、日文等 即需要分词的文本
				 * ****************************************************/
				case Character.OTHER_LETTER: //待分词文本
					end = slip(sen,pointer,otherLetter);
					char[] toSeg = new char[end-offset];
					System.arraycopy(sen, offset, toSeg, 0, end-offset);
					segment(toSeg,list);
					pointer = end;
				break;
				/******************************************************
				 * 情形二：是字母 //字母后面的数字,如: VH049PA
				 * ****************************************************/
				case Character.UPPERCASE_LETTER:
				case Character.LOWERCASE_LETTER:
				case Character.TITLECASE_LETTER:
				case Character.MODIFIER_LETTER:
					end = pointer;
					int codePoint = toAscii(sen[pointer-1]);
					NationLetter nl = getNation(codePoint);
					switch(nl) {
					case EN: end = slip(sen, pointer,digitOrAscii);
						break;
					case RA: end = slip(sen, pointer,russia);
						break;
					case GE: end = slip(sen, pointer,greece);
						break;
					case  UNKNOW:
						break;
					}
					if(nl!=NationLetter.UNKNOW)
						list.add(newWord(sen,offset,pointer));
					pointer = end;
				break;
				/******************************************************
				 * 情形三：字符的类型是DECIMAL_DIGIT_NUMBER; 即数字
				 *        数字后面可能带单位符号，如“29元”，或者英文字母。
				 *        所有有两种处理方式
				 * ****************************************************/
				case Character.DECIMAL_DIGIT_NUMBER: //十进制数字（全角和半角）
					end = slip(sen,pointer,digit);
					if(end<sen.length){
						if(dic.isUnit(sen[end])){
							list.add(newWord(sen, offset, ++end));
						}else{
							end = slip(sen,offset,digitOrAscii);
							list.add(newWord(sen, offset, end));
						}
					}
					pointer = end;
				break;
				/******************************************************
				 * 情形四：①⑩㈠㈩⒈⒑⒒⒛⑴⑽⑾⒇ 连着用
				 * ****************************************************/
				case Character.OTHER_NUMBER:
					end = slip(sen,pointer,otherNumber);
					list.add(newWord(sen,offset,end));
					pointer = end;
				break;
				/******************************************************
				 * 情形五：ⅠⅡⅢ 单分
				 * ****************************************************/
				case Character.LETTER_NUMBER:
					end = slip(sen,pointer,letterNumber);
					list.add(newWord(sen,offset,end));
					pointer = end;
				break;
				/******************************************************
				 * 情形六：无法识别的字符，直接丢弃
				 * ****************************************************/
				default: 
				 break;
			}
		}
		return list.toArray(new String[list.size()]);
	}

	private String newWord(char[] sen,int s,int e){
		for(int i=s;i<e;i++)sen[i] = (char) transform(sen[i]);
		return String.valueOf(sen,s,e-s);
	}
	
	
	
	/**
	 * @param sen
	 * @param offset
	 * @param acceptor 接收的字符是否符合向前滑动的规则
	 * @return 往前 滑动 的距离
	 */
	private int slip(char[] sen,int offset,Acceptor acceptor){
		while(offset<sen.length){
			if(!acceptor.accept(sen[offset])) break;
			offset++;
		}
		return offset;
	}
	
	private static boolean isAsciiLetter(int codePoint) {
		return (codePoint >= 'a' && codePoint <= 'z') ||  (codePoint >= 'A' && codePoint <= 'Z') ;
	}
	
	private static boolean isRussiaLetter(int codePoint) {
		return (codePoint >= 'А' && codePoint <= 'я') || codePoint=='Ё' || codePoint=='ё';
	}
	
	private static boolean isGreeceLetter(int codePoint) {
		return (codePoint >= 'Α' && codePoint <= 'Ω') || (codePoint >= 'α' && codePoint <= 'ω');
	}
	/**
	 * EN -> 英语
	 * RA -> 俄语
	 * GE -> 希腊
	 * 
	 */
	private static enum NationLetter {EN, RA, GE, UNKNOW};
	
	private NationLetter getNation(int codePoint) {
		if(isAsciiLetter(codePoint)) {
			return NationLetter.EN;
		}
		if(isRussiaLetter(codePoint)) {
			return NationLetter.RA;
		}
		if(isGreeceLetter(codePoint)) {
			return NationLetter.GE;
		}
		return NationLetter.UNKNOW;
	}
	
	private static boolean isDigit(int codePoint) {
		return Character.getType(codePoint) == Character.DECIMAL_DIGIT_NUMBER;
	}
	/**
	 * 全角转半角
	 */
	private static int toAscii(int codePoint) {
		if((codePoint>=65296 && codePoint<=65305)	//０-９
				|| (codePoint>=65313 && codePoint<=65338)	//Ａ-Ｚ
				|| (codePoint>=65345 && codePoint<=65370)	//ａ-ｚ
				) {	
			codePoint -= 65248;
		}
		return codePoint;
	}
	
	private int transform(int codePoint){
		codePoint = Character.toLowerCase(codePoint);
		codePoint = toAscii(codePoint);
		return codePoint;
	}
	
//================分别处理各种不同类型的字符=========================
	
	interface Acceptor{boolean accept(int codePoint);}
	
	private final  Acceptor otherLetter = new Acceptor(){
		@Override
		public boolean accept(int codePoint) {return Character.getType(codePoint) == Character.OTHER_LETTER;	}
	};
	
	private final Acceptor digitOrAscii = new Acceptor(){
		@Override
		public boolean accept(int codePoint) {
			codePoint = toAscii(codePoint);//全角转半角
			return isDigit(codePoint)||isAsciiLetter(codePoint);}
	};
	
	private final Acceptor  russia= new Acceptor(){
		@Override
		public boolean accept(int codePoint) {return isRussiaLetter(codePoint);}
	};
	private final Acceptor greece = new Acceptor(){
		@Override
		public boolean accept(int codePoint) {return isGreeceLetter(codePoint);}
	};
	private final Acceptor digit = new Acceptor(){
		@Override
		public boolean accept(int codePoint) {return Character.getType(codePoint) == Character.DECIMAL_DIGIT_NUMBER;}
	};
	private final Acceptor otherNumber = new Acceptor(){
		public boolean accept(int codePoint) {return Character.getType(codePoint) == Character.OTHER_NUMBER;}
	};
	private final Acceptor letterNumber = new Acceptor(){
		public boolean accept(int codePoint) {return Character.getType(codePoint) == Character.LETTER_NUMBER;}
	};
	
	
	public static void main(String[] args) {
		Tokenizer s = new Tokenizer();
		System.out.println(s.segment("VH049PA VANCL ｖａｎｃｌ　http://www.baidu.com"));
	}
}
