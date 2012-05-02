package com.dotmarketing.servlets;

import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.MultiTree;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.exception.DotHibernateException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.factories.MultiTreeFactory;
import com.dotmarketing.portlets.containers.model.Container;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.htmlpages.factories.HTMLPageFactory;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.templates.model.Template;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;

public class RecreateTreeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		///Do WORKING ones first!!!
		java.util.List list = HTMLPageFactory.getWorkingHTMLPages();

		Iterator i = list.iterator();
		while (i.hasNext()) {
			HTMLPage htmlPage = (HTMLPage) i.next();

			//gets working (not published) template parent for this html page
			Template templateParent = HTMLPageFactory.getHTMLPageTemplate(htmlPage, true);
			
			if (InodeUtils.isSet(templateParent.getInode())) {

                Logger.debug(this, "*****Recreate Tree Servlet -- Got Working Template=" + templateParent.getInode());

				//gets all container children 
				java.util.List identifiers = InodeFactory.getChildrenClass(templateParent, Identifier.class);
				java.util.Iterator identifiersIter = identifiers.iterator();

				while (identifiersIter.hasNext()) {

					Identifier identifier = (Identifier) identifiersIter.next();
	                Logger.debug(this, "*****Recreate Tree Servlet -- Container Identifier =" + identifier.getInode());
					Container container = (Container) IdentifierFactory.getWorkingChildOfClass(identifier, Container.class);
	                Logger.debug(this, "*****Recreate Tree Servlet -- Container Working Inode =" + container.getInode());

					String condition = "working=" + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and deleted = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue(); 
					java.util.List contentlets = InodeFactory.getChildrenClassByCondition(htmlPage.getInode(),container.getInode(),Contentlet.class,condition);
					
					//gets all not live contentlet children
					java.util.Iterator contentletsIter = contentlets.iterator();
					while (contentletsIter.hasNext()) {
						//publishes each one
						Contentlet contentlet = (Contentlet) contentletsIter.next();
						Identifier iden = new Identifier();
						try{
							iden = IdentifierCache.getIdentifierFromIdentifierCache(contentlet);
						}catch (DotHibernateException he) {
							Logger.error(this,"Unable to load identifier",he);
						}
						Identifier idenContainer = IdentifierFactory.getIdentifierByWebAsset(container);
						Identifier idenHtmlPage = IdentifierFactory.getIdentifierByWebAsset(htmlPage);
		                Logger.debug(this, "*****Recreate Tree Servlet -- Contentlet Working Inode=" + contentlet.getInode());
						
						//if it's live then i need to create one more tree record for the working one
						MultiTree childTree = new MultiTree(idenHtmlPage.getInode(),idenContainer.getInode(),iden.getInode());
						MultiTreeFactory.saveMultiTree(childTree);

						
					}
				}

			}
		}

	
		///Do LIVE ones after working ones!!!
		list = HTMLPageFactory.getLiveHTMLPages();

		i = list.iterator();
		while (i.hasNext()) {
			HTMLPage htmlPage = (HTMLPage) i.next();

			//gets working (not published) template parent for this html page
			Template templateParent = HTMLPageFactory.getHTMLPageTemplate(htmlPage, false);
			
			if (InodeUtils.isSet(templateParent.getInode())) {

                Logger.debug(this, "*****Recreate Tree Servlet -- Got Live Template=" + templateParent.getInode());

				//gets all container children 
				java.util.List identifiers = InodeFactory.getChildrenClass(templateParent, Identifier.class);
				java.util.Iterator identifiersIter = identifiers.iterator();

				while (identifiersIter.hasNext()) {

					Identifier identifier = (Identifier) identifiersIter.next();
	                Logger.debug(this, "*****Recreate Tree Servlet -- Container Identifier =" + identifier.getInode());
					Container container = (Container) IdentifierFactory.getLiveChildOfClass(identifier, Container.class);
	                Logger.debug(this, "*****Recreate Tree Servlet -- Container Live Inode =" + container.getInode());

					String condition = "live=" + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " and deleted = " + com.dotmarketing.db.DbConnectionFactory.getDBFalse(); 
					java.util.List contentlets = InodeFactory.getChildrenClassByCondition(htmlPage.getInode(),container.getInode(),Contentlet.class,condition);
					
					//gets all not live contentlet children
					java.util.Iterator contentletsIter = contentlets.iterator();
					while (contentletsIter.hasNext()) {
						//publishes each one
						Contentlet contentlet = (Contentlet) contentletsIter.next();
						Identifier iden = new Identifier();
						try{
							iden = IdentifierCache.getIdentifierFromIdentifierCache(contentlet);
						}catch (DotHibernateException he) {
								Logger.error(this,"Unable to load identifier",he);
						}
		                Logger.debug(this, "*****Recreate Tree Servlet -- Contentlet Live Inode=" + contentlet.getInode());
						
						//if it's live then i need to create one more tree record for the working one
						MultiTree childTree = new MultiTree(htmlPage.getInode(),container.getInode(),iden.getInode());
						MultiTreeFactory.saveMultiTree(childTree);

						
					}
				}

			}
		}
	
	
	}
}
