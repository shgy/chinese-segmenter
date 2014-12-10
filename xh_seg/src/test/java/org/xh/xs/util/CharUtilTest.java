package org.xh.xs.util;
import org.junit.Test;
import org.xh.xs.util.CharUtil;




public class CharUtilTest {
	@Test
	public void testSW(){
		String str="";
		System.out.println(CharUtil.sw(str));
	}
	@Test
	public void testFilter(){
		String line="鞋子不错！很喜欢！找企鹅4 0 3 8 1 6 3 7 3 购买有优惠哦！";
		System.out.println(CharUtil.filter(line));
	}
}
