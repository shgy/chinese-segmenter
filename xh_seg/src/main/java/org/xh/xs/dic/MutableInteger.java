package org.xh.xs.dic;

public class MutableInteger {
	private int val;
	
	public MutableInteger(int val) {
		this.val = val;
	}
	
	public void increment(int inc){
		this.val+=inc;
	}
	
	public int intVal() {
		return val;
	}
	

	public void setVal(int val) {
		this.val = val;
	}

}
