package com.dotmarketing.portlets.entities.action;

import static com.dotmarketing.business.PermissionAPI.PERMISSION_READ;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portal.struts.DotPortletAction;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.entities.model.Entity;
import com.dotmarketing.portlets.entities.struts.EntityForm;
import com.dotmarketing.portlets.structure.factories.StructureFactory;
import com.dotmarketing.portlets.structure.model.Field;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.Validator;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;
import com.liferay.util.servlet.SessionMessages;

public class EditEntityAction extends DotPortletAction {
    
    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
            RenderResponse res) throws Exception {
        
        _editEntity(form, req, res);
        BeanUtils.copyProperties(form,req.getAttribute(WebKeys.ENTITY_EDIT));
        return mapping.findForward("portlet.ext.entities.edit_entity");
    }
    
    public void processAction(ActionMapping mapping, ActionForm form, PortletConfig config, ActionRequest req,
            ActionResponse res) throws Exception {
        String cmd = req.getParameter(com.liferay.portal.util.Constants.CMD);
        
        _editEntity(form, req, res);
        
        String referer = req.getParameter("referer");
        
        // Saving Entity
        if (com.liferay.portal.util.Constants.SAVE.equals(cmd)) {
            
            Logger.debug(this, "Entity:  Saving Entity");
            
            if (!Validator.validate(req, form, mapping)) {
                Logger.debug(this, "Entity Form Validation Failed Entity");
                setForward(req, "portlet.ext.entities.edit_entity");
                return;
            } else {
                try {
                    _saveEntity(form,req,res);
                }
                catch (Exception e) {
                    _handleException(e,req);
                }
                _sendToReferral(req,res,referer);
            }
        }
        // Deleting Entity
        if (com.liferay.portal.util.Constants.DELETE.equals(cmd)) {
            Logger.debug(this, "Entity:  Deleting Entity");
            try {
                _deleteEntity(form, req, res); 
            }
            catch (Exception e) {
                _handleException(e,req);
            }
            _sendToReferral(req,res,referer);
        }
    }
    /* Private Methods */
    
    private void _editEntity(ActionForm form, ActionRequest req, ActionResponse res) throws Exception {
        
        Entity e= (Entity) InodeFactory.getInode(req.getParameter("inode"),Entity.class);
        Logger.debug(this, "EditEntityAction: entityInode=" + e.getInode());
        req.setAttribute(WebKeys.ENTITY_EDIT, e);
        
    }
    
    private void _editEntity(ActionForm form, RenderRequest req, RenderResponse res) throws Exception {
        
        Entity e= (Entity) InodeFactory.getInode(req.getParameter("inode"),Entity.class);
        Logger.debug(this, "EditEntityAction: entityInode=" + e.getInode());
        req.setAttribute(WebKeys.ENTITY_EDIT, e);
        
    }
    
    private void _deleteEntity(ActionForm form, ActionRequest req, ActionResponse res) throws Exception {
        
        Entity e = ( Entity ) req.getAttribute(WebKeys.ENTITY_EDIT);
        InodeFactory.deleteInode(e);
        
        //For messages to be displayed on messages page
        SessionMessages.add(req, "message", "message.entity.delete");
        
    }
    
    private void _saveEntity(ActionForm form, ActionRequest req, ActionResponse res) throws Exception {
    	
    	//Check if the related structure already have a category field
        String entityName = ((EntityForm) form).getEntityName();
        Structure structure = StructureFactory.getStructureByType(entityName);
        if(InodeUtils.isSet(structure.getInode()))
        {
        	List<Field> fields = structure.getFields();
        	for(Field field : fields)
        	{
        		if(field.getFieldType().equals(Field.FieldType.CATEGORY.toString()))
        		{
        			SessionMessages.add(req, "error", "message.structure.duplicate.categoryfield");
        			return;
        		}
        	}
        }
    	
    	User user = _getUser(req);
    	
    	PermissionAPI perAPI = APILocator.getPermissionAPI();
    	CategoryAPI catAPI = APILocator.getCategoryAPI();
    	
        BeanUtils.copyProperties(req.getAttribute(WebKeys.ENTITY_EDIT),form);
        Entity e = (Entity) req.getAttribute(WebKeys.ENTITY_EDIT);
        Logger.debug(this, "UpdateEntityAction: Inode 1=" + e.getInode());
        InodeFactory.saveInode(e);
        Logger.debug(this, "UpdateEntityAction: Inode 2=" + e.getInode());
        
        //wipe out the old categories
        List<Category> _cats = new ArrayList<Category>(catAPI.getParents(e, user, false));
        for (Category cat : _cats) {
            boolean canUse = perAPI.doesUserHavePermission(cat, PERMISSION_READ, user);
            if(canUse){
            	catAPI.removeParent(e, cat, user, false);
            }
        }
        
        //add the new categories
        String[] arr = e.getCategories();
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                Category node = ( Category ) InodeFactory.getInode(arr[i], Category.class);
            	catAPI.addParent(e, node, user, false);
            }
        }
        //For messages to be displayed on messages page
        SessionMessages.add(req, "message", "message.entity.save");
    }
    
}
