package com.dotmarketing.portlets.order_manager.struts;

import java.util.List;

import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.util.Logger;
import com.liferay.portal.model.User;

public class UsersForm extends ValidatorForm  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String filter;
	private int page = 1;
	private int pageSize = 25;
	private List<User> users;

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}	
	
	public int getTotalPages()
	{
		long count = 0;
		try {
			count = APILocator.getUserAPI().getCountUsersByNameOrEmail(filter);
		} catch (DotDataException e) {
			Logger.error(this, e.getMessage(), e);
		}
		
		int totalPages = (int) Math.ceil(((double)count) / ((long) pageSize));
		totalPages = (totalPages == 0 ? 1 : totalPages);
		return totalPages;
	}
}
