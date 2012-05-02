package com.dotmarketing.portlets.entities.action;

import java.util.HashMap;
import java.util.List;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.entities.factories.EntityFactory;
import com.dotmarketing.portlets.entities.model.Entity;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.struts.PortletAction;


public class ViewEntitiesAction extends PortletAction {
    public ActionForward render(ActionMapping mapping, ActionForm form, PortletConfig config, RenderRequest req,
        RenderResponse res) throws Exception {
        
        //gets entities and set them as attributes on the request

        if (req.getWindowState().equals(WindowState.NORMAL)) 
        {
        	List<Entity> l = (List<Entity>) InodeFactory.getInodesOfClass(Entity.class);
        	//Get the Categories of the entity
        	HashMap<String, List<Category>> entityCategories = new HashMap<String, List<Category>>();
        	for(Entity entity : l)
        	{
        		List<Category> categories = EntityFactory.getEntityCategories(entity);
        		String entityInode = entity.getInode();
        		entityCategories.put(entityInode, categories);
        	}
        	
            req.setAttribute(WebKeys.ENTITY_VIEW, l);
            req.setAttribute(WebKeys.CATEGORY_VIEW,entityCategories);
            return mapping.findForward("portlet.ext.entities.view");
        } 
        else 
        {
        	List<Entity> l = (List<Entity>) InodeFactory.getInodesOfClass(Entity.class,"lower(entity_name)");
        	HashMap<String, List<Category>> entityCategories = new HashMap<String, List<Category>>();
        	for(Entity entity : l)
        	{        		
        		List<Category> categories = EntityFactory.getEntityCategories(entity);
        		String entityInode = entity.getInode();
        		entityCategories.put(entityInode, categories);
        	}
            req.setAttribute(WebKeys.ENTITY_VIEW, l);
            req.setAttribute(WebKeys.CATEGORY_VIEW,entityCategories);
            return mapping.findForward("portlet.ext.entities.view_entities");
        }
    }
}
