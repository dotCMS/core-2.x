package com.dotmarketing.util.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.ClosedChannelException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.util.NumberUtils;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.cache.FieldsCache;
import com.dotmarketing.cache.StructureCache;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.portlets.contentlet.factories.ReindexationProcessStatus;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.ConfigUtils;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.LuceneHits;
import com.dotmarketing.util.LuceneProfiler;
import com.dotmarketing.util.NumberUtil;
import com.dotmarketing.util.RegEX;
import com.dotmarketing.util.RegExMatch;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.viewtools.WebAPI;

public class LuceneUtils {

	private static LuceneUtils instance;
	public final static String ERROR_DATE = "error date";
	private final static String LUCENE_STRUCTURE_FIELD = "structureName";
	private final static int dirType = Config.getIntProperty("LUCENE_FSDIRECTORY",0);

	/**
	 * Creates and starts a thread that doesn't process anything yet
	 */
	public synchronized static void createInstance() {
		if (instance == null) {
			instance = new LuceneUtils();
		}
	}

	public static void destroySearcherForCurrentIndex() throws CorruptIndexException, IOException {
		LuceneCurrentIndexObjectHolder.getInstance().destroySearcher();
	}
	
	public static void destroySearcherForNewIndex() throws CorruptIndexException, IOException {
		LuceneNewIndexObjectHolder.getInstance().destroySearcher();
	}

	/**
	 * This instance is intended to already be started. It will try to restart
	 * the thread if instance is null.
	 */
	public static LuceneUtils getInstance() {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}

	public static String getBaseLuceneDir() {
		File f = new File(ConfigUtils.getLucenePath()
				+ File.separator);
		if (f.exists() && !f.isDirectory()) {
			f.delete();
		}
		if (!f.exists()) {
			f.mkdirs();
		}
		try {
			return f.getCanonicalPath() + File.separator;
		} catch (IOException e) {
			Logger.error(LuceneUtils.class, "getBaseLuceneDir: Error setting up the dotlucene folder.");
			return null;
		}
	}

	private static long readIndexNumberFromFile(boolean newIndex) {
		long current = buildFolderName();
		String baseDir = getBaseLuceneDir();
		File indexNumberStoreFile;
		if (newIndex) {
			indexNumberStoreFile = new File(baseDir + "newindex.txt");
		} else {
			indexNumberStoreFile = new File(baseDir + "currentindex.txt");
		}
		if (indexNumberStoreFile.exists()) {
			FileReader fReader = null;
			try {
				fReader = new FileReader(indexNumberStoreFile);
				BufferedReader reader = new BufferedReader(fReader);
				String numberSt = reader.readLine();
				current = Long.parseLong(numberSt);
			} catch (Exception e) {
				Logger.error(LuceneUtils.class, "Error trying to obtain the current index number from "
						+ indexNumberStoreFile.getAbsolutePath(), e);
			} finally {
				if (fReader != null)
					try {
						fReader.close();
					} catch (Exception e) {
						Logger.error(LuceneUtils.class, "Error trying to close the opened file reader for "
								+ indexNumberStoreFile.getAbsolutePath(), e);
					}
			}
		} else {
			saveIndexNumberToFile(current, newIndex);
		}
		return current;
	}

	private static long buildFolderName() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.applyPattern("yyyyMMddHHmmss");
		return new Long(sdf.format(cal.getTime()));
	}

	private static void saveIndexNumberToFile(long newNumber, boolean newIndex) {

		String baseDir = getBaseLuceneDir();

		File directory = new File(baseDir);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		File indexNumberStoreFile;
		if (newIndex) {
			indexNumberStoreFile = new File(baseDir + "newindex.txt");
		} else {
			indexNumberStoreFile = new File(baseDir + "currentindex.txt");
		}
		if (!indexNumberStoreFile.exists()) {
			try {
				indexNumberStoreFile.createNewFile();
			} catch (IOException e) {
				Logger.error(LuceneUtils.class, "Error trying to close the opened file writer for "
						+ indexNumberStoreFile.getAbsolutePath(), e);
				return;
			}
		}
		FileWriter fWriter = null;
		try {
			if (!indexNumberStoreFile.exists()) {
				indexNumberStoreFile.createNewFile();
			}
			fWriter = new FileWriter(indexNumberStoreFile, false);
			fWriter.write(String.valueOf(newNumber));
		} catch (IOException e) {
			Logger.error(LuceneUtils.class, "Error trying to save the index number: " + newNumber + " to "
					+ indexNumberStoreFile.getAbsolutePath());
		} finally {
			if (fWriter != null)
				try {
					fWriter.close();
				} catch (IOException e) {
					Logger.error(LuceneUtils.class, "Error trying to close the opened file writer for "
							+ indexNumberStoreFile.getAbsolutePath(), e);
				}
		}
	}

	public static FSDirectory getCurrentLuceneDir() throws IOException {
		FSDirectory dirFS = null;
		if(dirType==1){
			dirFS = new SimpleFSDirectory(new File(getCurrentLuceneDirPath()));
		}else if(dirType==2){
			dirFS = new NIOFSDirectory(new File(getCurrentLuceneDirPath()));
		}else if(dirType==3){
			dirFS = new MMapDirectory(new File(getCurrentLuceneDirPath()));
		}else{
			dirFS = FSDirectory.open(new File(getCurrentLuceneDirPath()));
		}
		return dirFS;
	}

	private static void removeNewIndexFile() {
		File indexNumberStoreFile = new File(getBaseLuceneDir() + "newindex.txt");
		if (indexNumberStoreFile.exists()) {
			indexNumberStoreFile.delete();
		}
	}

	public static String getCurrentLuceneDirPath() {
		String baseDir = getBaseLuceneDir();

		String dir = baseDir + readIndexNumberFromFile(false) + File.separator;

		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		if (dirFile.exists() && !dirFile.isDirectory()) {
			Logger.fatal(LuceneUtils.class, "Trying to use " + dir + " as the current but it's not a directory");
			return dir;
		}
		return dir;
	}

	public static FSDirectory getNewLuceneDir() throws IOException {
		FSDirectory dirFS = null;
		if(dirType==1){
			dirFS = new SimpleFSDirectory(new File(getNewLuceneDirPath()));
		}else if(dirType==2){
			dirFS = new NIOFSDirectory(new File(getNewLuceneDirPath()));
		}else if(dirType==3){
			dirFS = new MMapDirectory(new File(getNewLuceneDirPath()));
		}else{
			dirFS = FSDirectory.open(new File(getNewLuceneDirPath()));
		}
		return dirFS;
	}

	public static String getNewLuceneDirPath() {
		String baseDir = getBaseLuceneDir();

		String dir = "";
		dir = baseDir + readIndexNumberFromFile(true) + File.separator;
		File dirFile = new File(dir);
		if (!dirFile.exists())
			dirFile.mkdirs();
		if (dirFile.exists() && !dirFile.isDirectory()) {
			Logger.fatal(LuceneUtils.class, "Trying to use " + dir + " as the current but it's not a directory");
			return dir;
		}
		return dir;
	}

	// Method to change the current index pointer to the new index created at
	// the reindexation
	public synchronized static void changeToTheNewLuceneDir() {
		long newi = readIndexNumberFromFile(true);
		saveIndexNumberToFile(newi, false);
		removeNewIndexFile();
	}

	// Method to recreate the index to be used in the reindexation
	public synchronized static boolean createNewIndexFolder() {
		File indexNumberStoreFile = new File(getBaseLuceneDir() + "newindex.txt");
		if (indexNumberStoreFile.exists()) {
			Logger.warn(LuceneUtils.class, "You cannot create a new index because one is already created");
			return false;
		}
		String baseDir = getBaseLuceneDir();

		long fname = buildFolderName();
		String dir = "";
		dir = baseDir + fname + File.separator;
		File dirFile = new File(dir);
		if (!dirFile.exists())
			dirFile.mkdirs();
		saveIndexNumberToFile(fname, true);
		boolean result = true;
		// IndexModifier newIndexModifier = null;
		/**/IndexWriter newIndexModifier = null;

		FSDirectory dirFS = null;
		try {
			Logger.debug(LuceneUtils.class, "Creting new index at: " + getNewLuceneDirPath());
			dirFS = getNewLuceneDir();
			if (IndexWriter.isLocked(dirFS)) {
				IndexWriter.unlock(dirFS);
			}
			UtilMethods.revomeDir(dirFS.getFile().getPath());
			dirFS.getFile().mkdirs();
			newIndexModifier = new IndexWriter(dirFS, new WhitespaceAnalyzer(), true, MaxFieldLength.UNLIMITED);

			newIndexModifier.setUseCompoundFile(true);
		} catch (IOException e) {
			Logger.fatal(LuceneUtils.class, "Error encountered trying to create the new index dir: "
					+ getNewLuceneDirPath()
					+ ", but no more usable directories has been found!!. Check the directories permissions.", e);
			result = false;
		} finally {
			if (newIndexModifier != null) {
				try {
					newIndexModifier.close();
				} catch (IOException e) {
					Logger.error(LuceneUtils.class, "Error encountered trying to create the new index dir: "
							+ getNewLuceneDirPath(), e);
				}
			}
			if(dirFS!=null){
				try{
					dirFS.close();
				}catch (Exception e) {
					Logger.error(LuceneUtils.class, e.getMessage(),e);
				}
			}
		}
		return result;
	}

	// Methods to write documents to the indexes
	public synchronized static void removeDocByIdenToReindexationIndex(String identifier) {
		try {
			IndexWriter newIndexModifier = LuceneNewIndexObjectHolder.getInstance().checkoutWriter();
			newIndexModifier.deleteDocuments(new Term("identifier", identifier));
		} catch (Exception e) {
			Logger.error(LuceneUtils.class, "Error ocurred trying to write a document to the reindexation index.", e);
		} finally {
			try {
				LuceneNewIndexObjectHolder.getInstance().checkinWriter();
			} catch (Exception e) {
				Logger.error(LuceneUtils.class,
						"Error ocurred trying to close the modifier for the reindexation index.", e);
			}
		}
	}

	public synchronized static void removeDocByInodeToReindexationIndex(String inode) {
		try {
			IndexWriter newIndexModifier = LuceneNewIndexObjectHolder.getInstance().checkoutWriter();
			newIndexModifier.deleteDocuments(new Term("inode", inode));
		} catch (Exception e) {
			Logger.error(LuceneUtils.class, "Error ocurred trying to write a document to the reindexation index.", e);
		} finally {
			try {
				LuceneNewIndexObjectHolder.getInstance().checkinWriter();
			} catch (Exception e) {
				Logger.error(LuceneUtils.class,
						"Error ocurred trying to close the modifier for the reindexation index.", e);
			}
		}
	}

	@SuppressWarnings("finally")
	public synchronized static boolean writeDocsToReindexationIndex(List<Document> docs, String... identifierToClear) {
		boolean returnValue = true;
		IndexWriter newIndexModifier = null;
		try {
			newIndexModifier = LuceneNewIndexObjectHolder.getInstance().checkoutWriter();
			
			for (String ident : identifierToClear) {
				newIndexModifier.deleteDocuments(new Term("identifier", ident.toLowerCase()));
			}
			for (Document doc : docs) {
				newIndexModifier.deleteDocuments(new Term("inode", doc.get("inode")));
				newIndexModifier.addDocument(doc);
			}
		} catch (Exception e) {
			Logger.error(LuceneUtils.class, "Error ocurred trying to write a document to the reindexation index.", e);
			returnValue = false;
		} finally {
			try {
				LuceneNewIndexObjectHolder.getInstance().checkinWriter();
			} catch (Exception e) {
				Logger.error(LuceneUtils.class,
						"Error ocurred trying to close the modifier for the reindexation index.", e);
			}
			return returnValue;
		}
	}

	public synchronized static void removeDocByIdenToCurrentIndex(String identifier) {
		removeDocFromCurrentIndex(identifier, "identifier");
	}

	public synchronized static void removeDocByInodeToCurrentIndex(String inode) {
		removeDocFromCurrentIndex(inode, "inode");
	}
	
	public synchronized static void removeDocByStructureToCurrentIndex(String structureName) {
		removeDocFromCurrentIndex(structureName, "structureName");
	}	

	private static void removeDocFromCurrentIndex(String valueToDelete, String fieldToFind) {
		IndexWriter currentIndexModifier = null;
		try {
			Term term = new Term(fieldToFind, valueToDelete);
			currentIndexModifier = LuceneCurrentIndexObjectHolder.getInstance().checkoutWriter();
			currentIndexModifier.deleteDocuments(term);
		} catch (Exception e) {
			Logger.error(LuceneUtils.class, "Error ocurred trying to write a document to the reindexation index.", e);
		} finally {
			try {
				LuceneCurrentIndexObjectHolder.getInstance().checkinWriter();
			} catch (Exception e) {
				Logger.error(LuceneUtils.class,
						"Error ocurred trying to close the modifier for the reindexation index.", e);
			}
		}
	}

	/**
	 * 
	 * @param docs
	 * @param identifierToClear
	 *            The writer will delete all passed in identifiers before
	 *            writing docs
	 * @return
	 */
	@SuppressWarnings("finally")
	public synchronized static boolean writeDocsToCurrentIndex(List<Document> docs, String... identifierToClear) {
		boolean returnValue = true;
		IndexWriter currentIndexModifier = null;
		try {
			currentIndexModifier = LuceneCurrentIndexObjectHolder.getInstance().checkoutWriter();
			for (String ident : identifierToClear) {
				currentIndexModifier.deleteDocuments(new Term("identifier", ident.toLowerCase()));
			}
			for (Document doc : docs) {
				currentIndexModifier.deleteDocuments(new Term("inode", doc.get("inode")));
				currentIndexModifier.addDocument(doc);
			}
		} catch (Exception e) {
			Logger.error(LuceneUtils.class, "Error ocurred trying to write a document to the reindexation index.", e);
			returnValue = false;
		} finally {
			try {
				LuceneCurrentIndexObjectHolder.getInstance().checkinWriter();
			} catch (Exception e) {
				Logger.error(LuceneUtils.class,
						"Error ocurred trying to close the modifier for the reindexation index.", e);
			}
			return returnValue;
		}
	}

	/**
	 * This is method should be used only at startup to
	 * initialize/unblock/create the current lucene index
	 * 
	 */
	public synchronized static void checkAndInitialiazeIndex() {
		String currentIndexDir = getCurrentLuceneDirPath();
		boolean buildingNewIndex = ReindexationProcessStatus.inFullReindexation();
		Directory d = null;
		try {
			d= getCurrentLuceneDir();
			File currentIndexDirFile = new File(currentIndexDir);
			if (!IndexReader.indexExists(d)) {
				currentIndexDirFile.delete();
				currentIndexDirFile.mkdirs();
				IndexWriter.unlock(d);
			} else {
				if (IndexWriter.isLocked(d))
					IndexWriter.unlock(d);
			}
			if (buildingNewIndex) {
				Directory dNew = getNewLuceneDir();
				IndexWriter.unlock(dNew);
			}
		} catch (IOException e) {
			Logger.fatal(LuceneUtils.class, "Error ocurred initialing the indexes.", e);
		}finally{
			if(d != null){
				try {
					d.close();
				} catch (IOException e) {
					Logger.error(LuceneUtils.class,e.getMessage(),e);
				}
			}
		}
	}

	public static LuceneHits searchInCurrentIndex(String query, int offset, int limit, String sortBy)
			throws ParseException {
		boolean profile = Config.getBooleanProperty("LUCENE_PROFILING", false);
		Long profileTime = null;
		if(profile){
			profileTime=Calendar.getInstance().getTimeInMillis();
			LuceneProfiler.log(LuceneUtils.class, "searchInCurrentIndex", "Entered method");
		}
		LuceneHits lhits = new LuceneHits();
		IndexSearcher searcher = null;
		try {
			TopDocs hits = null;
			PreparedQuery preparedQuery = prepareQuery(query, sortBy);
			Query lq = preparedQuery.getLuceneQuery();
			if(profile){
				LuceneProfiler.log(LuceneUtils.class, "searchInCurrentIndex", "About to checkout IndexSearcher");
			}
			searcher = LuceneCurrentIndexObjectHolder.getInstance().checkOutSearcher();
			if(profile){
				LuceneProfiler.log(LuceneUtils.class, "searchInCurrentIndex", "About to Search");
			}
			hits = searcher.search(lq, null, 100000, buildSort(preparedQuery.getTranslatedQuery().getSortBy()));
			if(profile){
				LuceneProfiler.log(LuceneUtils.class, "searchInCurrentIndex", "About to Record Hits");
			}
			lhits.recordHits(hits, offset, limit, preparedQuery.getTranslatedQuery().getSortBy(), searcher);
			lhits.setLuceneQuery(lq.toString());
			if(profile){
				LuceneProfiler.log(LuceneUtils.class, "searchInCurrentIndex", "Done recording hits");
			}
		} catch (FileNotFoundException fe) {
			Logger.info(LuceneUtils.class,
					"Unable to find Lucene Directory : Shouldn't be a problem as it should be auto created");

			synchronized (LuceneUtils.class) {
				IndexWriter indexModifier = null;
				FSDirectory dirFS = null;
				try {
					Logger.debug(LuceneUtils.class, "Creting new index at: " + getCurrentLuceneDir());
					dirFS = getCurrentLuceneDir();
					if (IndexWriter.isLocked(dirFS)) {
						IndexWriter.unlock(dirFS);
					}
					UtilMethods.revomeDir(dirFS.getFile().getPath());
					dirFS.getFile().mkdirs();
					indexModifier = new IndexWriter(dirFS, new WhitespaceAnalyzer(), true, MaxFieldLength.UNLIMITED);

					indexModifier.setUseCompoundFile(true);
				} catch (Exception e) {
					Logger.fatal(LuceneUtils.class, "Error encountered trying to create the new index dir: "
							+ getCurrentLuceneDirPath()
							+ ", but no more usable directories has been found!!. Check the directories permissions.",
							e);
				} finally {
					if (indexModifier != null) {
						try {
							indexModifier.close();
						} catch (IOException e) {
							Logger.error(LuceneUtils.class, "Error encountered trying to create the new index dir: "
									+ getCurrentLuceneDirPath(), e);
						}
					}
					if(dirFS!=null){
						try{
							dirFS.close();
						}catch (Exception e) {
							Logger.error(LuceneUtils.class, e.getMessage(),e);
						}
					}
				}
			}

		} catch (CorruptIndexException e) {
			Logger.error(LuceneUtils.class, "Error ocurred trying to search results in the current index.", e);
		}catch(AlreadyClosedException ace){
			try {
				Logger.error(LuceneUtils.class,ace.getMessage(),ace);
				LuceneCurrentIndexObjectHolder.getInstance().ensureSearcherIsValid(searcher);
				searcher = null;
			} catch (CorruptIndexException e) {
				Logger.error(LuceneUtils.class,e.getMessage(),e);
			} catch (IOException e) {
				Logger.error(LuceneUtils.class,e.getMessage(),e);
			} 
		}catch(ClosedChannelException cce){
			try {
				Logger.error(LuceneUtils.class,cce.getMessage(),cce);
				LuceneCurrentIndexObjectHolder.getInstance().ensureSearcherIsValid(searcher);
				searcher = null;
			} catch (CorruptIndexException e) {
				Logger.error(LuceneUtils.class,e.getMessage(),e);
			} catch (IOException e) {
				Logger.error(LuceneUtils.class,e.getMessage(),e);
			} 
		} catch (IOException ioe) {
			try {
				Logger.error(LuceneUtils.class,ioe.getMessage(),ioe);
				LuceneCurrentIndexObjectHolder.getInstance().ensureSearcherIsValid(searcher);
				searcher = null;
			} catch (CorruptIndexException e) {
				Logger.error(LuceneUtils.class,e.getMessage(),e);
			} catch (IOException e) {
				Logger.error(LuceneUtils.class,e.getMessage(),e);
			}
		} catch (Exception e) {
			Logger.error(LuceneUtils.class, "Error ocurred trying to search results in the current index.", e);
		} finally {
			if (searcher != null)
				try {
					if(profile){
						LuceneProfiler.log(LuceneUtils.class, "searchInCurrentIndex", "Checking in IndexSearcher");
					}
					LuceneCurrentIndexObjectHolder.getInstance().checkInSearcher(searcher);
				} catch (IOException e) {
					Logger.error(LuceneUtils.class,
							"Error ocurred trying to close the searcher over the results in the current index.", e);
				}
		}
		if(profile){
	    	profileTime=Calendar.getInstance().getTimeInMillis()-profileTime;
			LuceneProfiler.log(LuceneUtils.class, "searchInCurrentIndex", "Exiting method : Time in Method : " + profileTime);
		}
		return lhits;
	}

	public static LuceneHits searchInReindexationIndex(String query, int offset, int limit, String sortBy)
			throws ParseException {
		boolean profile = Config.getBooleanProperty("LUCENE_PROFILING", false);
		Long profileTime = null;
		if(profile){
			profileTime=Calendar.getInstance().getTimeInMillis();
			LuceneProfiler.log(LuceneUtils.class, "searchInReindexationIndex", "Entered method");
		}
		LuceneHits lhits = new LuceneHits();
		IndexSearcher searcher = null;
		try {
			TopDocs hits = null;
			PreparedQuery preparedQuery = prepareQuery(query, sortBy);
			Query lq = preparedQuery.getLuceneQuery();
			if(profile){
				LuceneProfiler.log(LuceneUtils.class, "searchInReindexationIndex", "Checking out IndexSearcher");
			}
			searcher = LuceneNewIndexObjectHolder.getInstance().checkOutSearcher();
			if(profile){
				LuceneProfiler.log(LuceneUtils.class, "searchInReindexationIndex", "About to search");
			}
			hits = searcher.search(lq, new QueryWrapperFilter(lq), 100000, buildSort(preparedQuery.getTranslatedQuery().getSortBy()));
			if(profile){
				LuceneProfiler.log(LuceneUtils.class, "searchInReindexationIndex", "About to record hits");
			}
			lhits.recordHits(hits, offset, limit, preparedQuery.getTranslatedQuery().getSortBy(), searcher);
			lhits.setLuceneQuery(lq.toString());
			if(profile){
				LuceneProfiler.log(LuceneUtils.class, "searchInReindexationIndex", "Done recording hits");
			}
		}catch(AlreadyClosedException ace){
			try {
				Logger.error(LuceneUtils.class,ace.getMessage(),ace);
				LuceneNewIndexObjectHolder.getInstance().ensureSearcherIsValid(searcher);
				searcher = null;
			} catch (CorruptIndexException e) {
				Logger.error(LuceneUtils.class,e.getMessage(),e);
			} catch (IOException e) {
				Logger.error(LuceneUtils.class,e.getMessage(),e);
			} 
		}catch(ClosedChannelException cce){
			try {
				Logger.error(LuceneUtils.class,cce.getMessage(),cce);
				LuceneCurrentIndexObjectHolder.getInstance().ensureSearcherIsValid(searcher);
				searcher = null;
			} catch (CorruptIndexException e) {
				Logger.error(LuceneUtils.class,e.getMessage(),e);
			} catch (IOException e) {
				Logger.error(LuceneUtils.class,e.getMessage(),e);
			} 
		} catch (IOException ioe) {
			try {
				Logger.error(LuceneUtils.class,ioe.getMessage(),ioe);
				LuceneCurrentIndexObjectHolder.getInstance().ensureSearcherIsValid(searcher);
				searcher = null;
			} catch (CorruptIndexException e) {
				Logger.error(LuceneUtils.class,e.getMessage(),e);
			} catch (IOException e) {
				Logger.error(LuceneUtils.class,e.getMessage(),e);
			} 
		} catch (Exception e) {
			Logger.error(LuceneUtils.class, "Error ocurred trying to search results in the current index.", e);
		} finally {
			if (searcher != null)
				try {
					if(profile){
						LuceneProfiler.log(LuceneUtils.class, "searchInReindexationIndex", "Checking in IndexSearcher");
					}
					LuceneNewIndexObjectHolder.getInstance().checkInSearcher(searcher);
				} catch (IOException e) {
					Logger.error(LuceneUtils.class,
							"Error ocurred trying to close the searcher over the results in the current index.", e);
				}
		}
		if(profile){
			profileTime=Calendar.getInstance().getTimeInMillis()-profileTime;
			LuceneProfiler.log(LuceneUtils.class, "searchInReindexationIndex", "Exiting method : Time Taken : " + profileTime);
		}
		return lhits;
	}

	// This method optimize the lucene index files, optimizing an index does not
	// help improve indexing performance.
	// As a matter of fact, optimizing an index during the indexing process will
	// only slow things down.
	// Despite this, optimizing may sometimes be necessary in order to keep the
	// number of open files under control.
	public static synchronized void optimizeCurrentIndex() {
		IndexWriter currentIndexModifier = null;
		try {
			currentIndexModifier = LuceneCurrentIndexObjectHolder.getInstance().checkoutWriter();
			currentIndexModifier.optimize();
		} catch (Exception e) {
			Logger.error(LuceneUtils.class, "A error ocurred trying to optimize the lucene index", e);
		} finally {
			try {
				if (currentIndexModifier != null)
					LuceneCurrentIndexObjectHolder.getInstance().checkinWriter();
			} catch (Exception e) {
				Logger.error(LuceneUtils.class, "Error ocurred trying to close the modifier for the current index.", e);
			}
		}
	}

	/**
	 * Optimizes the new index being created. Should only be called if the new
	 * index is being created. This method helps optimize it while writing to
	 * it.
	 */
	public static synchronized void optimizeNewIndex() {
		IndexWriter newIndexModifier = null;
		try {
			newIndexModifier = LuceneNewIndexObjectHolder.getInstance().checkoutWriter();
			newIndexModifier.optimize();
		} catch (Exception e) {
			Logger.error(LuceneUtils.class, "A error ocurred trying to optimize the lucene index", e);
		} finally {
			try {
				if (newIndexModifier != null)
					LuceneNewIndexObjectHolder.getInstance().checkinWriter();
			} catch (Exception e) {
				Logger.error(LuceneUtils.class, "Error ocurred trying to close the modifier for the current index.", e);
			}
		}
	}

	/**
	 * 
	 * @param field
	 * @param keyword
	 * @deprecated
	 * @return
	 */
	public static Field getKeywordField(String field, String keyword) {
		Field fieldObj = null;
		if (field.equalsIgnoreCase("inode") || field.equalsIgnoreCase("identifier")) {
			fieldObj = new Field(field.toLowerCase(), keyword.toLowerCase(), Field.Store.YES, Field.Index.NOT_ANALYZED);
		} else {
			fieldObj = new Field(field.toLowerCase(), keyword.toLowerCase(), Field.Store.NO, Field.Index.NOT_ANALYZED);
		}
		return fieldObj;
	}

	/**
	 * 
	 * @param field
	 * @param keyword
	 * @deprecated
	 * @return
	 */
	public static Field getIndexedField(String field, String keyword) {
		Field fieldObj = null;
		if (field.equalsIgnoreCase("inode") || field.equalsIgnoreCase("identifier")) {
			fieldObj = new Field(field.toLowerCase(), keyword.toLowerCase(), Field.Store.YES, Field.Index.ANALYZED);
			fieldObj = new Field(field.toLowerCase(), keyword.toLowerCase(), Field.Store.YES, Field.Index.ANALYZED);
		} else {
			fieldObj = new Field(StringUtils.lowerCase(field) + "_raw", keyword.toLowerCase(), Field.Store.NO,
					Field.Index.NOT_ANALYZED);
			fieldObj = new Field(StringUtils.lowerCase(field) + "_raw", keyword.toLowerCase(), Field.Store.NO,
					Field.Index.NOT_ANALYZED);
		}
		return fieldObj;
	}

	public static Document addFieldToLuceneDoc(Document doc, String indexName, String value, boolean analyze) {
		Field f1 = null;
		Field.Index fieldIndex = Field.Index.NOT_ANALYZED;
		if (analyze) {
			fieldIndex = Field.Index.ANALYZED;
		}
		Field.Store store = Field.Store.NO;
		if (indexName.equalsIgnoreCase("inode") || indexName.equalsIgnoreCase("identifier")) {
			store = Field.Store.YES;
		}
		f1 = new Field(StringUtils.lowerCase(indexName), StringUtils.lowerCase(value), store, fieldIndex);
		doc.add(f1);
		if (analyze) {
			Field f2 = null;
			f2 = new Field(StringUtils.lowerCase(indexName) + "_raw", StringUtils.lowerCase(value), Field.Store.NO,
					Field.Index.NOT_ANALYZED);
			doc.add(f2);
		}
		return doc;
	}

	// Query util methods
	private static final String[] SPECIAL_CHARS = new String[] { "+", "-", "&&", "||", "!", "(", ")", "{", "}", "[",
			"]", "^", "\"", "?", ":", "\\" };

	public static String escape(String text) {
		for (int i = SPECIAL_CHARS.length - 1; i >= 0; i--) {
			text = StringUtils.replace(text, SPECIAL_CHARS[i], "\\" + SPECIAL_CHARS[i]);
		}

		return text;
	}

	public static String toLuceneDateTime(String dateString) {
		String format = "MM/dd/yyyy HH:mm:ss";
		String result = toLuceneDateWithFormat(dateString, format);
		if (result.equals(ERROR_DATE)) {
			format = "MM/dd/yyyy HH:mm";
			result = toLuceneDateWithFormat(dateString, format);
		}
		return result;
	}

	public static String toLuceneDate(String dateString) {
		String format = "MM/dd/yyyy";
		return toLuceneDateWithFormat(dateString, format);
	}

	public static String toLuceneDateWithFormat(String dateString, String format) {
		try {
			if (!UtilMethods.isSet(dateString))
				return "";

			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date date = sdf.parse(dateString);
			String returnValue = LuceneUtils.toLuceneDate(date);

			return returnValue;
		} catch (Exception ex) {
			Logger.error(WebAPI.class, ex.toString());
			return ERROR_DATE;
		}
	}

	public static String toLuceneDate(Date date) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			String returnValue = df.format(date);
			return returnValue;
		} catch (Exception ex) {
			Logger.error(WebAPI.class, ex.toString());
			return ERROR_DATE;
		}
	}

	public static Date fromLuceneDate(String luceneDate) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return df.parse(luceneDate);
		} catch (java.text.ParseException e) {
			Logger.error(LuceneUtils.class, "Unable to convert luceneDate: " + luceneDate + " to a regular date.", e);
			return new Date();
		}
	}

	public static String toLuceneNumber(Number number) {
		return NumberUtil.pad(number);
	}

	public static long fromLuceneNumber(String number) {
		long returnValue = Long.parseLong(number);
		return returnValue;
	}

	public static String toLuceneTime(Date time) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("HHmmss");
			String returnValue = df.format(time);
			return returnValue;
		} catch (Exception ex) {
			Logger.error(WebAPI.class, ex.toString());
			return ERROR_DATE;
		}
	}

	public static String toLuceneTimeWithFormat(String dateString, String format) {
		try {
			if (!UtilMethods.isSet(dateString))
				return "";
			
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			Date time = sdf.parse(dateString);
			return toLuceneTime(time);
		} catch (Exception ex) {
			Logger.error(WebAPI.class, ex.toString());
			return ERROR_DATE;
		}
	}
	
	private static String replaceDateTimeWithFormat(String query, String regExp, String dateFormat) {
		List<RegExMatch> matches = RegEX.find(query, regExp);
		String originalDate;
		String luceneDate;
		StringBuilder newQuery;
		int begin;
		if ((matches != null) && (0 < matches.size())) {
			newQuery = new StringBuilder(query.length() * 2);
			begin = 0;
			for (RegExMatch regExMatch : matches) {
				originalDate = regExMatch.getMatch();
				
				if (UtilMethods.isSet(dateFormat))
					luceneDate = LuceneUtils.toLuceneDateWithFormat(originalDate, dateFormat);
				else
					luceneDate = LuceneUtils.toLuceneDateTime(originalDate);
				
				newQuery.append(query.substring(begin, regExMatch.getBegin()) + luceneDate);
				begin = regExMatch.getEnd();
			}
			
			return newQuery.append(query.substring(begin)).toString();
		}
		
		return query;
	}
	
	private static String replaceDateWithFormat(String query, String regExp) {
		List<RegExMatch> matches = RegEX.find(query, regExp);
		String originalDate;
		String luceneDate;
		StringBuilder newQuery;
		int begin;
		if ((matches != null) && (0 < matches.size())) {
			newQuery = new StringBuilder(query.length() * 2);
			begin = 0;
			for (RegExMatch regExMatch : matches) {
				originalDate = regExMatch.getMatch();
				
				luceneDate = LuceneUtils.toLuceneDate(originalDate);
				luceneDate = luceneDate.substring(0, 8) + "*";
				
				newQuery.append(query.substring(begin, regExMatch.getBegin()) + luceneDate);
				begin = regExMatch.getEnd();
			}
			
			return newQuery.append(query.substring(begin)).toString();
		}
		
		return query;
	}
	
	private static String replaceTimeWithFormat(String query, String regExp, String timeFormat) {
		List<RegExMatch> matches = RegEX.find(query, regExp);
		String originalDate;
		String luceneDate;
		StringBuilder newQuery;
		int begin;
		if ((matches != null) && (0 < matches.size())) {
			newQuery = new StringBuilder(query.length() * 2);
			begin = 0;
			for (RegExMatch regExMatch : matches) {
				originalDate = regExMatch.getMatch();
				
				luceneDate = LuceneUtils.toLuceneTimeWithFormat(originalDate, timeFormat);
				
				newQuery.append(query.substring(begin, regExMatch.getBegin()) + luceneDate);
				begin = regExMatch.getEnd();
			}
			
			return newQuery.append(query.substring(begin)).toString();
		}
		
		return query;
	}
	
	public static String findAndReplaceQueryDates(String query) {
		query = RegEX.replaceAll(query, " ", "\\s{2,}");
		
		List<RegExMatch> matches = RegEX.find(query, "[\\+\\-\\!\\(]?" + LUCENE_STRUCTURE_FIELD + ":(\\S+)\\)?");
		String structureVarName = null;
		if ((matches != null) && (0 < matches.size()))
			structureVarName = matches.get(0).getGroups().get(0).getMatch();
		
		if (!UtilMethods.isSet(structureVarName)) {
			matches = RegEX.find(query, "[\\+\\-\\!\\(]?" + LUCENE_STRUCTURE_FIELD.toLowerCase() + ":(\\S+)\\)?");
			if ((matches != null) && (0 < matches.size()))
				structureVarName = matches.get(0).getGroups().get(0).getMatch();
		}
		
		if (!UtilMethods.isSet(structureVarName)) {
			Logger.debug(LuceneUtils.class, "Structure Variable Name not found");
			//return query;
		}
		
		Structure selectedStructure = StructureCache.getStructureByVelocityVarName(structureVarName);
		
		if ((selectedStructure == null) || !InodeUtils.isSet(selectedStructure.getInode())) {
			Logger.debug(LuceneUtils.class, "Structure not found");
			//return query;
		}
		
		//delete additional blank spaces on date range
		if(UtilMethods.contains(query, "[ ")) {
			query = query.replace("[ ", "[");
		}
		
		if(UtilMethods.contains(query, " ]")) {
			query = query.replace(" ]", "]");
		}
		
		String clausesStr = RegEX.replaceAll(query, "", "[\\+\\-\\(\\)]*");
		String[] tokens = clausesStr.split(" ");
		String token;
		List<String> clauses = new ArrayList<String>();
		for (int pos = 0; pos < tokens.length; ++pos) {
			token = tokens[pos];
			if (token.matches("\\S+\\.\\S+:\\S*")) {
				clauses.add(token);
			} else if (token.matches("\\d+:\\S*")) {
				clauses.set(clauses.size() - 1, clauses.get(clauses.size() - 1) + " " + token);
			} else if (token.matches("\\[\\S*")) {
				clauses.set(clauses.size() - 1, clauses.get(clauses.size() - 1) + token);
			} else if (token.matches("to")) {
				clauses.set(clauses.size() - 1, clauses.get(clauses.size() - 1) + " " + token);
			} else if (token.matches("\\S*\\]")) {
				clauses.set(clauses.size() - 1, clauses.get(clauses.size() - 1) + " " + token);
			} else if (token.matches("\\d{1,2}/\\d{1,2}/\\d{4}")) {
				clauses.set(clauses.size() - 1, clauses.get(clauses.size() - 1) + " " + token);
			} else {
				clauses.add(token);
			}
		}
		
		//DOTCMS - 4127
		List<com.dotmarketing.portlets.structure.model.Field> dateFields = new ArrayList<com.dotmarketing.portlets.structure.model.Field>();
		String tempStructureVarName;
		Structure tempStructure;
		
		for (String clause: clauses) {
		
			// getting structure names from query
			if(clause.indexOf('.') >= 0 && (clause.indexOf('.') < clause.indexOf(':'))){
			
				tempStructureVarName = clause.substring(0, clause.indexOf('.'));
				tempStructure = StructureCache.getStructureByVelocityVarName(tempStructureVarName);
				
				List<com.dotmarketing.portlets.structure.model.Field> tempStructureFields = FieldsCache.getFieldsByStructureVariableName(tempStructure.getVelocityVarName());
			
				for (int pos = 0; pos < tempStructureFields.size();) {
				
					if (tempStructureFields.get(pos).getFieldType().equals(com.dotmarketing.portlets.structure.model.Field.FieldType.DATE_TIME.toString()) ||
							tempStructureFields.get(pos).getFieldType().equals(com.dotmarketing.portlets.structure.model.Field.FieldType.DATE.toString()) ||
							tempStructureFields.get(pos).getFieldType().equals(com.dotmarketing.portlets.structure.model.Field.FieldType.TIME.toString())) {
						++pos;
					} else {
						tempStructureFields.remove(pos);
					}
					
				}
				
				dateFields.addAll(tempStructureFields);
			}				
		}
		
		String replace;
		for (String clause: clauses) {
			for (com.dotmarketing.portlets.structure.model.Field field: dateFields) {
				
				structureVarName = StructureCache.getStructureByInode(field.getStructureInode()).getVelocityVarName().toLowerCase();
				
				if (clause.startsWith(structureVarName + "." + field.getVelocityVarName().toLowerCase() + ":") || clause.startsWith("moddate:")) {
					replace = new String(clause);
					if (field.getFieldType().equals(com.dotmarketing.portlets.structure.model.Field.FieldType.DATE_TIME.toString()) || clause.startsWith("moddate:")) {
						matches = RegEX.find(replace, "\\[(\\d{1,2}/\\d{1,2}/\\d{4}) to ");
						for (RegExMatch regExMatch : matches) {
							replace = replace.replace("[" + regExMatch.getGroups().get(0).getMatch() + " to ", "["
									+ regExMatch.getGroups().get(0).getMatch() + " 00:00:00 to ");
						}
						
						matches = RegEX.find(replace, " to (\\d{1,2}/\\d{1,2}/\\d{4})\\]");
						for (RegExMatch regExMatch : matches) {
							replace = replace.replace(" to " + regExMatch.getGroups().get(0).getMatch() + "]", " to "
									+ regExMatch.getGroups().get(0).getMatch() + " 23:59:59]");
						}
					}
					
					// Format MM/dd/yyyy hh:mm:ssa
					replace = replaceDateTimeWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}(?:AM|PM|am|pm))\\\"?", "MM/dd/yyyy hh:mm:ssa");
					
					// Format MM/dd/yyyy hh:mm:ss a
					replace = replaceDateTimeWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}\\s+(?:AM|PM|am|pm))\\\"?", "MM/dd/yyyy hh:mm:ss a");
					
					// Format MM/dd/yyyy hh:mm a
					replace = replaceDateTimeWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{1,2}\\s+(?:AM|PM|am|pm))\\\"?", "MM/dd/yyyy hh:mm a");
					
					// Format MM/dd/yyyy hh:mma
					replace = replaceDateTimeWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{1,2}(?:AM|PM|am|pm))\\\"?", "MM/dd/yyyy hh:mma");
					
					// Format MM/dd/yyyy HH:mm:ss
					replace = replaceDateTimeWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2})\\\"?", null);
					
					// Format MM/dd/yyyy HH:mm
					replace = replaceDateTimeWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4}\\s+\\d{1,2}:\\d{1,2})\\\"?", null);
					
					// Format MM/dd/yyyy
					replace = replaceDateWithFormat(replace, "\\\"?(\\d{1,2}/\\d{1,2}/\\d{4})\\\"?");
					
					// Format hh:mm:ssa
					replace = replaceTimeWithFormat(replace, "\\\"?(\\d{1,2}:\\d{1,2}:\\d{1,2}(?:AM|PM|am|pm))\\\"?", "hh:mm:ssa");
					
					// Format hh:mm:ss a
					replace = replaceTimeWithFormat(replace, "\\\"?(\\d{1,2}:\\d{1,2}:\\d{1,2}\\s+(?:AM|PM|am|pm))\\\"?", "hh:mm:ss a");
					
					// Format HH:mm:ss
					replace = replaceTimeWithFormat(replace, "\\\"?(\\d{1,2}:\\d{1,2}:\\d{1,2})\\\"?", "HH:mm:ss");
					
					// Format hh:mma
					replace = replaceTimeWithFormat(replace, "\\\"?(\\d{1,2}:\\d{1,2}(?:AM|PM|am|pm))\\\"?", "hh:mma");
					
					// Format hh:mm a
					replace = replaceTimeWithFormat(replace, "\\\"?(\\d{1,2}:\\d{1,2}\\s+(?:AM|PM|am|pm))\\\"?", "hh:mm a");
					
					// Format HH:mm
					replace = replaceTimeWithFormat(replace, "\\\"?(\\d{1,2}:\\d{1,2})\\\"?", "HH:mm");
					
					query = query.replace(clause, replace);
					
					break;
				}
			}
		}
		
		matches = RegEX.find(query, "\\[([0-9]*)(\\*+) to ");
		for (RegExMatch regExMatch : matches) {
			query = query.replace("[" + regExMatch.getGroups().get(0).getMatch() + regExMatch.getGroups().get(1).getMatch() + " to ", "["
					+ regExMatch.getGroups().get(0).getMatch() + " to ");
		}
		
		matches = RegEX.find(query, " to ([0-9]*)(\\*+)\\]");
		for (RegExMatch regExMatch : matches) {
			query = query.replace(" to " + regExMatch.getGroups().get(0).getMatch() + regExMatch.getGroups().get(1).getMatch() + "]", " to "
					+ regExMatch.getGroups().get(0).getMatch() + "]");
		}
		
		matches = RegEX.find(query, "\\[([0-9]*) (to) ([0-9]*)\\]");
		for (RegExMatch regExMatch : matches) {
			query = query.replace("[" + regExMatch.getGroups().get(0).getMatch() + " to "
					+ regExMatch.getGroups().get(2).getMatch() + "]", "["
					+ regExMatch.getGroups().get(0).getMatch() + " TO " + regExMatch.getGroups().get(2).getMatch()
					+ "]");
		}

		return query;
	}
	
	protected static class PreparedQuery {
		private TranslatedQuery translatedQuery;
		private Query luceneQuery;
		
		/**
		 * @return the translatedQuery
		 */
		public TranslatedQuery getTranslatedQuery() {
			return translatedQuery;
		}
		/**
		 * @param translatedQuery the translatedQuery to set
		 */
		public void setTranslatedQuery(TranslatedQuery translatedQuery) {
			this.translatedQuery = translatedQuery;
		}
		/**
		 * @return the luceneQuery
		 */
		public Query getLuceneQuery() {
			return luceneQuery;
		}
		/**
		 * @param luceneQuery the luceneQuery to set
		 */
		public void setLuceneQuery(Query luceneQuery) {
			this.luceneQuery = luceneQuery;
		}
	}
	
	protected static PreparedQuery prepareQuery(String query, String sortBy) throws ParseException {
		TranslatedQuery translatedQuery = translateQuery(query, sortBy);
		translatedQuery.setQuery(LuceneUtils.findAndReplaceQueryDates(translatedQuery.getQuery()));
		
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "", new WhitespaceAnalyzer());
		parser.setAllowLeadingWildcard(true);
		Query lq = parser.parse(translatedQuery.getQuery());
		
		PreparedQuery preparedQuery = new PreparedQuery();
		preparedQuery.setTranslatedQuery(translatedQuery);
		preparedQuery.setLuceneQuery(lq);
		
		return preparedQuery;
	}
	
	public static class TranslatedQuery implements Serializable {

		private static final long serialVersionUID = 1L;
		private String query;
		private String sortBy;
		
		/**
		 * @return the query
		 */
		public String getQuery() {
			return query;
		}
		/**
		 * @param query the query to set
		 */
		public void setQuery(String query) {
			this.query = query;
		}
		/**
		 * @return the sortBy
		 */
		public String getSortBy() {
			return sortBy;
		}
		/**
		 * @param sortBy the sortBy to set
		 */
		public void setSortBy(String sortBy) {
			this.sortBy = sortBy;
		}
	}
	
	protected static LRUMap translatedQueryCache = new LRUMap(5000);
	public static TranslatedQuery translateQuery(String query, String sortBy) {
		
		TranslatedQuery result = (TranslatedQuery) translatedQueryCache.get(query + " --- " + sortBy);
		if(result != null) 
			return result;
		
		result = new TranslatedQuery();
			
		String originalQuery = query;
		Structure st = null;
		String stInodestr = "structureInode";
		String stInodeStrLowered = "structureinode";
		String stNameStrLowered = "structurename";
		
		if (query.contains(stNameStrLowered))
			query = query.replace(stNameStrLowered,LUCENE_STRUCTURE_FIELD);
		
		if (query.contains(stInodeStrLowered))
			query = query.replace(stInodeStrLowered,stInodestr);
		
		if (query.contains(stInodestr)) {
			// get structure information
			int index = 0;
			index = query.indexOf(stInodestr) + stInodestr.length() + 1;
			String inode = null;
			try {
				inode = query.substring(index, query.indexOf(" ", index));
			} catch (StringIndexOutOfBoundsException e) {
				Logger.debug(LuceneUtils.class, e.toString());
				inode = query.substring(index);
			}
			st = StructureCache.getStructureByInode(inode);
			if (!InodeUtils.isSet(st.getInode()) || !UtilMethods.isSet(st.getVelocityVarName())) {
				Logger.error(LuceneUtils.class,
						"Unable to find Structure or Structure Velocity Variable Name from passed in structureInode Query : "
								+ query);
				
				result.setQuery(query);
				result.setSortBy(sortBy);
				
				return result;
			}
	
			// replace structureInode
			query = query.replace("structureInode:"+inode, LUCENE_STRUCTURE_FIELD + ":" + st.getVelocityVarName());
	
			// handle the field translation
			List<com.dotmarketing.portlets.structure.model.Field> fields = FieldsCache.getFieldsByStructureVariableName(st.getVelocityVarName());
			Map<String, com.dotmarketing.portlets.structure.model.Field> fieldsMap;
			try {
				fieldsMap = UtilMethods.convertListToHashMap(fields, "getFieldContentlet", String.class);
			} catch (Exception e) {
				Logger.error(LuceneUtils.class, e.getMessage(), e);
				result.setQuery(query);
				result.setSortBy(sortBy);
				return result;
			}
			String[] matcher = { "date", "text", "text_area", "integer", "float", "bool" };
			for (String match : matcher) {
				if (query.contains(match)) {
					List<RegExMatch> mathes = RegEX.find(query, match + "([1-9][1-5]?):");
					for (RegExMatch regExMatch : mathes) {
						String oldField = regExMatch.getMatch().substring(0, regExMatch.getMatch().indexOf(":"));
						query = query.replace(oldField, st.getVelocityVarName() + "."
								+ fieldsMap.get(oldField).getVelocityVarName());
					}
				}
			}
	
			// handle categories
			String catRegExpr = "((c(([a-f0-9]{8,8})\\-([a-f0-9]{4,4})\\-([a-f0-9]{4,4})\\-([a-f0-9]{4,4})\\-([a-f0-9]{12,12}))c:on)|(c[0-9]*c:on))";//DOTCMS-4564
			if (RegEX.contains(query, catRegExpr)) {
				List<RegExMatch> mathes = RegEX.find(query, catRegExpr);
				for (RegExMatch regExMatch : mathes) {
					try {
						String catInode = regExMatch.getGroups().get(0).getMatch().substring(1, regExMatch.getGroups().get(0).getMatch().indexOf("c:on"));
						query = query.replace(regExMatch.getMatch(), "categories:"
								+ APILocator.getCategoryAPI().find(catInode,
										APILocator.getUserAPI().getSystemUser(), true).getCategoryVelocityVarName());
					} catch (Exception e) {
						Logger.error(LuceneUtils.class, e.getMessage() + " : Error loading category", e);
						result.setQuery(query);
						result.setSortBy(sortBy);
						return result;
					}
				}
			}
			
			result.setSortBy(translateQuerySortBy(sortBy, originalQuery));
		}
		
		//Pad Numbers
		List<RegExMatch> numberMatches = RegEX.find(query, "(\\w+)\\.(\\w+):([0-9]+\\.?[0-9]+ |\\.?[0-9]+ |[0-9]+\\.?[0-9]+$|\\.?[0-9]+$)");
		if(numberMatches != null && numberMatches.size() > 0){
			for (RegExMatch numberMatch : numberMatches) {
				List<com.dotmarketing.portlets.structure.model.Field> fields = FieldsCache.getFieldsByStructureVariableName(numberMatch.getGroups().get(0).getMatch());
				for (com.dotmarketing.portlets.structure.model.Field field : fields) {
					if(field.getVelocityVarName().equalsIgnoreCase(numberMatch.getGroups().get(1).getMatch())){
						if (field.getFieldContentlet().startsWith("float")) {
							query = query.replace(numberMatch.getGroups().get(0).getMatch() + "." + numberMatch.getGroups().get(1).getMatch() + ":" + numberMatch.getGroups().get(2).getMatch(),
									numberMatch.getGroups().get(0).getMatch() + "." + numberMatch.getGroups().get(1).getMatch() + ":" + toLuceneNumber(NumberUtils.parseNumber((numberMatch.getGroups().get(2).getMatch()),Float.class)) + " ");
						}else if(field.getFieldContentlet().startsWith("integer")) {
							query = query.replace(numberMatch.getGroups().get(0).getMatch() + "." + numberMatch.getGroups().get(1).getMatch() + ":" + numberMatch.getGroups().get(2).getMatch(),
									numberMatch.getGroups().get(0).getMatch() + "." + numberMatch.getGroups().get(1).getMatch() + ":" + toLuceneNumber(NumberUtils.parseNumber((numberMatch.getGroups().get(2).getMatch()),Long.class)) + " ");
						}else if(field.getFieldContentlet().startsWith("bool")) {
							String oldSubQuery = numberMatch.getGroups().get(0).getMatch() + "." + numberMatch.getGroups().get(1).getMatch() + ":" + numberMatch.getGroups().get(2).getMatch();
							String oldFieldBooleanValue = oldSubQuery.substring(oldSubQuery.indexOf(":")+1,oldSubQuery.indexOf(":") + 2);
							String newFieldBooleanValue="";
							if(oldFieldBooleanValue.equals("1") || oldFieldBooleanValue.equals("true"))
								newFieldBooleanValue = "true";
							else if(oldFieldBooleanValue.equals("0") || oldFieldBooleanValue.equals("false"))
								newFieldBooleanValue = "false";
							query = query.replace(numberMatch.getGroups().get(0).getMatch() + "." + numberMatch.getGroups().get(1).getMatch() + ":" + numberMatch.getGroups().get(2).getMatch(),
									numberMatch.getGroups().get(0).getMatch() + "." + numberMatch.getGroups().get(1).getMatch() + ":" + newFieldBooleanValue + " ");
						}
					}
				}
			}
		}
		
		if (UtilMethods.isSet(sortBy))
			result.setSortBy(translateQuerySortBy(sortBy, query));
		
		// DOTCMS-6247
		query = query.toLowerCase();
		//Pad NumericalRange Numbers 
		List<RegExMatch> numberRangeMatches = RegEX.find(query, "(\\w+)\\.(\\w+):\\[(([0-9]+\\.?[0-9]+ |\\.?[0-9]+ |[0-9]+\\.?[0-9]+|\\.?[0-9]+) to ([0-9]+\\.?[0-9]+ |\\.?[0-9]+ |[0-9]+\\.?[0-9]+|\\.?[0-9]+))\\]");
		if(numberRangeMatches != null && numberRangeMatches.size() > 0){
			for (RegExMatch numberMatch : numberRangeMatches) {
				List<com.dotmarketing.portlets.structure.model.Field> fields = FieldsCache.getFieldsByStructureVariableName(numberMatch.getGroups().get(0).getMatch());
				for (com.dotmarketing.portlets.structure.model.Field field : fields) {
					if(field.getVelocityVarName().equalsIgnoreCase(numberMatch.getGroups().get(1).getMatch())){
						if (field.getFieldContentlet().startsWith("float")) {
							query = query.replace(numberMatch.getGroups().get(0).getMatch() + "." + numberMatch.getGroups().get(1).getMatch() + ":[" + numberMatch.getGroups().get(3).getMatch() + " to " + numberMatch.getGroups().get(4).getMatch() +"]",
									numberMatch.getGroups().get(0).getMatch() + "." + numberMatch.getGroups().get(1).getMatch() + ":[" + toLuceneNumber(NumberUtils.parseNumber((numberMatch.getGroups().get(3).getMatch()),Float.class)) + " TO " + toLuceneNumber(NumberUtils.parseNumber((numberMatch.getGroups().get(4).getMatch()),Float.class)) + "]");
						}else if(field.getFieldContentlet().startsWith("integer")) {
							query = query.replace(numberMatch.getGroups().get(0).getMatch() + "." + numberMatch.getGroups().get(1).getMatch() + ":[" + numberMatch.getGroups().get(3).getMatch() + " to " + numberMatch.getGroups().get(4).getMatch() +"]",
									numberMatch.getGroups().get(0).getMatch() + "." + numberMatch.getGroups().get(1).getMatch() + ":[" + toLuceneNumber(NumberUtils.parseNumber((numberMatch.getGroups().get(3).getMatch()),Long.class)) + " TO " + toLuceneNumber(NumberUtils.parseNumber((numberMatch.getGroups().get(4).getMatch()),Long.class)) + "]");
						}
					}
				}
			}
		}
		
		
		result.setQuery(query.trim());
		
		synchronized (translatedQueryCache) {
			translatedQueryCache.put(originalQuery + " --- " + sortBy, result);
		}
		
		return result;
	}
	
	private static String translateQuerySortBy(String sortBy, String originalQuery) {
		
		if(sortBy == null)
			return null;
		
		List<RegExMatch> matches = RegEX.find(originalQuery, LUCENE_STRUCTURE_FIELD + ":([^\\s)]+)");
		List<com.dotmarketing.portlets.structure.model.Field> fields = null;
		Structure structure = null;
		if(matches.size() > 0) {
			String structureName = matches.get(0).getGroups().get(0).getMatch();
			fields = FieldsCache.getFieldsByStructureVariableName(structureName);
			structure = StructureCache.getStructureByVelocityVarName(structureName);
		} else {
			matches = RegEX.find(originalQuery, "structureInode:([^\\s)]+)");
			if(matches.size() > 0) {
				String structureInode = matches.get(0).getGroups().get(0).getMatch();
				fields = FieldsCache.getFieldsByStructureInode(structureInode);
				structure = StructureCache.getStructureByInode(structureInode);
			}
		}
		
		if(fields == null)
			return sortBy;
		
		Map<String, com.dotmarketing.portlets.structure.model.Field> fieldsMap;
		try {
			fieldsMap = UtilMethods.convertListToHashMap(fields, "getFieldContentlet", String.class);
		} catch (Exception e) {
			Logger.error(LuceneUtils.class, e.getMessage(), e);
			return sortBy;
		}
		
		String[] matcher = { "date", "text", "text_area", "integer", "float", "bool" };
		List<RegExMatch> mathes;
		String oldField, oldFieldTrim, newField;
		for (String match : matcher) {
			if (sortBy.contains(match)) {
				mathes = RegEX.find(sortBy, match + "([1-9][1-5]?)");
				for (RegExMatch regExMatch : mathes) {
					oldField = regExMatch.getMatch();
					oldFieldTrim = oldField.replaceAll("[,\\s]", "");
					if(fieldsMap.get(oldFieldTrim) != null) {
						newField = oldField.replace(oldFieldTrim, structure.getVelocityVarName() + "." + fieldsMap.get(oldFieldTrim).getVelocityVarName());
						sortBy = sortBy.replace(oldField, newField);
					}
				}
			}
		}
		
		return sortBy;
	}

	private static Sort buildSort(String sortBy) {
		if (!UtilMethods.isSet(sortBy)) {
			return new Sort();
		}
		String[] orderFields = sortBy.split(",");

		Map<String, List<com.dotmarketing.portlets.structure.model.Field>> fields = new HashMap<String, List<com.dotmarketing.portlets.structure.model.Field>>();
		SortField[] sorts = new SortField[orderFields.length];
		int type = 0;
		int count = 0;
		for (String localOrder : orderFields) {
			type = SortField.STRING;
			localOrder = localOrder.trim();
			boolean desc = false;
			String compField = "";

			if (localOrder.endsWith("desc")) {
				desc = true;
				compField = localOrder.substring(0, localOrder.indexOf("desc")).trim();
			} else if (localOrder.endsWith("asc")) {
				compField = localOrder.substring(0, localOrder.indexOf("asc")).trim();
			} else {
				compField = localOrder.trim();
			}
			if (UtilMethods.isSet(compField)) {
				if (compField.contains(".")) {
					List<com.dotmarketing.portlets.structure.model.Field> fds = fields.get(compField.substring(0,
							compField.indexOf(".")));
					if (fds == null) {
						fds = FieldsCache.getFieldsByStructureVariableName(compField.substring(0, compField
								.indexOf(".")));
						if (fds != null && fds.size() > 0) {
							fields.put(compField.substring(0, compField.indexOf(".")), fds);
						}
					}
					if (fds != null && fds.size() > 0) {
						for (com.dotmarketing.portlets.structure.model.Field field : fds) {
							if (field.getVelocityVarName().equals(
									compField.substring(compField.indexOf(".") + 1, compField.length()))) {
								if (field.getFieldContentlet().startsWith("float")) {
									type = SortField.FLOAT;
								} 
								if (APILocator.getFieldAPI().isAnalyze(field)) {
									compField = compField + "_raw";
								}
								break;
							}
						}
					}
				}
			}
			sorts[count] = new SortField(StringUtils.lowerCase(compField), type, desc);
			count++;
		}
		if (sorts.length > 0) {
			return new Sort(sorts);
		} else {
			return new Sort();
		}
	}
	
	public static void stopReIndex() {
		try {
			APILocator.getDistributedJournalAPI().cleanDistReindexJournal();
		} catch (DotDataException e) {
			Logger.error(LuceneUtils.class, "Error ocurred trying to stopReIndex.", e);
		}
		removeNewIndexFile();
	}

}
