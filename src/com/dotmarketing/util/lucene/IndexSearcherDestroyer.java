/**
 * 
 */
package com.dotmarketing.util.lucene;

import java.io.IOException;
import java.util.TimerTask;

import org.apache.lucene.search.IndexSearcher;

import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.LuceneProfiler;

/**
 * @author Jason Tesser
 *
 */
public class IndexSearcherDestroyer extends TimerTask {

	private IndexSearcher is;
	
	public IndexSearcherDestroyer(IndexSearcher is) {
		this.is = is;
	}
	
	@Override
	public void run() {
		boolean profile = Config.getBooleanProperty("LUCENE_PROFILING", false);
		if(profile){
			LuceneProfiler.log(IndexSearcherDestroyer.class, "run", "Entered method");
		}
		if(is != null){
			try {
				if(is.getIndexReader() != null){
					is.getIndexReader().close();
				}	
			} catch (IOException e) {
				Logger.debug(IndexSearcherDestroyer.class,e.getMessage(),e);
			} catch (Exception e) {
				Logger.error(IndexSearcherDestroyer.class,e.getMessage(),e);
			}
			try {
				if(is != null){
					if(profile){
						LuceneProfiler.log(IndexSearcherDestroyer.class, "run", "Closing IndexSearcher " + is);
					}
					is.close();
				}
			} catch (IOException e) {
				Logger.debug(IndexSearcherDestroyer.class,e.getMessage(),e);
			} catch (Exception e) {
				Logger.error(IndexSearcherDestroyer.class,e.getMessage(),e);
			}
			is = null;
		}
	}
	
}
