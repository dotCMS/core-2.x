package com.dotmarketing.viewtools;

import java.util.List;

import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.portlets.jobs.factories.JobsFactory;
import com.dotmarketing.portlets.jobs.factories.ResumeFactory;
import com.dotmarketing.portlets.jobs.factories.SearchfirmFactory;
import com.dotmarketing.portlets.jobs.model.Jobs;
import com.dotmarketing.portlets.jobs.model.Resume;
import com.dotmarketing.portlets.jobs.model.Searchfirm;
import com.dotmarketing.util.UtilMethods;

public class CareersWebAPI implements ViewTool {
	


    public void init(Object obj) {


    }

    public List listJobs(String location, String s, int pageNumber) {
		java.util.List jobList = null;

		int limit = com.dotmarketing.util.Config.getIntProperty("PER_PAGE");
		int offset = (pageNumber - 1) * limit;

		if ((UtilMethods.isSet(location)) && 
			(UtilMethods.isSet(s))){
			jobList = JobsFactory.getJobsBySearchAndLocation(s, location, limit, offset);
		}
		else if (UtilMethods.isSet(location)) {
			jobList = JobsFactory.getJobsByLocation(location, limit, offset);
		}
		else if (UtilMethods.isSet(s)) {
			jobList = JobsFactory.getJobsBySearch(s, limit, offset);
		}
		else{
			jobList = JobsFactory.getActiveJobs(limit, offset);
		}
		return jobList;
    }

    public Jobs viewJob(String inode) {
		Jobs job = JobsFactory.getJob(inode);
		return job;
    }
    
    public Resume viewResume(String inode) {
		Resume resume = ResumeFactory.getResume(inode);
		return resume;
    }

    public Searchfirm viewSearchFirm(String inode) {
    	Searchfirm searchFirm = SearchfirmFactory.getSearchfirm(inode);
		return searchFirm;
    }

    public List listResumes(String location, String s, int pageNumber) {
    	
		int limit = com.dotmarketing.util.Config.getIntProperty("PER_PAGE");
		int offset = (pageNumber - 1) * limit;
		java.util.List resumesList = null;

		if ((UtilMethods.isSet(location)) && 
			(UtilMethods.isSet(s))){
			resumesList = ResumeFactory.getResumesBySearchAndLocation(s, location, limit, offset);
		}
		else if (UtilMethods.isSet(location)) {
			resumesList = ResumeFactory.getResumesByLocation(location, limit, offset);
		}
		else if (UtilMethods.isSet(s)) {
			resumesList = ResumeFactory.getResumesBySearch(s, limit, offset);
		}
		else{
			resumesList = ResumeFactory.getActiveResumes(limit, offset);
		}

    	return resumesList;
    }

    public List listSearchfirms(String location, String s, int pageNumber) {
    	
		int limit = com.dotmarketing.util.Config.getIntProperty("PER_PAGE");
		int offset = (pageNumber - 1) * limit;
		java.util.List firmList = null;

		if ((UtilMethods.isSet(location)) && 
			(UtilMethods.isSet(s))){
			firmList = SearchfirmFactory.getSearchFirmsBySearchAndLocation(s, location, limit, offset);
		}
		else if (UtilMethods.isSet(location)) {
			firmList = SearchfirmFactory.getSearchFirmsByLocation(location, limit, offset);
		}
		else if (UtilMethods.isSet(s)) {
			firmList = SearchfirmFactory.getSearchFirmsBySearch(s, limit, offset);
		}
		else{
			firmList = SearchfirmFactory.getActiveFirmlist(limit, offset);
		}
		return firmList;
    }
	public List listPremiumJobs() {
		return JobsFactory.getPremiumJobs();
	}


}