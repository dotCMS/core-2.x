package com.dotmarketing.portlets.workflows.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.dotmarketing.beans.Inode;



public class WorkflowComment extends Inode 
{
	
	private static final long serialVersionUID = 1L;
	
    Date creationDate;
    String postedBy;
    String comment;

    public WorkflowComment () {
        setType("workflow_comment");
    }
    
    public Date getCreationDate() {
        return creationDate;
    }


    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


    public String getComment() {
        return comment;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }


    public String getPostedBy() {
        return postedBy;
    }


    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }


    public Map getMap () {
        Map oMap = new HashMap ();
        oMap.put("comment", this.getComment());
        oMap.put("creationDate", this.getCreationDate());
        oMap.put("postedBy", this.getPostedBy());
        oMap.put("inode", this.getInode());
        return oMap;
    }
}
