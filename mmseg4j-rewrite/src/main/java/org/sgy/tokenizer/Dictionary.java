package org.sgy.tokenizer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
	 * @author shuaiguangying
	 *	分词器的词典，由于词典中词的个数不多，直接用Trie树即可
	 */
	public class Dictionary{
		Map<Character,Object> unitsMap = new HashMap<Character,Object>();
		Map<Character,Integer> degreeMap = new HashMap<Character,Integer>();
		Trie wordsTrie = new Trie();
		
		public boolean isUnit(char ch){
			return unitsMap.containsKey(ch);
		}
		
		public void addTerm(String term){
			wordsTrie.add(term.toCharArray());
		}
		
		public int[] matchAll(char[] sen,int offset){
			return wordsTrie.matchAll(sen, offset);
		}
		
		//单例
		static class ResourceHolder{
			static Dictionary dic = new Dictionary();
		}

		public static Dictionary getInstance(){
			return ResourceHolder.dic ;
		}
		private Dictionary(){
			try {
				init();
			} catch (Exception e) {
				throw new RuntimeException("Could not init dic normally!");
			}
		}
		private void init() throws IOException {
			String dirName = Dictionary.class.getResource("/data").getFile();
			File[] files = new File(dirName).listFiles();
			for (File file : files) {
				//加载自由语素
				if(file.getName().equals("chars.dic")){
					readByLine(file,degreeProcessor);
				//加载单位标识
				}else if(file.getName().equals("units.dic")){
					readByLine(file,unitsProcessor);
				//加载词典(可以是多个文件)
				}else if(file.getName().startsWith("words")){
					readByLine(file,wordsProcessor);
				}
//				else if(file.getName().equals("stop")){//加载停用词典
//					readByLine(file,stopProcessor);
//				}
			}
		}

		private void readByLine(File file,Processor processor) throws IOException{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line ;
			while((line = br.readLine())!=null){
				try {
					processor.processLine(line);
				} catch (Exception e) {}//忽略一行数据的错误
			}
			br.close();
		}
		
		class Trie{
			Node root = new Node();
			
			public void add(char[] term){
				Node node = root;
				
				Map<Character,Node> next ;
				for (int i=0;i<term.length;i++) {
					next = node.next();
					node = next.get(term[i]);
					if(node==null){
						node = new Node();
						next.put(term[i], node);
					}
				}
				node.isEnd = true;
			}
			/**
			 * @param sen
			 * @param offset
			 * @return 所有词尾在sen中的位置
			 */
			public int[] matchAll(char[] sen,int offset){
				int[] rs = new int[1];
				if(offset>=sen.length){
					rs[0]=sen.length-1;
					return rs;
				}
				rs[0]=offset;
				int idx = 1;
				if(root.next == null) return rs;
				
				Map<Character,Node> next = root.next;
				Node node = null;
				while(offset<sen.length){
					char ch = sen[offset++];
					node = next.get(ch);
					if(node==null) return rs;
					if(node.isEnd){
						if(idx>=rs.length){
							int[] newArr = new int[idx+1];
							System.arraycopy(rs, 0, newArr, 0, idx);
							rs = newArr;
						}
						rs[idx++]=offset-1;
						//词典已经到了尽头
						if(node.next==null) return rs;
					}
					next = node.next;
				}
				
				return rs;
			}
			
			class Node{
				Map<Character,Node> next ;
				boolean isEnd ;
				public Map<Character,Node> next(){
					if(next==null)
						next = new HashMap<Character,Node>();
					return next;
				}
				
			}

			/**
			 * @param sen
			 * @param offset
			 * @return 长度最大的词在sen中的位置
			 */
			public int maxMatch(char[] sen, int offset) {
				if(offset>=sen.length) return sen.length-1;
				int max = offset;
				if(root.next == null) return offset;
				
				Map<Character,Node> next = root.next;
				Node node = null;
				while(offset<sen.length){
					char ch = sen[offset++];
					node = next.get(ch);
					if(node==null) return max;
					if(node.isEnd){ //找到一个词，但是不确定是否是最大的词
						max = offset-1;
						//词典已经到了尽头
						if(node.next==null) return max;
					}
					next = node.next;
				}
				return max;
			}
			
		}
		/**
		 * @param sen
		 * @param offset
		 * @return 在词典中的最长的一个词
		 */
		public int maxMatch(char[] sen, int offset) {
			return wordsTrie.maxMatch(sen,offset);
		}
		
		public int getDegree(char ch){
			Integer d =  degreeMap.get(ch);
			return d ==null?0:d;
		}
		
		interface Processor{void processLine(String line);}
		private Processor unitsProcessor = new Processor(){
			@Override
			public void processLine(String line) {
				if(line.startsWith("#")||line.isEmpty()) return;
				unitsMap.put(line.charAt(0), Dictionary.class);
			}
		};
		private Processor degreeProcessor = new Processor(){
			@Override
			public void processLine(String line) {
				if(line.startsWith("#")||line.isEmpty()) return;
				String[] data = line.split("\\s+");
				degreeMap.put(data[0].charAt(0), Integer.parseInt(data[1]));
			}
		};
		private Processor wordsProcessor = new Processor(){
			@Override
			public void processLine(String line) {
				if(line.startsWith("#")||line.isEmpty()) return;
				wordsTrie.add(line.trim().toCharArray());
			}
		};
	}