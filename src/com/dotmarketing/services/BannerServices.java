package com.dotmarketing.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.apache.velocity.runtime.resource.ResourceManager;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.CacheLocator;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.banners.model.Banner;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.ConfigUtils;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.velocity.DotResourceCache;

/**
 * @author will
 */
public class BannerServices {

    public static void invalidate(Banner banner) {
        Identifier parentHTMLPageId = (Identifier) InodeFactory.getParentOfClass(banner,Identifier.class); 
        //gets parent identifier -- for the html page
        HTMLPage parentHTMLPage = (HTMLPage) IdentifierFactory.getWorkingChildOfClass(parentHTMLPageId,HTMLPage.class);
        Folder parentFolder = (Folder) InodeFactory.getParentOfClass(banner,Folder.class);
        invalidate(banner, parentFolder, parentHTMLPage);
    }
	
    public static void invalidate(Banner banner, Folder parentFolder, HTMLPage parentHTMLPage) {
    	removeBannerFile(banner);
    }
    
    public static InputStream buildVelocity(Banner banner, Folder parentFolder, HTMLPage parentHTMLPage) {
    	InputStream result;
    	//  let's write this puppy out to our file
        StringBuilder sb = new StringBuilder();

		//set all properties from the banner
        sb.append("##Set Banner properties\n");
        sb.append("#set( $BannerInode ='" + banner.getInode() + "' )\n");
        sb.append("#set( $BannerTitle =\"" + UtilMethods.espaceForVelocity(banner.getTitle()) + "\" )\n");
        sb.append("#set( $BannerCaption =\"" + UtilMethods.espaceForVelocity(banner.getCaption()) + "\" )\n");
        sb.append("#set( $BannerCaption = \"#fixBreaks($BannerCaption)\")\n");
        sb.append("#set( $BannerNewWindow =\"" + banner.isNewWindow() + "\" )\n");
        sb.append("#set( $BannerLink ='" + UtilMethods.espaceForVelocity(banner.getLink()) + "' )\n");
        sb.append("#set( $BannerStartDate =\"" + UtilMethods.dateToHTMLDate(banner.getStartDate()) + "\" )\n");
        sb.append("#set( $BannerEndDate =\"" + UtilMethods.dateToHTMLDate(banner.getEndDate()) + "\" )\n");
        sb.append("#set( $Active =\"" + banner.isActive() + "\" )\n");
        sb.append("#set( $BannerBody =\"" + UtilMethods.espaceForVelocity(banner.getBody()) + "\" )\n");
        sb.append("#set( $BannerBody = \"#fixBreaks($BannerBody)\")\n");
        
        //gets the parent folder
        if (InodeUtils.isSet(parentFolder.getInode())) {
            sb.append("#set( $BannerFolderInode ='" + parentFolder.getInode() + "' )\n");
            sb.append("#set( $BannerFolder =\"" + UtilMethods.espaceForVelocity(parentFolder.getPath()) + "\" )\n");
        }

        if (InodeUtils.isSet(parentHTMLPage.getInode())) {
            sb.append("#set( $BannerPageInode ='" + parentHTMLPage.getInode() + "' )\n");
            parentFolder = (Folder) InodeFactory.getParentOfClass(parentHTMLPage,Folder.class); 
            if (InodeUtils.isSet(parentFolder.getInode())) {
            	sb.append("#set( $BannerPage =\"" + UtilMethods.espaceForVelocity(parentFolder.getPath() + parentHTMLPage.getPageUrl()) + "\" )\n");
            }
        }

        String categories = "";
        //get the banner categories to make a list
        String[] bannerCategories = banner.getCategories();
        if (bannerCategories!=null) {
	        for (int i=0;i<bannerCategories.length;i++) {
	        	categories += bannerCategories[i];
	        	if (i!=(bannerCategories.length-1)) {
	        		categories += ",";
	        	}
	        }
        }
        //sets the categories as a list on velocity
        sb.append("#set( $BannerPlacement =\"[" + categories + "]\" )\n");
        
        File image = (File) InodeFactory.getInode(banner.getImage(),File.class);
        
        sb.append("\n\n##Set Image Banner properties\n");
        sb.append("#set( $BannerImageInode ='" + image.getInode() + "' )\n");
        sb.append("#set( $BannerImageWidth =\"" + image.getWidth() + "\" )\n");
        sb.append("#set( $BannerImageHeight =\"" + image.getHeight() + "\" )\n");
        sb.append("#set( $BannerImageExtension =\"" + UtilMethods.espaceForVelocity(image.getExtension()) + "\" )\n");
        sb.append("#set( $BannerImageURI =\"" + UtilMethods.espaceForVelocity(UtilMethods.encodeURIComponent(image.getURI())) + "\" )\n");
        sb.append("#set( $BannerImageTitle =\"" + UtilMethods.espaceForVelocity(image.getTitle()) + "\" )\n");
        String velocityRootPath = Config.getStringProperty("VELOCITY_ROOT");
        if (velocityRootPath.startsWith("/WEB-INF")) {
            velocityRootPath = Config.CONTEXT.getRealPath(velocityRootPath);
        }
        velocityRootPath += java.io.File.separator;

        String folderPath = "banners" + java.io.File.separator;
        if(Config.getBooleanProperty("SHOW_VELOCITYFILES", false)){
	        try {
	        	java.io.File folderDir = new java.io.File(velocityRootPath + folderPath);
	            folderDir.mkdir();
	            java.io.BufferedOutputStream tmpOut = new java.io.BufferedOutputStream(new java.io.FileOutputStream(new java.io.File(
	            		ConfigUtils.getDynamicVelocityPath()+java.io.File.separator +folderPath + banner.getInode() + "." + Config.getStringProperty("VELOCITY_BANNER_EXTENSION"))));
	
	            //Specify a proper character encoding        
	            OutputStreamWriter out = new OutputStreamWriter(tmpOut, UtilMethods.getCharsetConfiguration());
	            
	            out.write(sb.toString());
	            
	            out.flush();
	            out.close();
	            tmpOut.close();
	            
	        } catch (Exception e) {
		        Logger.error(BannerServices.class, e.toString(), e);
	        }
        }
        try {
			result = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			result = new ByteArrayInputStream(sb.toString().getBytes());
			Logger.error(ContainerServices.class,e1.getMessage(), e1);
		}
		return result;
    }
    public static void removeBannerFile (Banner banner) {
    	String folderPath = "banners" + java.io.File.separator;
        String velocityRootPath = Config.getStringProperty("VELOCITY_ROOT");
        if (velocityRootPath.startsWith("/WEB-INF")) {
            velocityRootPath = Config.CONTEXT.getRealPath(velocityRootPath);
        }
        velocityRootPath += java.io.File.separator;
        String filePath = folderPath + banner.getInode() + "." + Config.getStringProperty("VELOCITY_BANNER_EXTENSION");
        java.io.File f  = new java.io.File(velocityRootPath + filePath);
        f.delete();
        DotResourceCache vc = CacheLocator.getVeloctyResourceCache();
        vc.remove(ResourceManager.RESOURCE_TEMPLATE + filePath );
    }
}
