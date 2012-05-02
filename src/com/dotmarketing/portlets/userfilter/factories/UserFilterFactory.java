package com.dotmarketing.portlets.userfilter.factories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.userfilter.model.UserFilter;
import com.dotmarketing.portlets.usermanager.factories.UserManagerListBuilderFactory;
import com.dotmarketing.portlets.usermanager.struts.UserManagerListSearchForm;
import com.liferay.portal.model.User;

public class UserFilterFactory {

	public static java.util.List getAllUserFilter() {
		DotHibernate dh = new DotHibernate(UserFilter.class);
		dh.setQuery("from inode in class com.dotmarketing.portlets.userfilter.model.UserFilter order by title");
		return dh.list();
	}
	
	public static java.util.List getAllUserFilterByUser(User user) {
		DotHibernate dh = new DotHibernate(UserFilter.class);
		dh.setSQLQuery("select user_filter.* from user_filter user_filter, inode inode where user_filter.inode=inode.inode and inode.owner=? order by title");
		dh.setParam(user.getUserId());
		return dh.list();
	}
	
	public static java.util.List getUserFilterByTitle(String title) {
		DotHibernate dh = new DotHibernate(UserFilter.class);
		dh.setQuery("from inode in class com.dotmarketing.portlets.userfilter.model.UserFilter where lower(title) like ? order by title");
		dh.setParam("%" + title.toLowerCase() + "%");
		return dh.list();
	}
	
	public static java.util.List getUserFilterByTitleAndUser(String title, User user) {
		DotHibernate dh = new DotHibernate(UserFilter.class);
		dh.setSQLQuery("select user_filter.* from user_filter user_filter, inode inode where lower(user_filter.title) like ? and user_filter.inode=inode.inode and inode.owner=? order by title");
		dh.setParam("%" + title.toLowerCase() + "%");
		dh.setParam(user.getUserId());
		return dh.list();
	}
	
	public static UserFilter getUserFilter(String inode) {
		return (UserFilter) InodeFactory.getInode(inode, UserFilter.class);
	}

	public static List<UserProxy> getUserProxiesFromFilter(UserFilter uf) throws Exception {
		UserManagerListSearchForm userForm = new UserManagerListSearchForm();
		BeanUtils.copyProperties(userForm, uf);
		List<UserProxy> userProxies = new ArrayList<UserProxy>();
		List allUsers = UserManagerListBuilderFactory.doSearch(userForm);
		Iterator it = allUsers.iterator();
		for (int i = 0; it.hasNext(); i++) {
			User user = null;
			String userId = (String) ((Map)it.next()).get("userid");
			user = APILocator.getUserAPI().loadUserById(userId,APILocator.getUserAPI().getSystemUser(),false);
			UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user,APILocator.getUserAPI().getSystemUser(), false);
			userProxies.add(userProxy);
		}
		return userProxies;
	}
}
