package org.xh.xs.util;
/*
 * @author 帅广应
 * @email 810050504@qq.com
 * 
 * Unicode字符处理工具类，提供字符的大写转小写
 * 全角转半角
 * */
public class CharUtil {
	//判断是否是非字母非数字半角符号
	public static boolean isHalfWidthS(char ch){
		return false;
	}
	
	public static boolean isSpace(char input){
		return input == 8 || input == 9 
				|| input == 10 || input == 13 
				|| input == 32 || input == 160;
	}
	
	public static boolean isEnglish(char input){
		return (input >= 'a' && input <= 'z') 
				|| (input >= 'A' && input <= 'Z');
	}
	
	public static boolean isNumber(char input){
		return input >= '0' && input <= '9';
	}
	
	/*
	 * 判断是否是通用标点符号
	 * */
	public static boolean isCommPunc(char ch){
		if( ch=='。' ||ch=='!'||ch=='、'||
			ch=='.'||ch==','||ch=='…'||
		   ch=='~'){
			return true;
		}
		return false;
	}
	
	
	public static boolean isCJKCharacter(char input){
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(input);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				//全角数字字符和日韩字符
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				//韩文字符集
				|| ub == Character.UnicodeBlock.HANGUL_SYLLABLES 
				|| ub == Character.UnicodeBlock.HANGUL_JAMO
				|| ub == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
				//日文字符集
				|| ub == Character.UnicodeBlock.HIRAGANA //平假名
				|| ub == Character.UnicodeBlock.KATAKANA //片假名
				|| ub == Character.UnicodeBlock.KATAKANA_PHONETIC_EXTENSIONS
				) {  
			return true;
		}else{
			return false;
		}
		//其他的CJK标点符号，可以不做处理
		//|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
		//|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
	}
	/*全角字符从的unicode编码从 65281~65374   
     *半角字符从的unicode编码从  33 ~ 126   
     *	差值65248
     *空格比较特殊,全角为       12288,半角为       32 
     */
	public static char sw(char ch){
		if (ch == 12288) { //空格
		ch = (char) 32;
		}else if (ch > 65280 && ch < 65375) {
    	ch = (char) (ch - 65248);
    	//转成半角以后，有可能是大写字母，需要进行转换
	    	 if (ch >= 'A' && ch <= 'Z') {
	        	ch += 32;
			 }
		}else if (ch >= 'A' && ch <= 'Z') {
			ch += 32;
		}
//		else if(ch == 12290){ //句号
//			ch=(char)46;
//		}
		return ch;
	}
	/*
	 * 对字符数组进行全角半角转化
	 * */
	public static void sw(char[] str){
		if(str==null||str.length<1)return;
		for (int i = 0; i < str.length; i++) {
			str[i]=sw(str[i]);
		}
	}
	
	public char[] trim(char[] str){
//		去除两边空白
		if(str==null||str.length<1)return str;
		int s=0,e=str.length-1;
		while(s<e&&CharUtil.isSpace(str[s]))++s;
		while(e>s&&CharUtil.isSpace(str[e]))--e;
		
		if(e-s+1<str.length){
			char[] des=new char[e-s+1];
			System.arraycopy(str, s, des, 0, e-s+1);
			str=des;
		}
		return str;
	}
	
	public static String sw(String str){
		char[] ss=str.toCharArray();
		sw(ss);
		return String.valueOf(ss);
	}
	
	/*
	 * 过滤文本，只保留：中文、英文字母，阿拉伯数学
	 * 对于广告评论，有这个需求
	 * */
	public static String filter(String str){
		if(str==null||str.isEmpty())return str;
		StringBuffer sb=new StringBuffer();
		char ch;
		for (int i = 0; i < str.length(); i++) {
			ch=CharUtil.sw(str.charAt(i));
			if(CharUtil.isCJKCharacter(ch)||
				CharUtil.isEnglish(ch)||
				CharUtil.isNumber(ch)){
				sb.append(ch);
			}
		}
		return sb.toString();
	}

}
