package org.sgy.tokenizer;

/**
 * @author shuaiguangying
 * 正向最大匹配, 加四个过虑规则的分词方式.
 */
public class MMSeg {
	private Dictionary dic;
	//默认有4个规则
	private Rule[] rules = new Rule[4];
	private int idx = 0;
	
	public MMSeg(Dictionary dic){
		this.dic = dic;
		addRule(new MaxMatchRule(1));
		addRule(new LargestAvgLenRule(2));
		addRule(new SmallestVarianceRule(3));
		addRule(new LargestSumDegreeFreedomRule(4));
	}
	
	public Chunk segment(char[] sen,int offset){
		Chunk  best = null;
		// 遍历所有不同词长 从最长的开始，到0；可以减少一部分多余的查找.
		int[] tailPos = new int[3];//记录词的尾在sen中的位置
		int[] tail1 = dic.matchAll(sen, offset);
		for(int i=tail1.length-1;i>=0;i--){
			tailPos[0] = tail1[i];
			//第二个词
			int[] tail2 = dic.matchAll(sen, tail1[i]+1);
			for(int j=tail2.length-1;j>=0;j--){
				tailPos[1] = tail2[j];
				//第三个词只取最长的
				int tail3 = dic.maxMatch(sen,tail2[j]+1);
				tailPos[2]=tail3;
				Chunk chunk= createChunk(sen,offset,tailPos);
				best = addChunk(best,chunk);
			}
		}
		return best;
	}
	
	
	private Chunk createChunk(char[] sen, int offset, int[] tailPos) {
		Chunk c = new Chunk();
		int start=offset,len;
		for(int i=0;i<tailPos.length;i++){
			len = tailPos[i]+1-start;
			if(len == 0)break;
			c.words[i] = new Word(sen,start,len);
			if(len==1){//单字要记录 自由语素度
				c.words[i].setDegree(dic.getDegree(sen[start]));
			}
			start = tailPos[i]+1;
		}
		return c;
	}

	public void addRule(Rule rule){
		if(idx >= rules.length){
			Rule[] newArr = new Rule[idx+1];
			System.arraycopy(rules, 0, newArr, 0, idx);
			rules = newArr;
		}
		rules[idx++] = rule;
	}
	
	
	public Chunk addChunk(Chunk pre,Chunk cur){
		if(pre == null) {
			return cur;
		}
		
		for (Rule rule : rules) {
			if(rule.choose(cur,pre)>0){
				return cur;
			}else if(rule.choose(cur, pre)<0){
				return pre;
			}
		}
		//如果经过4个规则过滤后，依然判断不出来，那么就返回前面的一个，即【前向最大匹配】
		return pre;
	}
	
	public abstract class Rule implements Comparable<Rule>{
		protected int priority;
		/** 
		 * @param priority 规则的优先级
		 */
		public Rule(int priority){
			this.priority = priority;
		}
		abstract int choose(Chunk c1,Chunk c2);
		
		int getPriority(){return this.priority;}
		@Override
		public int compareTo(Rule o) {
			return this.priority - o.getPriority();
		}
	}
	/**
	 * @author shuaiguangying
	 * 选择长度最大的Chunk
	 */
	private class MaxMatchRule extends Rule {
		public MaxMatchRule(int priority){
			super(priority);
		}
		@Override
		public int choose(Chunk c1, Chunk c2) {
			return c1.getLen() - c2.getLen();
		}
	}
	/**
	 * @author shuaiguangying
	 *	选择平均长度最大的Chunk
	 */
	private class LargestAvgLenRule extends Rule {
		public LargestAvgLenRule(int priority){
			super(priority);
		}
		@Override
		public int choose(Chunk c1, Chunk c2) {
			return Double.compare( c1.getAvgLen(), c2.getAvgLen());
		}
	}
	/**
	 * @author shuaiguangying
	 * 取词长方差最小的chunk
	 */
	private class SmallestVarianceRule extends Rule {
		public SmallestVarianceRule(int priority){
			super(priority);
		}
		@Override
		public int choose(Chunk c1, Chunk c2) {
			//这里是求最小的
			return Double.compare(c2.getVariance(), c1.getVariance());
		}
	}
	/**
	 * @author shuaiguangying
	 * 选择自由语素度最大的Chunk
	 */
	private class LargestSumDegreeFreedomRule extends Rule {
		public LargestSumDegreeFreedomRule(int priority){
			super(priority);
		}
		@Override
		public int choose(Chunk c1, Chunk c2) {
			return c1.getSumDegree()-c2.getSumDegree();
		}
	}
}
