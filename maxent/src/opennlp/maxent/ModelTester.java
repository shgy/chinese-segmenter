package opennlp.maxent;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import opennlp.model.GenericModelReader;
import opennlp.model.MaxentModel;

public class ModelTester {
	
	public static char[] tag(char[] text,MaxentModel m){
		char[] tags= new char[text.length];
		FeaturesV2 feats = new FeaturesV2(text);
		for(int i=0,len=text.length;i<len;i++){
			tags[i] = m.getBestOutcome(m.eval(feats.getFeatures(i))).charAt(0);//,foreTag
		}
		return tags;
	}
	
	public static String removetag(char[] text,char[] tags){
		StringBuilder buf = new StringBuilder();
		for(int i=0;i<text.length;i++){
			buf.append(text[i]);
			if(tags[i]=='E'||tags[i]=='S'){
				buf.append(' ');
			}
		}
		return buf.toString();
	}
	
	public static void results(File input,File output, MaxentModel m) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(input));
		BufferedWriter writer = new BufferedWriter(new FileWriter(output));
		String line;
		int count=0;
		while((line=reader.readLine())!=null){
			count++;
			if(count%50==0){
				System.out.println("have finished "+count+" records...");
				writer.flush();
			}
			try {
				if((line=line.trim()).equals("")){
					writer.write("\n");
					continue;
				}
				char[] origArr = line.toCharArray();
				char[] textArr=CharUtil.strq2b(origArr);
				char[] tags = tag(textArr,m);
				line = removetag(origArr,tags);
				writer.write(line+"\n");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("the content of line is:"+line);
			}
		}
		reader.close();
		writer.close();
	
	}
	
	public static void main(String[] args) {
		String modelFileName="E:/xh_exp/script/pku_training_maxentModel.txt";
		try {
	      MaxentModel m = new GenericModelReader(new File(modelFileName)).getModel();
//	      File input = new File("E:/icwb2-data/testing/pku_test.utf8");
	      File input = new File("E:/icwb2-data/training/pku_training.utf8");
	      File output = new File("E:/xh_exp/script/pku_closed_results.utf8");
	      System.out.println("model load finished");
	      results(input,output,m);
		} catch (Exception e) {
		    e.printStackTrace();
		    System.exit(0);
		}
	}
}
