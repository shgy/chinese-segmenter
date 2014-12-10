package org.xh.xs.lucene_42;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.xh.xs.lucene_42.XhAnalyzer;
class DocAddTask implements Runnable{
private IndexWriter iw;
private Document doc;
	public DocAddTask(IndexWriter iw,Document doc){
		this.iw=iw;
		this.doc=doc;
	}
	public void run() {
		// TODO Auto-generated method stub
		try {
			iw.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
public class LuceneDemo {
	Directory dir;
	Analyzer analyzer;
	
	public LuceneDemo() throws IOException{
		dir= new SimpleFSDirectory(new File("d:/lucene_demo/"));
		analyzer=new XhAnalyzer();
	}
	
	public void index() throws IOException, InterruptedException{
		IndexWriterConfig conf=new IndexWriterConfig(Version.LUCENE_42, analyzer);
		IndexWriter iw=new IndexWriter(dir, conf);
		Document doc=new Document();
		doc.add(new TextField("content", "content field appear here", Store.YES));
		doc.add(new TextField("title", "title field shows here", Store.YES));
		Thread t1=new Thread(new DocAddTask(iw,doc));
		Document doc2=new Document();
		doc2.add(new TextField("content", "this is a simple doc two", Store.YES));
		doc2.add(new TextField("title", "this is a simple title two", Store.YES));
		Thread t2=new Thread(new DocAddTask(iw,doc2));
		t1.start();t2.start();
		t1.join();t2.join();
		iw.commit();
		iw.close();
	}
	
	public void search() throws IOException{
		IndexReader ir=DirectoryReader.open(dir);
		IndexSearcher is=new IndexSearcher(ir);
		
		BooleanQuery query=new BooleanQuery();
		query.add(new TermQuery(new Term("content","simple")), Occur.SHOULD);
		TopDocs top=is.search(query, 10);
		ScoreDoc[] hits=top.scoreDocs;
		
		System.out.println("total hits:"+hits.length);
		for (ScoreDoc scoreDoc : hits) {
			System.out.println(is.doc(scoreDoc.doc).get("content"));
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		LuceneDemo ld=new LuceneDemo();
		//ld.index();
		ld.search();
	}
}
