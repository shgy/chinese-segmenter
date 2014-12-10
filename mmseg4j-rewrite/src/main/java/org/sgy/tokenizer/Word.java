package org.sgy.tokenizer;
/**
	 * @author shuaiguangying
	 * 无状态的对象，线程安全
	 */
	public class Word{
		private int degree;
		private int offset;
		private int len;
		private char[] text;
		public Word(char[] text,int offset,int len){
			this.text= text;
			this.offset = offset;
			this.len = len;
		}
		
		public int getDegree() {
			return degree;
		}
		public void setDegree(int degree) {
			this.degree = degree;
		}
		public int getLength() {
			return len;
		}
		public String getText(){
			return String.valueOf(text, offset, len);
		}
		
		@Override
		public String toString(){
			return String.valueOf(text,offset,len);
		}
	}