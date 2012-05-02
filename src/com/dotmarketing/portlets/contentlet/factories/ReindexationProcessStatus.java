package com.dotmarketing.portlets.contentlet.factories;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.util.ConfigUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.lucene.LuceneUtils;

/** @author Hibernate CodeGenerator */
public class ReindexationProcessStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    
//    private static long contentCountToIndex = -1;
//    
//    private static int contentCountIndexed = -1;
//    
//    private static String lastIndexationStartTime = null;
//    
//    private static String lastIndexationEndTime = null;

    
    public synchronized static boolean inFullReindexation () {
    	File f = new File (ConfigUtils.getLucenePath()+ File.separator + "newindex.txt");
    	return f.exists();
    }
    
    private synchronized static File getStatFile(){
    	FileWriter fw = null;
    	File f = null;
    	try{
	    	f = new File (ConfigUtils.getLucenePath()+ File.separator + "stats.txt");
	    	if(!f.exists()){
	    		f.createNewFile();
	    	}
	    	FileReader fReader = null;
	    	String stats = null;
			try {
				fReader = new FileReader(f);
				BufferedReader reader = new BufferedReader(fReader);
				stats = reader.readLine();
			} catch (Exception e) {
				Logger.error(ReindexationProcessStatus.class, "Unable to read from stat file to get stat");
			} finally {
				if (fReader != null){
					try {
						fReader.close();
					} catch (Exception e) {
						Logger.error(ReindexationProcessStatus.class, "Error closing stat file");
					}
				}
			}
			if(!UtilMethods.isSet(stats)){
				fw = new FileWriter(f, false);
		    	fw.write("contentCountToIndexSTART-1ENDcontentCountToIndex,contentCountIndexedSTART-1ENDcontentCountIndexed,lastIndexationStartTimeSTARTunknownENDlastIndexationStartTime,lastIndexationEndTimeSTARTunknownENDlastIndexationEndTime");
			}
    	}catch(Exception e){
    		Logger.error(ReindexationProcessStatus.class, "Error trying to create index stats file",e);
	    } finally {
			if (fw != null)
				try {
					fw.close();
				} catch (IOException e) {
					Logger.error(ReindexationProcessStatus.class, "Error trying to create index stats file", e);
				}
		}
	    return f;
    }
    
    private static String getStat(String stat){
    	File f = getStatFile();
    	if(f == null){
    		Logger.error(ReindexationProcessStatus.class, "Unable to load stat file to get stat : " + stat);
    		return "";
    	}
    	FileReader fReader = null;
    	String stats = null;
		try {
			fReader = new FileReader(f);
			BufferedReader reader = new BufferedReader(fReader);
			stats = reader.readLine();
		} catch (Exception e) {
			Logger.error(ReindexationProcessStatus.class, "Unable to read from stat file to get stat : " + stat);
		} finally {
			if (fReader != null){
				try {
					fReader.close();
				} catch (Exception e) {
					Logger.error(ReindexationProcessStatus.class, "Error closing stat file");
				}
			}
		}
		if(UtilMethods.isSet(stats)){
			return stats.substring(stats.indexOf(stat + "START"), stats.indexOf("END" + stat)).replace(stat + "START", "");
		}
		return null;
    }
    
    private static void setStat(String stat, Object v){
    	File f = getStatFile();
    	if(f == null){
    		Logger.error(ReindexationProcessStatus.class, "Unable to load stat file to get stat : " + stat);
    		return;
    	}
    	FileReader fReader = null;
    	String stats = null;
		try {
			fReader = new FileReader(f);
			BufferedReader reader = new BufferedReader(fReader);
			stats = reader.readLine();
		} catch (Exception e) {
			Logger.error(ReindexationProcessStatus.class, "Unable to read from stat file to write stat : " + stat);
		} finally {
			if (fReader != null)
			try {
				fReader.close();
			} catch (Exception e) {
				Logger.error(ReindexationProcessStatus.class, "Error closing stat file");
			}
		}
		if(UtilMethods.isSet(stats)){
			String newStats = stats.replaceFirst(stat + "START.*END" + stat, stat + "START" + v.toString() + "END" + stat);
			FileWriter fw = null;
	    	try{
		    	fw = new FileWriter(f, false);
		    	fw.write(newStats);
	    	}catch(Exception e){
	    		Logger.error(ReindexationProcessStatus.class, "Error trying to create index stats file",e);
		    } finally {
				if (fw != null){
					try {
						fw.close();
					} catch (IOException e) {
						Logger.error(ReindexationProcessStatus.class, "Error trying to create index stats file", e);
					}
				}
			}
		}
    }
    
    public synchronized static long getContentCountToIndex () {
    	String s = getStat("contentCountToIndex");
    	if(UtilMethods.isSet(s) && Long.parseLong(s)< 1){
    		try {
				long i = APILocator.getContentletAPI().contentletIdentifierCount();
				setStat("contentCountToIndex", i);
				return i;
			} catch (DotDataException e) {
				Logger.error(ReindexationProcessStatus.class, e.getMessage(), e);
			}
    	}
        return Long.parseLong(s);
    }

    public synchronized static void setContentCountToIndex (long count) {
        setStat("contentCountToIndex", count);
        setStat("contentCountIndexed", 0);
    }

    public synchronized static int getLastIndexationProgress () {
    	return Integer.parseInt(getStat("contentCountIndexed"));
    }

    public synchronized static void updateIndexationProgress (int increment) {
    	int i = Integer.parseInt(getStat("contentCountIndexed"));
    	setStat("contentCountIndexed", i + 1);
    }

    public synchronized static String currentIndexPath () {
        return LuceneUtils.getCurrentLuceneDirPath();
    }

    public synchronized static String getNewIndexPath () {
    	File indexNumberStoreFile = new File (ConfigUtils.getLucenePath() + File.separator + "newindex.txt");
    	String result = "";
    	if(indexNumberStoreFile.exists()){
    		try{
	    		FileReader fReader = new FileReader (indexNumberStoreFile);
				BufferedReader reader = new BufferedReader (fReader);
				result = LuceneUtils.getBaseLuceneDir() + reader.readLine();
				reader.close();
				fReader.close();
    		}catch (Exception e) {
				Logger.error(ReindexationProcessStatus.class, e.getMessage(),e);
			}
    	}
    	return result;
    }
    
    public synchronized static Map getProcessIndexationMap () {
        Map<String, Object> theMap = new Hashtable<String, Object> ();
        theMap.put("inFullReindexation", inFullReindexation());
        theMap.put("contentCountToIndex", getContentCountToIndex());
        theMap.put("lastIndexationProgress", getLastIndexationProgress());
        theMap.put("currentIndexPath", currentIndexPath());
        theMap.put("newIndexPath", getNewIndexPath());
        if(getLastIndexationStartTime() != null){
        	theMap.put("lastIndexationStartTime", getLastIndexationStartTime());
        }
        if(getLastIndexationEndTime() != null){
        	theMap.put("lastIndexationEndTime", getLastIndexationEndTime());
        }
        return theMap;
    }

	/**
	 * @return the lastIndexationStartTime
	 */
	public static synchronized String getLastIndexationStartTime() {
		return getStat("lastIndexationStartTime");
	}

	/**
	 * @param lastIndexationStartTime the lastIndexationStartTime to set
	 */
	public static synchronized void setLastIndexationStartTime(String lastIndexationStartTime) {
		setStat("lastIndexationStartTime", lastIndexationStartTime);
	}

	/**
	 * @return the lastIndexationEndTime
	 */
	public static synchronized String getLastIndexationEndTime() {
		return getStat("lastIndexationEndTime");
	}

	/**
	 * @param lastIndexationEndTime the lastIndexationEndTime to set
	 */
	public static synchronized void setLastIndexationEndTime(String lastIndexationEndTime) {
		setStat("lastIndexationEndTime", lastIndexationEndTime);
	}
	
	public static synchronized void startNewIndex(){
		try {
			setContentCountToIndex(APILocator.getContentletAPI().contentletIdentifierCount());
		} catch (DotDataException e) {
			Logger.error(ReindexationProcessStatus.class, e.getMessage(),e);
		}
		setLastIndexationEndTime("unknown");
		setLastIndexationStartTime(UtilMethods.htmlDateToHTMLTime(new Date()));
	}
}