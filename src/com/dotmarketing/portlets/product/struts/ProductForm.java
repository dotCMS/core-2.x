package com.dotmarketing.portlets.product.struts;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.language.LanguageUtil;
import com.liferay.portal.model.User;
import com.liferay.portal.util.Constants;

public class ProductForm extends ValidatorForm {

	private static final long serialVersionUID = 1L;
	private String inode; //identifier
    private String title; //persistent
    private String shortDescription; //persistent
    private String longDescription; //persistent
    private boolean reqShipping; //persistent
    private boolean featured; //persistent
    private String comments; //persistend
         
    //Categories
    private String[] productTypes;
    private String[] topics;
    //Files
    private String[] fileIdList;
    
    //Images
    private String smallImage;
    private String mediumImage;
    private String largeImage;
        
    private String orderBy = "sort_order";
    private String direction = "asc";
    private String keyword;
    private List products;
    private List formats; 
    private String[] relatedProducts;
    private boolean showOnWeb;
    public ProductForm() 
    {
    }

    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && request.getParameter("cmd").equals(Constants.ADD)) {
            return super.validate(mapping, request);
        }
        return null;
    }

	public boolean isFeatured() {
		return featured;
	}

	public void setFeatured(boolean featured) {
		this.featured = featured;
	}

	public String getInode() {
		if(InodeUtils.isSet(inode))
			return inode;
		
		return "";
	}

	public void setInode(String inode) {
		this.inode = inode;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public boolean isReqShipping() {
		return reqShipping;
	}

	public void setReqShipping(boolean reqShipping) {
		this.reqShipping = reqShipping;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String[] getProductTypes() {
		return productTypes;
	}

	public void setProductTypes(String[] productTypes) {
		this.productTypes = productTypes;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public List getFormats() {
		return formats;
	}

	public void setFormats(List formats) {
		this.formats = formats;
	}

	public List getProducts() {
		return products;
	}

	public void setProducts(List products) {
		this.products = products;
	}

	public String[] getTopics() {
		return topics;
	}

	public void setTopics(String[] topics) {
		this.topics = topics;
	}

	public void setFileIds(String fileIds) {
		setFileIdList(fileIds.split(","));
	}
	
	public String[] getFileIdList() {
		return fileIdList;
	}

	public void setFileIdList(String[] filesInodesList) {
		fileIdList = filesInodesList;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLargeImage() {
		return largeImage;
	}

	public void setLargeImage(String largeImage) {
		this.largeImage = largeImage;
	}

	public String getMediumImage() {
		return mediumImage;
	}

	public void setMediumImage(String mediumImage) {
		this.mediumImage = mediumImage;
	}

	public String getSmallImage() {
		return smallImage;
	}

	public void setSmallImage(String smallImage) {
		this.smallImage = smallImage;
	}

	public ActionErrors validate(ActionMapping mapping,ActionRequest req, User user) throws Exception {
		ActionErrors ae = new ActionErrors();
		if(!UtilMethods.isSet(title))
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Title")));
		}
		if(productTypes != null && productTypes.length == 0)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Product-Type")));
		}
		if(!UtilMethods.isSet(shortDescription))
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Short-Description")));
		}
		if(!UtilMethods.isSet(longDescription))
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory",LanguageUtil.get(user, "Long-Description")));
		}
		/*if(smallImage == 0)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Small Image"));
		}
		if(mediumImage == 0)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Medium Image"));
		}
		if(largeImage == 0)
		{
			ae.add(Globals.ERROR_KEY,new ActionMessage("error.form.mandatory","Large Image"));
		}*/
		return ae;
	}

	public String[] getRelatedProducts() {
		return relatedProducts;
	}

	public void setRelatedProducts(String[] relatedProducts) {
		this.relatedProducts = relatedProducts;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public boolean isShowOnWeb() {
        return this.showOnWeb;
    }

    public void setShowOnWeb(boolean showOnWeb) {
        this.showOnWeb = showOnWeb;
    }
}
