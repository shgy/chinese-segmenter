package org.nlpir.ictclas;

public class ICTToken {
	private String term;
	private String type;
	private int termLen;
	public ICTToken(String term,String type){
		this.term = term;
		this.type = type;
		this.termLen = term.length();
	}
	public String getTerm() {
		return term;
	}
	public String getType() {
		return type;
	}
	
	public int getTermLen(){
		return termLen;
	}
	@Override
	public String toString() {
		return "ICTToken [term=" + term + ", type=" + type + ",termLen=" + termLen + "]";
	}
}
