package org.sgy.tokenizer;


/**
 * @author shuaiguangying
 * * 它是MMSeg分词算法中一个关键的概念。
 * Chunk中包含依据上下文分出的一组词和相关的属性，
 * 包括长度(Length)、
 * 平均长度(Average Length)、
 * 标准差的平方(Variance)和
 * 自由语素度(Degree Of Morphemic Freedom)。
 * 
 * 按照网页的说法，一个Chunk中只有3个Word是性能和正确率一个折中方案。
 * 一个简单句式 = 主语+谓语+宾语 
 * 
 * Chunk里面的初始值都赋值为-1是一种懒操作(lazy)的做法
 * 即用到的时候再计算，而且只计算一次
 */
public class Chunk {
	//
	 Word[] words = new Word[3];
	
	int count = -1;
	
	/** Word Length */
	private int len = -1;
	/** Largest Average Word Length */
	private double avgLen = -1;
	/** Variance of Word Lengths 就是 标准差的平方 */
	private double variance = -1;
	/** Sum of Degree of Morphemic Freedom of One-Character */
	private int sumDegree = -1;
	
	/** Word Length */
	public int getLen() {
		if(len < 0) {
			len = 0;
			count = 0;
			for(Word word : words) {
				if(word != null) {
					len += word.getLength();
					count++;
				}
			}
		}
		return len;
	}
	
	/** 有多少个词，最多3个。*/
	public int getCount() {
		if(count < 0) {
			count = 0;
			for(Word word : words) {
				if(word != null) {
					count++;
				}
			}
		}
		return count;
	}
	
	/** Largest Average Word Length */
	public double getAvgLen() {
		if(avgLen < 0) {
			avgLen = (double)getLen()/getCount();
		}
		return avgLen;
	}
	
	/** Variance of Word Lengths 就是 标准差的平方 */
	public double getVariance() {
		if(variance < 0) {
			double sum = 0;
			for(Word word : words) {
				if(word != null) {
					sum += Math.pow(word.getLength()-getAvgLen(), 2);
				}
			}
			variance = sum/getCount();
		}
		return variance;
	}
	
	/** Sum of Degree of Morphemic Freedom of One-Character */
	public int getSumDegree() {
		if(sumDegree < 0) {
			int sum = 0;
			for(Word word : words) {
				if(word != null && word.getDegree() > -1) {
					sum += word.getDegree();
				}
			}
			sumDegree = sum;
		}
		return sumDegree;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Word word : words) {
			if(word != null) {
				sb.append(word.getText()).append('_');
			}
		}
		return sb.toString();
	}
	
	public Word[] getWords() {
		return words;
	}
}
