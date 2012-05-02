package com.dotmarketing.cms.product.struts;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.product.model.Product;

public class ProductsForm extends ActionForm
{
	private static final long serialVersionUID = 1L;
	//Fields to search
	private String categoryInode;
	private String categoryName;
	private String orderBy;
	private int page;
	private int pageSize;
	private String direction;
	private String filter;
	private String inode;
	
	//Fields with return data
	private List<Product> listProducts;
	private List<Category> listTypeProducts;
	
	public String getCategoryInode() {
		return categoryInode;
	}

	public void setCategoryInode(String categoryInode) {
		this.categoryInode = categoryInode;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public List<Product> getListProducts() {
		return listProducts;
	}

	public void setListProducts(List<Product> listProducts) {
		this.listProducts = listProducts;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
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

	public List<Category> getListTypeProducts() {
		return listTypeProducts;
	}

	public void setListTypeProducts(List<Category> listTypeProducts) {
		this.listTypeProducts = listTypeProducts;
	}

	public String getInode() {
		return inode;
	}

	public void setInode(String inode) {
		this.inode = inode;
	}

	/**
	 * @return Returns the categoryName.
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName The categoryName to set.
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}	
	
	
	
}
