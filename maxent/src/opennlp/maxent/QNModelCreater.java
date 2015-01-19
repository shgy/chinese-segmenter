package opennlp.maxent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import opennlp.maxent.io.ObjectQNModelWriter;
import opennlp.maxent.quasinewton.QNModel;
import opennlp.model.AbstractModelWriter;
import opennlp.model.EventStream;
import opennlp.model.TwoPassDataIndexer;
import aa.QNTrainer;

public class QNModelCreater {
	public static void main(String[] args) throws IOException {
		String dataFileName="E:/xh_exp/script/pku_training_maxent.utf8";
        FileReader datafr = new FileReader(new File(dataFileName));
        EventStream es = new BasicEventStream(new PlainTextByLineDataStream(datafr));
        QNTrainer trainer = new QNTrainer();
        QNModel qnmodel=trainer.trainModel(new  TwoPassDataIndexer(es,0));
        System.out.println("train finished ....");
        AbstractModelWriter writer = null;
        writer = new ObjectQNModelWriter(qnmodel,new ObjectOutputStream(new FileOutputStream(new File("data/maxent_pku_qn.model"))) );
        writer.persist();
        writer.close();
        System.out.println("persist finished ....");
//        BinaryQNModelReader reader 
//        =new BinaryQNModelReader(
//        		new DataInputStream(
//        				new FileInputStream(new File("data/maxent_pku_qn.model"))));
//        AbstractModel qnmodel =  reader.getModel();
        String text="共同创造美好的新世纪——二○○一年新年贺词";
    	char[] tags =ModelTester.tag(text.toCharArray(), qnmodel );
		System.out.println(Arrays.toString(tags));
		text=ModelTester.removetag(text.toCharArray(),tags);
		System.out.println(text);
	}
}
