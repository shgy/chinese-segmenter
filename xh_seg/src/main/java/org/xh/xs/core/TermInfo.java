package org.xh.xs.core;

public final class TermInfo implements Comparable<TermInfo>{
	public final String term;
	public final int s;
	public final int c;
	public TermInfo(String term,int s,int c){
		this.term=term;
		this.s=s;
		this.c=c;
	}
	public TermInfo(char[] text,int s,int c){
		this.term=String.valueOf(text, s, c);
		this.s=s;
		this.c=c;
	}
	//根据开始位置进行排序
	public int compareTo(TermInfo o) {
		return this.s-o.s;
	}
}
