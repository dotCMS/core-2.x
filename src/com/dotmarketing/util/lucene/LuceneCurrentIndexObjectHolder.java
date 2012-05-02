package com.dotmarketing.util.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.LockObtainFailedException;

public class LuceneCurrentIndexObjectHolder extends BaseLuceneObjectHolder {

	private static LuceneCurrentIndexObjectHolder instance;
	private IndexReader indexReader = null;
	
	private LuceneCurrentIndexObjectHolder() {}
	
	private synchronized static void createInstance() throws CorruptIndexException, IOException {
		if (instance == null) {
			instance = new LuceneCurrentIndexObjectHolder();
		}
	}

	protected static LuceneCurrentIndexObjectHolder getInstance() throws CorruptIndexException, IOException {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}
	
	protected synchronized void createSearcher() throws CorruptIndexException, IOException{
		if(indexSearcher == null){
			indexReader = IndexReader.open(LuceneUtils.getCurrentLuceneDir(), true);
			indexSearcher = new IndexSearcher(indexReader);
		}
	}
	
	protected synchronized void createWriter() throws CorruptIndexException, LockObtainFailedException, IOException {
		if(indexWriter == null){
			indexWriter = new IndexWriter(LuceneUtils.getCurrentLuceneDir(), new WhitespaceAnalyzer(), false, MaxFieldLength.UNLIMITED);
		}
		writeLock = false;
	}
	
}
