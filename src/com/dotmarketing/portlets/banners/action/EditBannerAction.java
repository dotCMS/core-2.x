package com.dotmarketing.portlets.banners.action;

import java.net.URLDecoder;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.cache.BannerCache;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.banners.model.Banner;
import com.dotmarketing.portlets.banners.struts.BannerForm;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.contentlet.business.HostAPI;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.services.BannerServices;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.portlet.ActionRequestImpl;
import com.liferay.util.servlet.SessionMessages;


public class EditBannerAction extends DotPortletAction {
    public void processAction(ActionMapping mapping, ActionForm form, PortletConfig config, ActionRequest req,
            ActionResponse res) throws Exception {
        String cmd = req.getParameter(com.liferay.portal.util.Constants.CMD);
        
        //wraps request to get session object
        ActionRequestImpl reqImpl = (ActionRequestImpl) req;
        HttpServletRequest httpReq = reqImpl.getHttpServletRequest();
        
        String referer = req.getParameter("referer");
        if (referer!=null) {
        	referer = URLDecoder.decode(referer,"UTF-8");
        }
        
        //get Banner from inode
        _retrieveBanner(form, req, res);
        
        // edit a Banner
        if (com.liferay.portal.util.Constants.EDIT.equals(cmd)) {
            Logger.debug(this, "Banner:  Editing Banner");
            
            try {
                _editBanner(form, req, res);
            } catch (Exception e) {
                _handleException(e, req);
            }
        }
        
        // Save / Update a Banner
        else if (com.liferay.portal.util.Constants.ADD.equals(cmd)) {
            Logger.debug(this, "Banner:  Saving Banner");
            
            ///Validate Banner
            if (!Validator.validate(req, form, mapping)) {
                Logger.debug(this, "Banner:  Validation Banner Failed");
                setForward(req, mapping.getInput());
                
                return;
            } else {
                try {
                    _saveBanner(form, req, res);
                } catch (Exception e) {
                    _handleException(e, req);
                }
                
                _sendToReferral(req, res, referer);
                
                return;
            }
        }
        // Delete a Banner
        else if (com.liferay.portal.util.Constants.DELETE.equals(cmd)) {
            Logger.debug(this, "Banner:  Deleting Banner");
            
            try {
                _deleteBanner(form, req, res);
            } catch (Exception e) {
                _handleException(e, req);
            }
            _sendToReferral(req, res, referer);
            
            return;
        }
        
        BeanUtils.copyProperties(form, req.getAttribute(WebKeys.BANNER_EDIT));
        setForward(req, "portlet.ext.banners.edit_banner");
    }
    
    /*Private Methods*/
    
    //save Banner
    private void _saveBanner(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {
        
    	HostAPI hostAPI = APILocator.getHostAPI();
    	
    	User user = _getUser(req);
    	
        BeanUtils.copyProperties(req.getAttribute(WebKeys.BANNER_EDIT), form);
        Banner b = ( Banner) req.getAttribute(WebKeys.BANNER_EDIT);
        InodeFactory.saveInode(b);
 
        //wipe out the old categories
        java.util.List _cats = InodeFactory.getParentsOfClass(b, Category.class);
        java.util.Iterator it = _cats.iterator();
		boolean delChild = true;
        while (it.hasNext()) {
            Category cat = ( Category ) it.next();
            delChild = cat.deleteChild(b);
        }

        String placement = "";

        //add the new categories
        String[] arr = b.getCategories();
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                Category node = ( Category ) InodeFactory.getInode(arr[i], Category.class);
                node.addChild(b);
                if (node.getKey()!=null) {
                	placement += node.getKey() + ",";
                }
                else {
                	placement += node.getCategoryName() + ",";
                }
            }
        }
        
        String path = "";
        
        //gets current parent folder
        Folder currParentFolder = (Folder) InodeFactory.getParentOfClass(b,Folder.class); 
        //only remove from the current folder if the new folder is different.
        if (!currParentFolder.getInode().equalsIgnoreCase(b.getParent())) {
        	if (InodeUtils.isSet(currParentFolder.getInode())) {
        		currParentFolder.deleteChild(b);
        	}
            //adds the parent folder
            Folder parentFolder = (Folder) InodeFactory.getInode(b.getParent(),Folder.class); 
            if (InodeUtils.isSet(parentFolder.getInode())) {
            	parentFolder.addChild(b);
            	currParentFolder = parentFolder; 
            }
        }
        //gets folder path 
        if (InodeUtils.isSet(currParentFolder.getInode())) {
            Host host = hostAPI.findParentHost(currParentFolder, user, false);
        	path = host.getHostname() + currParentFolder.getPath();
        }

        //gets parent identifier -- for the html page
        Identifier parentHTMLPageId = (Identifier) IdentifierFactory.getIdentifierByInode(b); 

        HTMLPage parentHTMLPage = (HTMLPage) InodeFactory.getInode(b.getHtmlpage(),HTMLPage.class);
        Identifier newParentHTMLPageId = IdentifierFactory.getIdentifierByInode(parentHTMLPage);
        if (!parentHTMLPageId.getInode().equalsIgnoreCase(newParentHTMLPageId.getInode())) {
            //remove from the current page identifier
            if (InodeUtils.isSet(parentHTMLPageId.getInode())) {
            	parentHTMLPageId.deleteChild(b);
            }
            if (InodeUtils.isSet(newParentHTMLPageId.getInode())) {
            	newParentHTMLPageId.addChild(b);
            	parentHTMLPageId = newParentHTMLPageId; 
            }
        }
        //gets page path with folder included
        if (InodeUtils.isSet(parentHTMLPageId.getInode())) {
            parentHTMLPage = (HTMLPage) IdentifierFactory.getWorkingChildOfClass(parentHTMLPageId,HTMLPage.class);
            Folder parentFolder = (Folder) InodeFactory.getParentOfClass(parentHTMLPage,Folder.class); 
            Host host = hostAPI.findParentHost(parentFolder, user, false);
            path = host.getHostname() + parentFolder.getPath() + parentHTMLPage.getPageUrl();
        }
        	
        //sets placement and path for easier searches...
        String previousPath = b.getPath();
        String previousPlacement  = b.getPlacement();

        b.setPlacement(placement);
        b.setPath(path);
        InodeFactory.saveInode(b);
        
        //writes file with banners to file
        BannerServices.invalidate(b, currParentFolder, parentHTMLPage);

        //updates cache
        BannerCache.removeFromBannerCache(previousPath, previousPlacement, b);
        BannerCache.updateBannerCache(previousPath, previousPlacement,path,placement,b);

        //For messages to be displayed on messages page
     	SessionMessages.add(req, "message", "message.banner.save");    
    
    }
    
    //delete Banner
    private void _deleteBanner(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {
        Banner b = ( Banner ) req.getAttribute(WebKeys.BANNER_EDIT);
        InodeFactory.deleteInode(b);
        //For messages to be displayed on messages page
     	SessionMessages.add(req, "message", "message.banner.delete");    
    
    }
    
    //view Banner for Action request
    private void _retrieveBanner(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {

    	Banner banner = (Banner) InodeFactory.getInode(req.getParameter("inode"), Banner.class);
        req.setAttribute(WebKeys.BANNER_EDIT, banner);
    }
    //view Banner for Action request
    private void _editBanner(ActionForm form, ActionRequest req, ActionResponse res)
    throws Exception {

    	Banner banner = (Banner) req.getAttribute(WebKeys.BANNER_EDIT);
        BannerForm bf = (BannerForm) form;
        
        //gets the parent folder
        Folder parentFolder = (Folder) InodeFactory.getParentOfClass(banner,Folder.class); 
        //gets parent identifier -- for the html page
        Identifier parentHTMLPageId = (Identifier) IdentifierFactory.getIdentifierByInode(banner); 
        HTMLPage parentHTMLPage = (HTMLPage) IdentifierFactory.getWorkingChildOfClass(parentHTMLPageId,HTMLPage.class);

        if (InodeUtils.isSet(parentFolder.getInode())) {
        	bf.setParent(parentFolder.getInode());
        	banner.setParent(parentFolder.getInode());
        	bf.setSelectedparent(parentFolder.getPath());
        	bf.setSelectedparentPath(parentFolder.getPath());
        }
        if (InodeUtils.isSet(parentHTMLPage.getInode())) {
        	banner.setHtmlpage(parentHTMLPage.getInode());
        	bf.setHtmlpage(parentHTMLPage.getInode());
        	bf.setSelectedhtmlpage(parentHTMLPage.getPageUrl());
        }
        File image = (File) InodeFactory.getInode(banner.getImage(),File.class);
        bf.setSelectedimage(image.getFileName());
        bf.setImageExtension(UtilMethods.getFileExtension(image.getFileName()));
        
        req.setAttribute(WebKeys.BANNER_EDIT, banner);
    }
}
