package com.dotmarketing.portlets.contentratings.factories;

import java.util.List;

import com.dotmarketing.beans.Rating;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.util.Logger;

public class ContentRatingsFactory {
    public static List<Structure> getContentRatingsStructures() {
        String query = "select {structure.*} from structure, field, inode as structure_1_ where field.field_name='Average Rating' and structure.inode=field.structure_inode and structure.inode=structure_1_.inode";
        DotHibernate dh = new DotHibernate (Structure.class);
        dh.setSQLQuery(query);
        List<Structure> list = dh.list();
        return list;
    }
    
    public static List<Rating> getContentRatingsByStructure(String inode) {
    	List<Rating> result = null;
    	
    	try {
    		String query = "select {content_rating.*} from content_rating, contentlet,inode where content_rating.identifier=inode.identifier and inode.inode =contentlet.inode and structure_inode=?";
    		DotHibernate dh = new DotHibernate (Rating.class);
    		dh.setSQLQuery(query);
    		dh.setParam(inode);
    		result = dh.list();
    	} catch (Exception e) {
    		Logger.error(ContentRatingsFactory.class, "", e);
    		return null;
    	}
    	
    	return result;
    }
}