package opennlp.maxent;

import java.util.HashSet;

public class CharUtil {
	 static HashSet<Character> digits=new HashSet<Character>();
	 static HashSet<Character> punc = new HashSet<Character>();
	 static{
		 String digits_str="1234567890〇一二三四五六七八九十";
		 for(int i=0,len=digits_str.length();i<len;i++){
			 digits.add(digits_str.charAt(i));
		 }
		 String punc_str="!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~。？！，、；：“”‘’（）{}【】—…《》";
		 for(int i=0,len=punc_str.length();i<len;i++){
			 punc.add(punc_str.charAt(i));
		 }
	 }
	 
	 
	// 1234567890〇一二三四五六七八九十
	public static boolean isdigit(char ch){
		return digits.contains(ch);
	}
	
	//全角转半角
	public static String strq2b(String text){
		return String.valueOf(strq2b(text.toCharArray()));
	}
	
	public static char[] strq2b(char[] text){
		char[] newText = new char[text.length];
		for(int i=0;i<text.length;i++){
			char ch=text[i];
			if(ch==12288)
				newText[i]=' ';
			else if(ch>=65281 && ch<=65374){
				ch-=65248;
				newText[i]=ch;
			}else{
				newText[i]=ch;
			}
		}
		return newText;
	}
	
//	#判断是否是标点符号
//	#。？！，、；：“”‘’（）{}【】—…《》
//	asc_punc_str=u"!\"#$%&\'()*+,-./:;<=>?@[\\]^_`{|}~"
//	chi_punc_str=u"。？！，、；：“”‘’（）{}【】—…《》"
	public static boolean ispunc(char ch){
		return punc.contains(ch);
	}
	//是否是日期单位: 年/月/日
	public static boolean isdateunit(char ch){
		return ch=='年'||ch=='月'||ch=='日';
	}
	//是否是英文字符:大写、小写 共52个
	public static boolean isletter(char ch){
		if( (ch>='a' && ch<='z') || (ch>='A' && ch<='Z'))
			return true;
		return false;
	}
	//是否是汉字
	//\u4e00-\u9fa5
	//\uF900-\uFA2D
	public static boolean ishanzi(char ch){
		if ((ch >= '\u4e00' && ch <= '\u9fa5') || (ch >= '\uF900' && ch <= '\uFA2D'))
			return true;
		return false; 
	}
	
	
}
