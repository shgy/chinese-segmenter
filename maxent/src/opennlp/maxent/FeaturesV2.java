package opennlp.maxent;


public class FeaturesV2 {
	private final char[] text;
	//特征TC5
	private final char[] textTypes;
	
	public FeaturesV2(String text){
		this(text.toCharArray());
	}
	
	public FeaturesV2(char[] text){
		this.text = text;
		textTypes = new char[this.text.length];
		for (int i = 0; i < this.text.length; i++) {
			textTypes[i]=featype(this.text[i]);
		}
	}
	
	private static char featype(char ch){
		if(CharUtil.isdigit(ch))
			return '1';
		else if(CharUtil.isdateunit(ch))
			return '2';
		else if(CharUtil.isletter(ch))
			return '3';
		return '4';
	}

	public String[] getFeatures(int idx){
		StringBuilder feas =new StringBuilder();//线程安全，高效
		feCn(feas,idx); //first feature
		feCnCn1(feas,idx); //second feature
		feC_1C1(feas,idx); //third feature
		fePunc(feas,idx); //forth feature
		feTC5(feas,idx); //fifth feature
		return feas.toString().trim().split("\\s+");
	}
	
	private void addFeaCn(StringBuilder feas,int idx){
		feas.append(this.text[idx]);feas.append(' ');
	}
	//提取特征a): cn (n=-2,-1,0,1,2)
	public void feCn(StringBuilder feas,int idx){
		int len=this.text.length;
		feas.append("C0=");addFeaCn(feas,idx);
		if(idx-2>=0){feas.append("C_2="); addFeaCn(feas,idx-2);}
		if(idx-1>=0){feas.append("C_1="); addFeaCn(feas,idx-1);}
		if(idx+1<len){feas.append("C1=");addFeaCn(feas,idx+1);}
		if(idx+2<len){feas.append("C2=");addFeaCn(feas,idx+2);}
	}
	private void addFeaCnCn1(StringBuilder feas,int idx){
		feas.append(this.text[idx]);
		feas.append(this.text[idx+1]);
		feas.append(' ');
	}
	//提取特征b): CnCn+1 (n=-2,-1,0,1)
	public void feCnCn1(StringBuilder feas,int idx){
		int len=this.text.length;
		if(idx-2>=0){feas.append("C_2_1=");addFeaCnCn1(feas,idx-2);}
		if(idx-1>=0){feas.append("C_10=");addFeaCnCn1(feas,idx-1);}
		if(idx+1<len){feas.append("C01=");addFeaCnCn1(feas,idx);}
		if(idx+2<len){feas.append("C12=");addFeaCnCn1(feas,idx+1);}
	}
	//提取特征c): C-1C1
	public void feC_1C1(StringBuilder feas,int idx){
		int len=this.text.length;
		if(len<3||idx==0||idx==len-1)return;
		feas.append("C_11=");
		feas.append(this.text[idx-1]);
		feas.append(this.text[idx+1]);
		feas.append(' ');
	}
	//提取特征Pu(C0)
	public void fePunc(StringBuilder feas,int idx){
		String ans = CharUtil.ispunc(this.text[idx])?"1":"0";
		feas.append("Pu=");feas.append(ans);feas.append(' ');
	}
	
	//#提取特征e): T(C-2)T(C-1)T(C0)T(C1)T(C2)
	public  void feTC5(StringBuilder feas,int idx){
		int len=this.text.length;
		feas.append("TC=");
		if(idx-2>=0)feas.append(textTypes[idx-2]);
		if(idx-1>=0)feas.append(textTypes[idx-1]);
					feas.append(textTypes[idx]);
		if(idx+1<len)feas.append(textTypes[idx+1]);
		if(idx+2<len)feas.append(textTypes[idx+2]);
	}
}
