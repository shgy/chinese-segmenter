package opennlp.maxent;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.TestCase;
import opennlp.maxent.io.SuffixSensitiveGISModelWriter;
import opennlp.model.AbstractModel;
import opennlp.model.EventStream;
import opennlp.model.GenericModelReader;
import opennlp.model.MaxentModel;

import org.junit.Test;

public class TestModelTester extends TestCase{
	@Test
	public void testFeatures(){
		String text="abcdefg";
		FeaturesV2 feats = new FeaturesV2(text);
		int len=text.length();
		String[] features = feats.getFeatures(2);
		String[] expect={""};
		for (int i=0;i<features.length;i++) {
			System.out.println(features[i]);
		}
		
	}
	
	@Test
	public void testTag() throws IOException{
		String dataFileName = "data/gameLocation.dat";
		 FileReader datafr = new FileReader(new File(dataFileName));
	     EventStream es = new BasicEventStream(new PlainTextByLineDataStream(datafr));
	    AbstractModel model = GIS.trainModel(es,100,0,false,true);
		System.out.println(Arrays.toString(model.eval(new String[]{"Cloudy","Happy"})));
	}
	@Test
	public void testRemoveTag() throws IOException{
		String modelFileName="E:/xh_exp/script/pku_training_maxentModel.txt";
		String text="共同创造美好的新世纪——二○○一年新年贺词";
		MaxentModel m=new GenericModelReader(new File(modelFileName)).getModel();
		char[] tags =ModelTester.tag(text.toCharArray(), m );
		System.out.println(Arrays.toString(tags));
		text=ModelTester.removetag(text.toCharArray(),tags);
		System.out.println(text);
	}
	
}
