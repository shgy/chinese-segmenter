package org.lucene.ictanalyzer;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * Unit test for simple App.
 */
public class ICTCLASAnalyzerTest 
    extends TestCase
{
	
	public void testICTCLASAanayzer() throws IOException{
		ICTCLASAnalyzer analyzer = new ICTCLASAnalyzer(Version.LUCENE_43);
		analyzer.setMaxTokenLength(5);
    	TokenStream ts = analyzer.tokenStream("title", new StringReader("据悉，aaaaaaaaaa 质检总局已将最新有关情况再次通报美方，要求美方加强对输华玉米的产地来源、运输及仓储等环节的管控措施，有效避免输华玉米被未经我国农业部安全评估并批准的转基因品系污染。"));
    	ts.reset();
    	while(ts.incrementToken()){
    		CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);
    		System.out.println(term.toString());
    	}
    	analyzer.close();
	}
	
   public void testLucene() throws IOException{
	   RAMDirectory ram = new RAMDirectory();
	   IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43,new ICTCLASAnalyzer(Version.LUCENE_43));
	   IndexWriter iw = new IndexWriter(ram, config);
	   Document doc = new Document();
	   doc.add(new TextField("title", "质检总局已将最新有关情况再次通报美方", Store.YES));
	   doc.add(new TextField("content", "质检总局已将最新有关情况再次通报美方", Store.YES));
	   iw.addDocument(doc);
	   iw.commit();
	   iw.close();
	   
	   IndexReader ir = DirectoryReader.open(ram);
	   IndexSearcher is = new IndexSearcher(ir);
	   TopDocs hits = is.search(new TermQuery(new Term("title", "美方")), 10);
	   System.out.println(hits.totalHits);
	   Document ts = ir.document(hits.scoreDocs[0].doc);
	   System.out.println(ts.get("content"));
	   
   }
}
