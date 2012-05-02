package com.dotmarketing.portlets.workflows.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.dotmarketing.beans.Inode;



public class WorkflowHistory extends Inode 
{
	
	private static final long serialVersionUID = 1L;
	
    Date creationDate;
    String madeBy;
    String changeDescription;
    
    public WorkflowHistory () {
        setType("workflow_history");
    }
    
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public String getMadeBy() {
        return madeBy;
    }

    public void setMadeBy(String madeBy) {
        this.madeBy = madeBy;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


    public Map getMap () {
        Map oMap = new HashMap ();
        oMap.put("inode", this.getInode());
        oMap.put("creationDate", this.getCreationDate());
        oMap.put("madeBy", this.getMadeBy());
        oMap.put("changeDescription", this.getChangeDescription());
        return oMap;
    }
}
