package org.xh.xs.dic;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;
import org.xh.xs.dic.DATrie;
import org.xh.xs.util.ResLoader;

public class TestDATrie {
	 String out="aa";
	public   void aa(String ss){
		System.out.println(out==ss);
	}
	@Test
	public  void testSave() throws Exception {
		DATrie dat=null;
		String line;
		BufferedReader br=null;
		StringBuffer sb=new StringBuffer("");
		dat=new DATrie(DATrie.InitType.Empty);
		br=ResLoader.getReader("vancl.dic", true);
		while((line=br.readLine())!=null){
			if((line=line.trim()).equals(""))continue;
			sb.append(line.trim());
			dat.add(sb.reverse().toString());
			sb.setLength(0);
		}
		dat.show();
		dat.save();br.close();
//		dat=new DATrie(DATrie.InitType.NOT_Empty);
		br=ResLoader.getReader("vancl.dic", true);
		while((line=br.readLine())!=null){
			sb.append(line.trim());
			boolean exist=dat.contains(sb.reverse().toString());
			if(!exist)System.out.println(line);
			sb.setLength(0);
		}
		br.close();
	}
	@Test
	public void testTrie(){
		DATrie dat=new DATrie(DATrie.InitType.NOT_Empty);
		System.out.println(dat.contains("力给"));
	}
	
	@Test
	public void testMaxMatch() throws UnsupportedEncodingException{
		DATrie dat=new DATrie(DATrie.InitType.Empty);
		dat.add("ybaaaab");
		dat.add("baab");
		int len=dat.maxMatch("asbabaa&by",9);
		Assert.assertEquals(len, 2);
	}
}