package com.dotmarketing.util.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.LockObtainFailedException;

import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.LuceneProfiler;
import com.dotmarketing.util.lucene.LuceneUtils.PreparedQuery;

public abstract class BaseLuceneObjectHolder {

	protected boolean writeLock = false;
	protected IndexSearcher indexSearcher = null;
	protected IndexWriter indexWriter = null;
	protected BaseLuceneObjectHolder(){};
	private Timer t = new Timer();
	protected List<String> destroyedSearchersHC = new ArrayList<String>(1000);
	
	protected abstract void createSearcher() throws CorruptIndexException, IOException;
	
	protected abstract void createWriter() throws CorruptIndexException, LockObtainFailedException, IOException;

	private synchronized void doClose(boolean destroy) {
		boolean profile = Config.getBooleanProperty("LUCENE_PROFILING", false);
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "doClose(" + destroy + ")", "Entered method");
		}
		try {
			if(indexSearcher != null){
				if(profile){
					LuceneProfiler.log(BaseLuceneObjectHolder.class, "doClose(" + destroy + ")", "Searcher is not null");
				}
				if(destroy){
					if(profile){
						LuceneProfiler.log(BaseLuceneObjectHolder.class, "doClose(" + destroy + ")", "Closing searcher and setting to null");
					}
					try{
						indexSearcher.getIndexReader().close();
					}catch (Throwable e) {
						Logger.fatal(this, "Problem closing indexSearcher Trying to recover : " + e.getMessage(), e);
					}
					try{
						indexSearcher.close();
					}catch (Throwable e) {
						Logger.fatal(this, "Problem closing indexSearcher Trying to recover : " + e.getMessage(), e);
					}
					indexSearcher = null;
				}else{
					if(profile){
						LuceneProfiler.log(BaseLuceneObjectHolder.class, "doClose(" + destroy + ")", "Reopening Searcher");
					}
					IndexSearcher newIndexSearcher = new IndexSearcher(indexSearcher.getIndexReader().reopen(true));
					if(newIndexSearcher.getIndexReader() != indexSearcher.getIndexReader()){
						if(profile){
							LuceneProfiler.log(BaseLuceneObjectHolder.class, "doClose(" + destroy + ")", "Readers are different sending searcher for destruction in 10 seconds. IndexSearcher is : " + indexSearcher == null ? "null":indexSearcher.toString());
						}
						t.schedule(new IndexSearcherDestroyer(indexSearcher), 10000);
					}
					indexSearcher = newIndexSearcher;
				}
			}else{
				if(profile){
					LuceneProfiler.log(BaseLuceneObjectHolder.class, "doClose(" + destroy + ")", "Searcher is null");
				}
			}
		} catch (Throwable e) {
			Logger.error(this, "Error reopening Lucene index searcher.", e);
		}
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "doClose(" + destroy + ")", "Exiting method");
		}
	}

	protected void refresh() {
		boolean profile = Config.getBooleanProperty("LUCENE_PROFILING", false);
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "refresh", "Entered method");
		}
		doClose(false);
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "refresh", "Exiting method");
		}
	}

	protected IndexSearcher checkOutSearcher() throws CorruptIndexException, IOException {
		boolean profile = Config.getBooleanProperty("LUCENE_PROFILING", false);
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkOutSearcher", "Entered method");
		}
		if(indexSearcher == null){
			if(profile){
				LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkOutSearcher", "Searcher is null. Creating a new one");
			}
			createSearcher();
		}
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkOutSearcher", "Exiting method. Returning searcher");
		}
		return indexSearcher;
	}
	
	protected void checkInSearcher(IndexSearcher is){
		boolean profile = Config.getBooleanProperty("LUCENE_PROFILING", false);
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkInSearcher", "Entered method");
		}
		if(is != indexSearcher){
			if(profile){
				LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkInSearcher", "Searchers are not equal so ending to destory will destroy in 10 second");
			}
			t.schedule(new IndexSearcherDestroyer(is), 10000);
		}
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkInSearcher", "Exiting method");
		}
	}

	/**
	 * Only one writer can be out at a time.  
	 * @return
	 * @throws IOException 
	 * @throws LockObtainFailedException 
	 * @throws CorruptIndexException 
	 */
	protected synchronized IndexWriter checkoutWriter() throws CorruptIndexException, LockObtainFailedException, IOException{
		boolean profile = Config.getBooleanProperty("LUCENE_PROFILING", false);
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkoutWriter", "Entered method");
		}
		int counter = 0;
		while(writeLock){
			if(counter == 1){
				if(profile){
					LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkoutWriter", "There is a write lock waiting");
				}
			}
			if(counter > 300){
				Logger.fatal(this, "******Timed out waiting for INDEX SEARCHER to free.  Going to return******");
				Thread.dumpStack();
				createWriter();
				break;
			}
			try{
				Thread.sleep(100);
			}catch (Exception e) {
				Logger.debug(this, "Cannot sleep : ", e);
			}
			counter++;
		}
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkoutWriter", "No write lock getting writer to return");
		}
		if(indexWriter == null){
			if(profile){
				LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkoutWriter", "IndexWrighter is null creating a new one");
			}
			createWriter();
			if(profile){
				LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkoutWriter", "Done creating a new writer");
			}
		}
		writeLock = true;
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkoutWriter", "WriteLock is now true. Exiting method. Returning the checkedout Writer");
		}
		return indexWriter;
	}
	
	/**
	 * This will commit the writer, close the write, and reopen the searcher
	 * @throws IOException 
	 * @throws LockObtainFailedException 
	 * @throws CorruptIndexException 
	 */
	protected synchronized void checkinWriter() throws CorruptIndexException, LockObtainFailedException, IOException{
		boolean profile = Config.getBooleanProperty("LUCENE_PROFILING", false);
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkinWriter", "Started to checkin the Writer.");
		}
		if(indexWriter != null){
//			IndexWriter.unlock(indexWriter.getDirectory());
			if(profile){
				LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkinWriter", "About to commit writer");
			}
			indexWriter.commit();
			if(profile){
				LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkinWriter", "About to close writer");
			}
			indexWriter.close();
			indexWriter = null;
		}
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkinWriter", "Refreshing the searcher");
		}
		refresh();
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkinWriter", "Done refreshing the searcher");
		}
		writeLock = false;
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "checkinWriter", "Done checking in the Writer");
		}
	}
	
	protected synchronized void destroySearcher(){
		boolean profile = Config.getBooleanProperty("LUCENE_PROFILING", false); 
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "destroySearcher", "Entered Destroying the searcher method. This is usually only done from a switch of the index or trying to recover from a problem");
		}
		doClose(true);
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "destroySearcher", "Exiting Destroying the searcher method. This is usually only done from a switch of the index  or trying to recover from a problem");
		}
	}
	
	/**
	 * 
	 * @param is
	 */
	public void ensureSearcherIsValid(IndexSearcher is){
		boolean profile = Config.getBooleanProperty("LUCENE_PROFILING", false); 
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "ensureSearcherIsValid", "Entering method");
		}
		String hc = is.hashCode() + "";
		if(destroyedSearchersHC.contains(hc)){
			if(profile){
				LuceneProfiler.log(BaseLuceneObjectHolder.class, "ensureSearcherIsValid", "Already destroyed so returning");
			}
			return;
		}
		refresh();
		PreparedQuery preparedQuery = null;;
		try {
			preparedQuery = LuceneUtils.prepareQuery("+structureName:IDONTEXIST", "modDate");
			Query lq = preparedQuery.getLuceneQuery();
			indexSearcher.search(lq, 1);
		} catch (ParseException e) {
			Logger.error(BaseLuceneObjectHolder.class,e.getMessage(),e);
		} catch(NullPointerException npe){
			Logger.debug(this, npe.getMessage() + " : IndexSearcher is NULL should lazily recover",npe);
			if(profile){
				LuceneProfiler.log(BaseLuceneObjectHolder.class, "ensureSearcherIsValid", "IndexSearcher is NULL should lazily recover");
			}
		} catch (Exception e) {
			Logger.error(BaseLuceneObjectHolder.class,e.getMessage(),e);
			if(profile){
				LuceneProfiler.log(BaseLuceneObjectHolder.class, "ensureSearcherIsValid", e.getMessage() + " : Found Error going to destory");
			}
			destroySearcher();
			destroyedSearchersHC.add(is.hashCode() + "");
			try{
				if(destroyedSearchersHC.size()>1000){
					destroyedSearchersHC.subList(1000, destroyedSearchersHC.size()).clear();
				}
			}catch (Exception ex) {
				Logger.error(this, ex.getMessage(),ex);
			}
		}
		if(profile){
			LuceneProfiler.log(BaseLuceneObjectHolder.class, "ensureSearcherIsValid", "Exiting  method.");
		}
	}
}
