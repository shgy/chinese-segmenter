package org.xh.xs.core;

import java.util.Arrays;

import org.junit.Test;

public class PosTest {
	@Test
	public void test(){
		TermInfo[] pos={new TermInfo("",1,2),new TermInfo("",5,2),new TermInfo("", 3,2)};
		Arrays.sort(pos);
		for (TermInfo pos2 : pos) {
			System.out.println(pos2.s);
		}
	}
}
