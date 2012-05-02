package com.dotmarketing.util.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.LockObtainFailedException;

public class LuceneNewIndexObjectHolder extends BaseLuceneObjectHolder{

	private LuceneNewIndexObjectHolder(){};

	private IndexReader indexReader = null;
	private static LuceneNewIndexObjectHolder instance;
	
	private synchronized static void createInstance() throws CorruptIndexException, IOException {
		if (instance == null) {
			instance = new LuceneNewIndexObjectHolder();
		}
	}

	public static LuceneNewIndexObjectHolder getInstance() throws CorruptIndexException, IOException {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}
	
	protected synchronized void createSearcher() throws CorruptIndexException, IOException{
		if(indexSearcher == null){
			indexReader = IndexReader.open(LuceneUtils.getNewLuceneDir(), true);
			indexSearcher = new IndexSearcher(indexReader);
		}
	}
	
	protected synchronized void createWriter() throws CorruptIndexException, LockObtainFailedException, IOException {
		if(indexWriter == null){
			indexWriter = new IndexWriter(LuceneUtils.getNewLuceneDir(), new WhitespaceAnalyzer(), false, MaxFieldLength.UNLIMITED);
		}
		writeLock = false;
	}
}
