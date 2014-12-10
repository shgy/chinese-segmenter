package org.nlpir.ictclas;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class NLPIR_ICTCLAS {
	   static String ict_path =  System.getProperty("ict-path");
	  
	// 定义接口CLibrary，继承自com.sun.jna.Library
		public interface CLibrary extends Library {
			// 定义并初始化接口的静态变量
			CLibrary Instance = ict_path==null?
					(CLibrary) Native.loadLibrary("lib/win32/NLPIR", CLibrary.class):
					(CLibrary) Native.loadLibrary(ict_path+File.separator+"lib/win32/NLPIR", CLibrary.class);
			
			public int NLPIR_Init(String sDataPath, int encoding,
					String sLicenceCode);
					
			public String NLPIR_ParagraphProcess(String sSrc, int bPOSTagged);

			public String NLPIR_GetKeyWords(String sLine, int nMaxKeyLimit,
					boolean bWeightOut);
			public String NLPIR_GetFileKeyWords(String sLine, int nMaxKeyLimit,
					boolean bWeightOut);
			public int NLPIR_AddUserWord(String sWord);//add by qp 2008.11.10
			public int NLPIR_DelUsrWord(String sWord);//add by qp 2008.11.10
			public String NLPIR_GetLastErrorMsg();
			public void NLPIR_Exit();
		}
		
		static{
			// String system_charset = "GBK";//GBK----0
			// String argu = ".";
			int charset_type = 1;
			int init_flag = CLibrary.Instance.NLPIR_Init(ict_path==null?".":ict_path, charset_type, "0");
			String nativeBytes = null;

			if (0 == init_flag) {
				nativeBytes = CLibrary.Instance.NLPIR_GetLastErrorMsg();
				System.err.println("初始化失败！fail reason is "+nativeBytes);
			}

		}
		private Reader input;
		private char[] buf = new char[512];
		private String[] results = null;
		private int rtIdx;
		public NLPIR_ICTCLAS(Reader input){
			if(input==null)throw new IllegalArgumentException("input should not be null!");
			this.input = input;
		}
		
//		private int offInOrig(char termStart,int start){
//			int offset = start;
//			while(offset<buf.length && Character.isWhitespace(buf[offset]))offset++;
//			if(buf[offset]!=termStart)throw new RuntimeException("分词错误！当前字符为:"+termStart+",原文中的字符为："+buf[offset]);
//			return offset;
//		}
		/** 非线程安全
		 * @return
		 * @throws IOException
		 */
		public ICTToken next() throws IOException{
			if(results==null||rtIdx >= results.length){
				rtIdx = 0;
				
				int count = input.read(buf,0,buf.length);
				if(count==-1)return null;
				
				String tokseq = segment(String.valueOf(buf, 0, count));
				results = toArray(tokseq);
				if(results==null)return null;
			}
			
			String term =results[rtIdx++];
			ICTToken tok = termToToken(term);
			return tok;

		}
		
		private String[] toArray(String tokseq){
			int len = tokseq.length();
			ArrayList<String> list = new ArrayList<String>();
			int i=0,s,e;
			while(true){
				while(i<len && Character.isWhitespace(tokseq.charAt(i))) i++;
				s=i;
				while(i<len && !Character.isWhitespace(tokseq.charAt(i))) i++;
				e=i;
				if(s>=e)break;
				list.add(tokseq.substring(s, e));
			}
			return list.isEmpty()?null:list.toArray(new String[list.size()]);
		}
		
		private ICTToken termToToken(String ictTerm){
			int slash = ictTerm.lastIndexOf('/');
			if(slash==-1) return new ICTToken(ictTerm,"unknown");
			return new ICTToken(ictTerm.substring(0, slash),ictTerm.substring(slash+1, ictTerm.length()));
		}
		
		public String segment(String input) throws UnsupportedEncodingException{
			return CLibrary.Instance.NLPIR_ParagraphProcess(input, 1);
		}
		
		public void exit(){
			 CLibrary.Instance.NLPIR_Exit();
		}
}
