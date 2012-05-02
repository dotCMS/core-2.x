package com.dotmarketing.common.reindex;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.lucene.document.Document;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.FactoryLocator;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.common.business.journal.DistributedJournalAPI;
import com.dotmarketing.common.business.journal.DistributedJournalFactory;
import com.dotmarketing.common.business.journal.IndexJournal;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.HibernateUtil;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.business.ReindexContentletFactory;
import com.dotmarketing.portlets.contentlet.factories.ReindexationProcessStatus;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.LuceneHits;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.lucene.LuceneUtils;

public class ReindexThread extends Thread {

	private List<IndexJournal> remoteQ = new ArrayList<IndexJournal>();
	private ReindexContentletFactory rif = FactoryLocator
			.getReindexContentletFactory();
	private DistributedJournalAPI jAPI = APILocator.getDistributedJournalAPI();
	private ContentletAPI conAPI = APILocator.getContentletAPI();

	private IndexJournal iJournal = null;
	private boolean newIndexOnly = false;
	private static ReindexThread instance;

	private boolean start = false;
	private boolean work = false;
	private int sleep = 100;
	private int delay = 0;
	private boolean localReindex = false;
	private int switchIndexCounter = 0;

	private void finish() {
		work = false;
		start = false;
	}

	private void startProcessing(int sleep, int delay) {
		this.sleep = sleep;
		this.delay = delay;
		start = true;
	}
	
	private boolean die=false;

	
	
	public void run() {

		while (!die) {

			if (start && !work) {
				if (delay > 0) {
					Logger.info(this, "Reindex Thread start delayed for "
							+ delay + " millis.");
					try {
						Thread.sleep(delay);
					} catch (InterruptedException e) {

					}
				}
				Logger.info(this, "Reindex Thread started with a sleep of "
						+ sleep);
				start = false;
				work = true;
			}
			if (work) {
				try {

					List<Document> docs = getNextDocToIndex();

					if ((!ReindexationProcessStatus.inFullReindexation() && jAPI
							.buildNewIndexForServer())
							|| localReindex) {
						LuceneUtils.createNewIndexFolder();
						if (!localReindex) {
							ReindexationProcessStatus.startNewIndex();
						}
						jAPI.buildNewIndexForServer();
					}
					localReindex = false;

					if(iJournal !=null){
						jAPI.deleteLikeJournalRecords(iJournal);
						writeDocumentToIndex(docs,iJournal.isDelete(), iJournal.getIdentToIndex().toString());
					}

					if (iJournal != null) {
						jAPI.deleteReindexEntryForServer(iJournal);
						remoteQ.remove(iJournal);
					}

				} catch (Exception ex) {
					Logger.error(this, "Unable to index record", ex);
				} finally {
					try {
						HibernateUtil.closeSession();
					} catch (DotHibernateException e) {
						Logger.error(this, e.getMessage(), e);
					}
					DbConnectionFactory.closeConnection();
				}
				if (remoteQ.isEmpty()) {
					try {
						Thread.sleep(sleep);
					} catch (InterruptedException e) {
						Logger.error(this, e.getMessage(), e);
					}
				}
			} else {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException e) {
					Logger.error(this, e.getMessage(), e);
				}
			}
		}
	}

	public synchronized void startLocalReindex() {
		localReindex = true;
	}

	/**
	 * Tells the thread to start processing. Starts the thread
	 */
	public synchronized static void startThread(int sleep, int delay) {
		Logger.info(ReindexThread.class,
				"ContentIndexationThread ordered to start processing");

		if (instance == null) {
			instance = new ReindexThread();
			instance.start();
			instance.setName("dotCMS Indexing Thread");
		}
		instance.startProcessing(sleep, delay);
	}

	/**
	 * Tells the thread to stop processing. Doesn't shut down the thread.
	 */
	public synchronized static void stopThread() {
		if (instance != null && instance.isAlive()) {
			Logger.info(ReindexThread.class,
					"ContentIndexationThread ordered to stop processing");
			instance.finish();
		} else {
			Logger.error(ReindexThread.class,
					"No ContentIndexationThread available");
		}
	}

	/**
	 * Creates and starts a thread that doesn't process anything yet
	 */
	public synchronized static void createThread() {
		if (instance == null) {
			instance = new ReindexThread();
			instance.start();
			instance.setName("dotCMS Indexing Thread");
		}

	}
	
    /**
     * Tells the thread to finish what it's down and stop
     */
	public synchronized static void shutdownThread() {
		if (instance!=null && instance.isAlive()) {
			Logger.info(ReindexThread.class, "ReindexThread shutdown initiated");
			instance.die=true;
		} else {
			Logger.warn(ReindexThread.class, "ReindexThread not running (or already shutting down)");
		}
	}

	public static void ensureLife(){
		if(instance == null){
			startThread(Config.getIntProperty("REINDEX_THREAD_SLEEP", 500), Config.getIntProperty("REINDEX_THREAD_INIT_DELAY", 5000));
		}
	}
	
	/**
	 * This instance is intended to already be started. It will try to restart
	 * the thread if instance is null.
	 */
	public static ReindexThread getInstance() {
		if (instance == null) {
			createThread();
		}
		return instance;
	}

	private void fillRemoteQ() throws DotDataException {
		remoteQ = jAPI.findContentReindexEntriesToReindex();
	}

	private List<Document> getNextDocToIndex() throws DotDataException,
			Exception {
		newIndexOnly = false;
		iJournal = null;
		List<Document> docs = new ArrayList<Document>();

		if (remoteQ.size() < 1) {
			fillRemoteQ();
		}
		if (remoteQ.size() > 0) {
			iJournal = remoteQ.get(0);
			String l = iJournal.getIdentToIndex().toString();
			if (iJournal.isDelete()) {
				Document d = new Document();
				d.add(LuceneUtils.getKeywordField("identifier", String.valueOf(l)));
				docs.add(d);
			} else {
				docs = rif.buildDocList(IdentifierCache.getIdentifierFromIdentifierCache(l));
			}
			if (iJournal.getPriority() == DistributedJournalFactory.REINDEX_JOURNAL_PRIORITY_NEWINDEX) {
				newIndexOnly = true;
			}
		}


		// if we have nothing to do and we were building an index switch
		if (docs	.size() < 1 && ReindexationProcessStatus.inFullReindexation()
				&& !jAPI.areRecordsLeftToIndex()) {
			LuceneHits hits;
			ContentletAPI conAPI = APILocator.getContentletAPI();
			LuceneUtils.destroySearcherForNewIndex();
			hits = LuceneUtils.searchInReindexationIndex("+type:content", -1, 0, "modDate");

			if (hits.getTotal() > 99999
					|| ((conAPI.contentletIdentifierCount() * .90) < hits
							.getTotal())) {
				ReindexationProcessStatus.setLastIndexationEndTime(UtilMethods
						.htmlDateToHTMLTime(new Date()));
				switchIndexCounter = 0;
				switchIndex();
				LuceneUtils.destroySearcherForCurrentIndex();
			} else {
				if (switchIndexCounter == 1000) {
					Logger
							.warn(
									this,
									"************UNABLE TO SWITCH TO NEW INDEX BECAUSE THE INDEX WAS TOO SMALL*************");
					Logger
							.warn(
									this,
									"************YOU SHOULD DELETE YOUR `newindex` FILE UNDER YOUR LUCENE DIRECTORY AND RERUN YOUR REINDEX *************");
					switchIndexCounter = 0;
				} else {
					switchIndexCounter++;
				}
			}
		}
		return docs;
	}

	private void switchIndex() {
		LuceneUtils.changeToTheNewLuceneDir();
	}

	private void writeDocumentToIndex(List<Document> docs, boolean delete, String identifier) throws DotRuntimeException {
		try {
			if (delete == true) {
				for (Document doc : docs) {
					// first we delete the document with the identifier
					String ident = doc.get("identifier");
				
					LuceneUtils.removeDocByIdenToCurrentIndex(ident + "");
					LuceneUtils.removeDocByInodeToCurrentIndex(ident + "");
					if (ReindexationProcessStatus.inFullReindexation()) {
						LuceneUtils.removeDocByIdenToReindexationIndex(ident
								+ "");
						LuceneUtils.removeDocByInodeToReindexationIndex(ident
								+ "");
					}
					//This is a delete command.  Reindex the identifier to make sure we don't have any more content in other lagunages. See DOTCMSEE-289					
					List<Document> newDocs = rif.buildDocList(IdentifierCache.getIdentifierFromIdentifierCache(ident));
					writeDocumentToIndex(newDocs,false,ident);
					continue;
				}
			}else{
				// if for current index. The record may only be a record for a
				// reindex
				if (!newIndexOnly) {
					if (!LuceneUtils.writeDocsToCurrentIndex(docs,identifier)) {
						throw new Exception(
								"Error occurred trying to reindex contentlet");
					}
					for (Document doc : docs) {
						validateIndex(true, doc);
					}
				}

				// if it's full reindexation we add to the new index
				if (ReindexationProcessStatus.inFullReindexation()) {
					if (!LuceneUtils.writeDocsToReindexationIndex(docs, identifier)) {
						throw new Exception(
								"Error occurred trying to reindex contentlet");
					}
					for (Document doc : docs) {
						validateIndex(false, doc);
					}
				}
			}
			if (UtilMethods.isSet(docs) && docs.size() > 0) {
				ReindexationProcessStatus.updateIndexationProgress(1);
			}
		} catch (Exception e) {
			throw new DotRuntimeException(e.getMessage(),e);
		}
	}

	private void validateIndex(boolean currentIndex, Document doc)
			throws Exception {
		String ident = doc.get("identifier");
		String languageId = doc.get("languageId");
		String workingQuery = "+type:content +identifier:" + ident
				+ " +languageId:" + languageId + " +working:" + "true";
		String liveQuery = "+type:content +identifier:" + ident
				+ " +languageId:" + languageId + " +live:" + "true";
		LuceneHits whits;
		LuceneHits lhits;
		if (currentIndex) {

			whits = LuceneUtils.searchInCurrentIndex(workingQuery, -1, 0,
					"modDate");
			lhits = LuceneUtils.searchInCurrentIndex(liveQuery, -1, 0,
					"modDate");

		} else {
			whits = LuceneUtils.searchInReindexationIndex(workingQuery, -1, 0,
					"modDate");
			lhits = LuceneUtils.searchInReindexationIndex(liveQuery, -1, 0,
					"modDate");
		}

		if (whits.getTotal() > 1) {
			Logger.debug(this,
					"More then one working contentlet with identifier " + ident
							+ " in the index");
			if (doc.get("working").equals("true")) {
				for (int i = 0; i < whits.getTotal(); i++) {
					Document d = whits.doc(i);
					String hinode = d.get("inode");
					String dinode = doc.get("inode");
					if (!hinode.equals(dinode)) {
						if (currentIndex) {
							Logger.debug(this,
									"Found too many working indexes in current index with identifier = "
											+ ident + " and languageId = "
											+ languageId
											+ "! Attempting to fix");
							LuceneUtils.removeDocByInodeToCurrentIndex(hinode);
						} else {
							Logger.debug(this,
									"Found too many working indexes in the new index with identifier = "
											+ ident + " and languageId = "
											+ languageId
											+ "! Attempting to fix");
							LuceneUtils
									.removeDocByInodeToReindexationIndex(hinode);
						}
					}
				}
			}
		}

if (lhits.getTotal() > 1) {
	Logger.debug(this, "More then one live contentlet with identifier "
			+ ident + " in the index");
	if (doc.get("live").equals("true")) {
		for (int i=0;i< lhits.getTotal() ; i++) {
			Document d=lhits.doc(i);
			String hinode = d.get("inode");
			String dinode = doc.get("inode");
			if (!hinode.equals(dinode)) {
				if (currentIndex) {
					Logger.debug(this,
							"Found too many live indexes in current index with identifier = "
									+ ident + " and languageId = "
									+ languageId
									+ "! Attempting to fix");
					LuceneUtils.removeDocByInodeToCurrentIndex(hinode);
				} else {
					Logger.debug(this,
							"Found too many live indexes in the new index with identifier = "
									+ ident + " and languageId = "
									+ languageId
									+ "! Attempting to fix");
					LuceneUtils
							.removeDocByInodeToReindexationIndex(hinode);
				}
			}
		}
	}
}
}

	int threadsPausing = 0;

	public synchronized void pause() {
		threadsPausing++;
		work=false;
	}

	public synchronized void unpause() {
		threadsPausing--;
		if (threadsPausing<1) {
			threadsPausing=0;
			work=true;
		}
	}
	
	public boolean isWorking() {
		return work;
	}

}
