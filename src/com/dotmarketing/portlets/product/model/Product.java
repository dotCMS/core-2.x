package com.dotmarketing.portlets.product.model;

import java.util.List;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.product.factories.ProductFormatFactory;
import com.dotmarketing.util.WebKeys;

/**
 * 
 * @author David
 *
 */
public class Product extends Inode {
	
	private static final long serialVersionUID = 1L;
	private transient CategoryAPI catAPI = APILocator.getCategoryAPI();
    private String title; //persistent
    private String shortDescription; //persistent
    private String longDescription; //persistent
    private boolean reqShipping; //persistent
    private boolean featured; //persistent
    private int sortOrder; //persistent
    private String comments; //persistent
    private boolean showOnWeb;//persistent
    public Product(String title, String shortDescription, String longDescription, boolean reqShipping, boolean featured) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.reqShipping = reqShipping;
        this.featured = featured;
    }

    public Product() {
    	setType("ecom_product");
    }

    public Product(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getShortDescription() {
        return this.shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    public String getLongDescription() {
        return this.longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }
    public boolean getReqShipping() {
        return this.reqShipping;
    }

    public void setReqShipping(boolean reqShipping) {
        this.reqShipping = reqShipping;
    }
    public boolean getFeatured() {
        return this.featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }
    
    public List<ProductFormat> getFormats()
    {
    	return ProductFormatFactory.getAllFormatsByProduct(this);
    }

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	public List<Product> getRelatedProducts()
	{
		List<Product> products = InodeFactory.getChildrenClassByRelationType(this,Product.class,WebKeys.PRODUCT_RELATED);
		return products;
	}
	
	public List<Category> getProductCategories() throws DotDataException, DotSecurityException{
		List<Category> categories = catAPI.getChildren(this, WebKeys.PRODUCT_PRODUCTS_TYPE, false, null, APILocator.getUserAPI().getSystemUser(), false);
		return categories;
	}

	public Category getProductType() throws DotDataException, DotSecurityException
    {
    	Category category;
		List<Category> children = catAPI.getChildren(this, WebKeys.PRODUCT_PRODUCTS_TYPE, false, null, APILocator.getUserAPI().getSystemUser(), false);
		category = children.size() > 0 ? children.get(0): new Category(); 
//    	Category category = (Category) InodeFactory.getParentOfClassByRelationType(this,Category.class,WebKeys.PRODUCT_PRODUCTS_TYPE);
    	return category;
    }
	
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public String getSmallImageInode()
	{
		Identifier identifier = (Identifier) InodeFactory.getChildOfClassByRelationType(this,Identifier.class,WebKeys.PRODUCT_SMALL_IMAGE);
		File image = (File) IdentifierFactory.getLiveChildOfClass(identifier,File.class);
		return image.getInode();
	}
	
	public String getMediumImageInode()
	{
		Identifier identifier = (Identifier) InodeFactory.getChildOfClassByRelationType(this,Identifier.class,WebKeys.PRODUCT_MEDIUM_IMAGE);
		File image = (File) IdentifierFactory.getLiveChildOfClass(identifier,File.class);
		return image.getInode();
	}
	
	public String getLargeImageInode()
	{
		Identifier identifier = (Identifier) InodeFactory.getChildOfClassByRelationType(this,Identifier.class,WebKeys.PRODUCT_LARGE_IMAGE);
		File image = (File) IdentifierFactory.getLiveChildOfClass(identifier,File.class);
		return image.getInode();
	}
	
	public boolean hasBulkPricing()
	{
		boolean bulkPricing = false;
		List<ProductFormat> formats =  ProductFormatFactory.getAllFormatsByProduct(this);
		for(ProductFormat format : formats)
		{
			bulkPricing = (format.getBulkPrices().size() > 1 ? true : false);
			if(bulkPricing)
			{
				break;
			}
		}
		return bulkPricing;
	}
	public boolean getShowOnWeb() {
        return this.showOnWeb;
    }

    public void setShowOnWeb(boolean showOnWeb) {
        this.showOnWeb = showOnWeb;
    }
}
