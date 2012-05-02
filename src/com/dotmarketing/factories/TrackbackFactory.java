package com.dotmarketing.factories;

import java.util.ArrayList;
import java.util.List;

import com.dotmarketing.beans.Trackback;
import com.dotmarketing.db.DotHibernate;

/**
 * This Factory manage the search and saveing of trackback records
 * @author Oswaldo Gallango
 * @version 1.0
 * @since 1.5
 */
public class TrackbackFactory {

	/**
	 * Get Trackbak by id
	 * @param id
	 * @return Trackback
	 */
	public static Trackback getTrackBack(long id) {
		if(id ==0){
			return new Trackback();
		}
		DotHibernate dh = new DotHibernate();
		dh.setClass(Trackback.class);
		Trackback tb = (Trackback) dh.load(id);
		return tb;
	}
	
	/**
	 * Get a list of trackback by asset inode
	 * @param assetIdentifier
	 * @return List<Trackback>
	 */
	@SuppressWarnings("unchecked")
	public static List<Trackback> getTrackbacksByAssetId(String assetIdentifier) {
		if(assetIdentifier == null ){
			return new ArrayList<Trackback>();
		}
		DotHibernate dh = new DotHibernate();
		dh.setClass(Trackback.class);
		dh.setQuery("from trackback in class com.dotmarketing.beans.Trackback where asset_identifier = '" + assetIdentifier + "'");
		List<Trackback> tb = (List<Trackback>) dh.list();
		return tb;
	}
	
	/**
	 * Get a list of trackback by url
	 * @param url
	 * @return List<Trackback>
	 */
	@SuppressWarnings("unchecked")
	public static List<Trackback> getTrackbakByURL(String url) {
		
		DotHibernate dh = new DotHibernate();
		dh.setClass(Trackback.class);
		dh.setQuery("from trackback in class com.dotmarketing.beans.Trackback where url like '%"+ url+"%'");
		List<Trackback> tb = (List<Trackback>) dh.list();
		return tb;
	}
	
	/**
	 * Save or update the trackback object 
	 * @param tb
	 */
	public static void save(Trackback tb){
		DotHibernate.saveOrUpdate(tb);
	}
	
	
}
