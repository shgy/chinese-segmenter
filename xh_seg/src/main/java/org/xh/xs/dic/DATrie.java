package org.xh.xs.dic;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.xh.xs.util.CharUtil;
import org.xh.xs.util.ResLoader;

/*
 * @author ShuaiGuangying
 * 用DoubleArrayTrie实现的分词词典
 * 
 * 
 * */
public class DATrie {
	private static final  int DEF_LEN=16;
	private static final  char END_TAG='#';//#在CWC中的value=1
	private static final int END_VAL=1;
	private static final String DIC_NAME="Main.dic";
	
	public static enum  InitType{
		Empty,NOT_Empty
	}
	private  Map<Character,MutableInteger> codeTable;
	
	private int[] base;
	
	private int[] check;
	
	private char[] tail;
	private int tailPos;
	 
	/*
	 * 初始化一个包含词典文件的DATrie
	 * */
	public DATrie(InitType empty){
		switch(empty){
		case Empty: init();
			break;
		case NOT_Empty: load();
			break;
		default: throw new RuntimeException("init error!");
		}
	}
	
	private void init(){
		base  = new int[DEF_LEN];
		check = new int[DEF_LEN];
		tail  = new char[DEF_LEN];
		codeTable=new ConcurrentHashMap<Character,MutableInteger>();
		base[1]=1;tailPos=1;
		codeTable.put(END_TAG,new MutableInteger(END_VAL));
	}

	public int maxMatch(final char[] sen,int begin){
		int pre=1,cur=0,i;
		int _max=0;
		for(i=begin;i>=0;--i){
			int val=sValueOf(sen[i]);
			//判断该位置是否连接着一个结束符
			if(check[base[pre]+END_VAL]==pre)
				_max=Math.max(_max,begin-i);
			if(val==-1){
				return _max;
			}
			cur=base[pre]+val;
			if(cur>=base.length)return _max;
			
			if(check[cur] != pre){
				return _max;
			}
			//到tail数组中去查询
			if(base[cur]<0 )
				return Math.max(_max,MatchInTail2(sen,begin, i-1, -base[cur]));
			pre=cur;
		}
		if(check[base[cur]+END_VAL]==cur)_max=Math.max(_max, begin-i);
		return _max;
	}
	/* 
	 * 从begin位置开始，逆向匹配词典中最长的词
	 * */
	public int maxMatch(String sen,int begin){
		return maxMatch(sen.toCharArray(),begin);
	}
	//可以计算匹配的长度
	private int MatchInTail2(char[] sen, int begin,int start, int head) {
		char ch;
		while(start>=0){
			ch=sen[start];
			if(CharUtil.isCJKCharacter(ch)||CharUtil.isEnglish(ch)||CharUtil.isNumber(ch)){
				if(ch!=tail[head]){
					if(tail[head]==DATrie.END_TAG)return begin-start;
					return 0;
				}
			}else{
				return begin-start;
			}
			--start;++head;
		}
		if(tail[head]==DATrie.END_TAG)return begin-start;
		return 0;
	}

	/*
	 * 查找一个词是否在Trie树结构中
	 * */
	public boolean contains(String word){
		word=CharUtil.sw(word);//全角转半角，大写转小写
		//执行查询操作
		int pre=1,cur=0;
		for(int i=0,len=word.length();i<len;++i){
			int val=sValueOf(word.charAt(i));
			if(val==-1)return false;
			cur=base[pre]+val;
			
			if(cur>=base.length)return false;
			
			if(check[cur] != pre)return false;
			
			//到tail数组中去查询
			if(base[cur]<0 ){
				int head=-base[cur];
				return MatchInTail(word, i+1, head);
			}
			pre=cur;
		}
		//这一句是关键，对于一个串是另一个字符串 子串的情况
		if(check[base[cur]+END_VAL]==cur)return true;
		return false;
	}
	
	public  void ensureIllegal(String word){
		//if(word.contains("#"))	throw new IllegalArgumentException(word+" can not contains # ");
		for(int i=0;i<word.length();i++){
			char ch=word.charAt(i);
			if(!CharUtil.isCJKCharacter(ch)&&!CharUtil.isEnglish(ch)&&!CharUtil.isNumber(ch)){
				throw new IllegalArgumentException(word+" can only contains CJKCharacter or English letters or Arbic Numbers ");
			}
		}
	}
	
	public void add(String word){	
		ensureIllegal(word);
		word=CharUtil.sw(word);//全角转半角，大写转小写
		word+=END_TAG;
		int curValue;
		for(int i=0,pre=1,cur,len=word.length();i<len;i++){
			cur=base[pre]+(curValue=valueOf(word.charAt(i)));
			//容量不够的时，扩容
			if(cur>=base.length)extend();
			
			//空白位置，可以添加，这里需要注意的是如果check[cur]=0,则base[cur]=0成立
			if( check[cur] == 0){
				check[cur]=pre;
				if(i+1<len){
					base[cur]=-tailPos;
					toTail(word,i+1); //把剩下的字符串存储到tail数组中
				}
				return;//当前词已经插入到DATire中
			}
			//公共前缀，直接走
			if(check[cur]==pre && base[cur]>0 ){
				pre=cur;continue;
			}
			//遇到压缩到tail中的字符串，有可能是公共前缀
			if(check[cur] == pre && base[cur]<0 ){
				//是公共前缀，把前缀解放出来
				int new_base_value,head;
				head=-base[cur];
				
				//插入相同的字符串
				if(tail[head]==END_TAG && word.charAt(i+1)== END_TAG)
					return ;
				
				if(tail[head]==word.charAt(i+1)){
					int ncode=valueOf(word.charAt(i+1));
					new_base_value=x_check(new Integer[]{ncode});
					//解放当前结点
					base[cur]=new_base_value;
					//连接到新的结点
					base[new_base_value+ncode]=-(head+1);
					tail[head]=0;
					check[new_base_value+ncode]=cur;
					//把边推向前一步,继续
					pre=cur;continue;
				}
				/*
				 * 两个字符不相等,这里需要注意"一个串是另一个串的子串的情况"
				 * */
				int tailH=valueOf(tail[head]),nextW=valueOf(word.charAt(i+1));
				
				new_base_value=x_check(new Integer[]{tailH,nextW});
				
				base[cur]=new_base_value;
				//确定父子关系
				check[new_base_value+tailH] = cur;
				check[new_base_value+nextW] = cur;
				
				//处理原来tail的首字符
				base[new_base_value+tailH] = (tail[head] == END_TAG) ? 0 : -(head+1);
				tail[head]=0;
				//处理新加进来的单词后缀
				base[new_base_value+nextW] = (word.charAt(i+1) == END_TAG) ? 0 : -tailPos;
				
				toTail(word,i+2); return;
			} 
			/*
			 * 冲突:当前结点已经被占用，需要调整pre的base
			 * 这里也就是整个DATrie最复杂的地方了
			 * */
			if(check[cur] != pre){
				int adjustBase=pre;
				Integer[] list = children(pre);//父结点的所有孩子
				Integer[] tmp = children(check[cur]);//产冲突结点的所有孩子
				
				int new_base_value;
				if(tmp.length <= list.length+1){
					list = tmp;
					adjustBase = check[cur];
					new_base_value = x_check(list);
				}else{
					//由于当前字符也是结点的孩子，所以需要把当前字符加上
					list=Arrays.copyOf(list, list.length+1);
					list[list.length-1]=curValue;
					new_base_value=x_check(list);
					//但是当前字符 现在并不是他的孩子，所以暂时先需要去掉
					list=Arrays.copyOf(list, list.length-1);
				}
				 tmp = null;
				 
				int old_base_value=base[adjustBase];
				
				base[adjustBase]=new_base_value;
				
				int old_pos,new_pos;
				//处理所有节点的冲突
				for(int j=0;j<list.length;j++){
					old_pos=old_base_value+list[j];
					new_pos=new_base_value+list[j];
					
					/*下面这句代码我用了至少3天才找到的,
					曾多次反复参看论文并在纸上画图构思DATrie树的结构
					如果没有 if(pre==old_pos)pre=new_pos;
					这句代码，测试插入论文中的例举数据
					"bachelor","jar","badge","baby"不会出问题：
					但是插入如下字符串会出问题
					"bac","bacd","be","bae"
					*/
					if(pre==old_pos)pre=new_pos;
					
					//把原来老结点的信息迁移到新节点上
					base[new_pos]=base[old_pos];
					check[new_pos]=check[old_pos];
					//有后续,所有孩子都用新的父亲替代原来的父亲
					if(base[old_pos]>0){
					   tmp=children(old_pos);
						for (int k = 0; k < tmp.length; k++) {
							check[base[old_pos]+tmp[k]] = new_pos;
						}
					}
					//释放废弃的节点空间
					base[old_pos]=0;
					check[old_pos]=0;
				}
				//冲突处理完毕，把新的单词插入到DATrie中
				cur=base[pre]+curValue;
				base[cur]=(word.charAt(i)==END_TAG)?0:-tailPos;
				check[cur]=pre;
				
				toTail(word,i+1);return;//这里不能忘记了
			}
		}
	}
	
	
	//到Tail数组中进行比较
	private boolean MatchInTail(String word,int start,int head){
		word+=END_TAG;
		int len=word.length();
		while(start<len){
			if(word.charAt(start++)!=tail[head++])return false;
		}
		return true;
	}
	/*
	 * 寻找最小的q,q要满足的条件是：q>0 ,并且对于list中所有的元素都有check[q+c]=0
	 * */
	private int x_check(Integer[] c){
		int cur,q=1,i=0;
		 do{
			cur = q + c[i++];
			if(cur >= check.length)extend();
			if(check[cur] != 0 ){
				i=0;++q;
			}
		}while(i<c.length);
		return q;
	}
	/*
	 * @param  DATrie的结点
	 * @return 连接到pos所有孩子的弧的值
	 * */
	private Integer[] children(int pos){
		if(base[pos]<0)return null;
		ArrayList<Integer> c=new ArrayList<Integer>();
		
		for(int i=1,hSize=codeTable.size();i<=hSize;i++){
			if(base[pos] + i >= check.length)break;
			if(check[base[pos]+i] == pos)c.add(i);
		}
		return c.toArray(new Integer[c.size()]);
	}
	
	/*
	 * 供检索的时候进行编号，与valueOf函数相比，
	 * 如果有不存在的字符，remove会耗费时间
	 * */
	private synchronized int sValueOf(char ch){
		int _hSize=codeTable.size();
		MutableInteger newValue=new MutableInteger(_hSize+1);
		MutableInteger oldValue=codeTable.put(ch,newValue);

		if(oldValue==null){
			codeTable.remove(newValue);
			return -1;
		}
		newValue.setVal(oldValue.intVal());
		oldValue=null;
		return newValue.intVal();
	}
	
	private synchronized int valueOf(char ch){
		int _hSize=codeTable.size();
		MutableInteger newValue=new MutableInteger(_hSize+1);
		MutableInteger oldValue=codeTable.put(ch,newValue);

		if(oldValue==null)
			return ++_hSize;
		
		newValue.setVal(oldValue.intVal());
		
		oldValue=null;
		return newValue.intVal();
	}
	//将字符串的后缀存储到tail数组中
	private synchronized void toTail(String w,int pos){
		//如果容量不足，就扩容
		if(tail.length-tailPos < w.length()-pos)
			tail=Arrays.copyOf(tail, tail.length<<1);
		
		while(pos<w.length()){
			tail[tailPos++]=w.charAt(pos++);
		}
	}
	
	private void extend(){
		base=Arrays.copyOf(base, base.length<<1);
		check=Arrays.copyOf(check, check.length<<1);
	}
	/*
	 * 把词典内容加载到
	 * */
	private void load(){
		InputStream is=DATrie.class.getResourceAsStream(DATrie.DIC_NAME);
		if(is==null) throw new RuntimeException(DATrie.DIC_NAME +" is not exist!");
		
		DataInputStream dis=null;
		
		try {
			dis=new DataInputStream(is);
			
			int codeTableSize=dis.readInt();
			codeTable=new ConcurrentHashMap<Character, MutableInteger>(codeTableSize);
			for(int i=0;i<codeTableSize;i++){
				codeTable.put((char) dis.readInt(), new MutableInteger( dis.readInt()));
			}
			//base数组
			int tLen=dis.readInt();
			base=new int[tLen];
			for (int i=0;i<tLen;i++) {
				base[i]=dis.readInt();
			}
			//check数组
			tLen=dis.readInt();
			check=new int[tLen];
			for (int i=0;i<tLen;i++) {
				check[i]=dis.readInt();
			}
			//tail数组
			tLen=dis.readInt();
			tail=new char[tLen];
			for(int i=0;i<tLen;i++){
				tail[i]=(char) dis.readInt();
			}
			tailPos=dis.readInt();
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(is!=null) is.close();
				if(dis!=null) dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * 生成DATrie词典
	 * */
	public void save() throws IOException{
		DataOutputStream dos=ResLoader.open_outStream(DATrie.DIC_NAME);
		if(dos==null)return;
		//保存codeTable size
		dos.writeInt(codeTable.size());
		//保存codeTable
		for (Map.Entry<Character, MutableInteger> entry:codeTable.entrySet()) {
			dos.writeInt(entry.getKey());
			dos.writeInt(entry.getValue().intVal());
		}
		//保存base数组
		dos.writeInt(base.length);
		for (int b : base) {
			dos.writeInt(b);
		}
		//保存check数组
		dos.writeInt(check.length);
		for (int c : check) {
			dos.writeInt(c);
		}
		//保存tail数组
		dos.writeInt(tail.length);
		for (int t : tail) {
			dos.writeInt(t);
		}
		dos.writeInt(tailPos);
		
		dos.close();
	}
	public void show(){
		System.out.println(base.length);
		System.out.println(check.length);
		System.out.println(tail.length);
	}
}
