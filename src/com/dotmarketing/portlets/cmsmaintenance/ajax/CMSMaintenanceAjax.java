package com.dotmarketing.portlets.cmsmaintenance.ajax;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.cache.StructureCache;
import com.dotmarketing.cms.factories.PublicCompanyFactory;
import com.dotmarketing.common.business.journal.IndexJournal;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.fixtask.FixTasksExecutor;
import com.dotmarketing.portlets.cmsmaintenance.factories.CMSMaintenanceFactory;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.factories.ReindexationProcessStatus;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.structure.model.ContentletRelationships;
import com.dotmarketing.portlets.structure.model.Relationship;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.structure.model.ContentletRelationships.ContentletRelationshipRecords;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.MaintenanceUtil;
import com.dotmarketing.util.lucene.LuceneUtils;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.ejb.UserLocalManagerUtil;
import com.liferay.portal.language.LanguageException;
import com.liferay.portal.language.LanguageUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;

public class CMSMaintenanceAjax {

    public Map getReindexationProgress() {
        return ReindexationProcessStatus.getProcessIndexationMap();
    }    
   private  FixTasksExecutor fixtask=FixTasksExecutor.getInstance();
    
    public List <Map> fixAssetsInconsistencies() throws Exception
    {
    	    	
        JobExecutionContext arg0=null;
        fixtask.execute(arg0);
		List result=fixtask.getTasksresults();
		 if(result.size()==0){
			 result=null;
		 }

        return result;
       
    }
    
    public List <Map> getFixAssetsProgress() throws Exception{
    	List result=fixtask.getTasksresults();
		 if(result.size()==0){
			 result=null;}
        return result;
    }
    

    
	public String[] deleteContentletsFromIdList(String List, String userId) throws PortalException, SystemException, DotDataException,DotSecurityException {

		ContentletAPI conAPI = APILocator.getContentletAPI();
		String[] inodes = List.split(",");
		Integer contdeleted = 0;
		String contnotfound = "";
		String conthasreqrel = "";
		String conthasnoperm = "";

		User user = UserLocalManagerUtil.getUserById(userId);
		for (int i = 0; i < inodes.length; i++) {
			inodes[i] = inodes[i].trim();
		}

		List<Contentlet> contentlets = new ArrayList<Contentlet>();

		for (String inode : inodes) {
			if (!inode.trim().equals("")) {
				contentlets.addAll(conAPI.getSiblings(inode));
			}
		}

		for (Contentlet contentlet : contentlets) {
			conAPI.delete(contentlet, APILocator.getUserAPI().getSystemUser(), true, true);
			contdeleted++;
		}
		
		String[] results = { contdeleted.toString(), contnotfound, conthasreqrel,conthasnoperm };

		return results;
	}
	
    public int removeOldVersions(String date) throws ParseException, SQLException, DotDataException {
//        if(!CMSMaintenanceFactory.findAssetsInconsistencies()) {
    		try {
    			Logger.debug(this, "Starting to fix assets before deleting older ones");
			    CMSMaintenanceFactory.fixAssetsInconsistencies();
			} catch (Exception e) {
				Logger.error(this,"There was a problem fixing asset inconsistencies",e);
			}
			Logger.debug(this, "Done fixing assets about to start deleting older ones");
        	Date assetsOlderThan = new SimpleDateFormat("MM/dd/yyyy").parse(date);
        	MaintenanceUtil.deleteAssetsWithNoInode();
        	return CMSMaintenanceFactory.deleteOldAssetVersions(assetsOlderThan);
//        } else {
//        	return -2;
//        }
    }
    
    public Map stopReindexation() {
    	LuceneUtils.stopReIndex();
    	return ReindexationProcessStatus.getProcessIndexationMap();
    }
    
    public String cleanReindexStructure(String inode) { 
    	Structure structure = StructureCache.getStructureByInode(inode);
    	String structureName = structure.getName();
    	LuceneUtils.removeDocByStructureToCurrentIndex(structureName);
    	APILocator.getContentletAPI().refresh(structure);
    	
    	Company d = PublicCompanyFactory.getDefaultCompany();    	
    	try {
			return LanguageUtil.get(d.getCompanyId(),d.getLocale(), "message.cmsmaintenance.cache.indexrebuilt");
		} catch (LanguageException e) {			
			return "message.cmsmaintenance.cache.indexrebuilt";
		}
    }
    
    public List<Map<String,Object>> getReindexJournalData(){
    	
    	List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();    	
    	try {
			List<IndexJournal> indexJournaldata = APILocator.getDistributedJournalAPI().viewReindexJournalData();
			for (IndexJournal obj:indexJournaldata){
				Map<String,Object> dataMap = new HashMap<String,Object>();
				dataMap.put("serverId", obj.getServerId());
				dataMap.put("count", String.valueOf(obj.getCount()));
				dataMap.put("priority", String.valueOf(obj.getPriority()));
				result.add(dataMap);
			}			
		} catch (DotDataException e) {
			Logger.error(this, e.getMessage());			
		}		
		return result;
    }

}
