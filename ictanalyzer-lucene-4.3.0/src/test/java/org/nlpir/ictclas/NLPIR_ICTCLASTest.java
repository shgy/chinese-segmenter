package org.nlpir.ictclas;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import junit.framework.TestCase;

import org.junit.Test;

public class NLPIR_ICTCLASTest extends TestCase {
	
	private Reader dataSource(String file) throws FileNotFoundException, UnsupportedEncodingException{
		Reader reader = new InputStreamReader(new FileInputStream(new File(file)),"utf-8");
		return reader;
	}
	
	private Reader dataSource2(){
		return new StringReader("大小刚好，穿着也不错，图片和实物颜色有点差别");
	}
	
	@Test
	public void testString() throws UnsupportedEncodingException{
		NLPIR_ICTCLAS ict = new NLPIR_ICTCLAS(dataSource2());
		String line = "..^^不过还是很满意的";
		line = ict.segment(line);
		System.out.println(line);
	}
}
