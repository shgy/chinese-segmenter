package org.sgy.tokenizer;

import java.util.Arrays;

import org.junit.Test;
import org.sgy.tokenizer.Tokenizer;


public class TestTokenizer {
	
	Tokenizer s = new Tokenizer();
	//京华 | 时报 | 2008 | 年 | 1 | 月 | 23 | 日 | 报道 | 昨天 | 受 | 一股 | 来自 | 中 | 西伯利亚 | 的 | 强 | 冷空气 | 影响 | 本市 | 出现 | 大风 | 降温 | 天气 | 白天 | 最高气温 | 只有 | 零下 | 7 | 摄氏度 | 同时 | 伴有 | 6 | 到 | 7 | 级 | 的 | 偏 | 北风

	@Test
	public void testSegment(){
		String[] terms =s.segment("京华时报２００８年1月23日报道 昨天，受一股来自中西伯利亚的强冷空气影响，本市出现大风降温天气，白天最高气温只有零下7摄氏度，同时伴有6到7级的偏北风。");
		System.out.println(Arrays.asList(terms));
	}
}
