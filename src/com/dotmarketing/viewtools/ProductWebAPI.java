package com.dotmarketing.viewtools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.cms.product.model.Holder;
import com.dotmarketing.cms.product.struts.ProductsForm;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.discountcode.factories.DiscountCodeFactory;
import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.order_manager.factories.OrderFactory;
import com.dotmarketing.portlets.order_manager.factories.OrderItemFactory;
import com.dotmarketing.portlets.order_manager.model.Order;
import com.dotmarketing.portlets.order_manager.model.OrderItem;
import com.dotmarketing.portlets.order_manager.struts.OrderForm;
import com.dotmarketing.portlets.product.factories.ProductFactory;
import com.dotmarketing.portlets.product.factories.ProductFormatFactory;
import com.dotmarketing.portlets.product.model.Product;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.model.User;

/*
 * @author Oswaldo Gallango
 */

public class ProductWebAPI implements ViewTool {

    private HttpServletRequest request;
    private ContentletAPI conAPI = APILocator.getContentletAPI();
    private CategoryAPI catAPI = APILocator.getCategoryAPI();
    private User loggedInUser;
    
    private static String productCategory = null;
    
    
    public void init(Object obj) {
        ViewContext context = (ViewContext) obj;
        this.request = context.getRequest();
        HttpSession ses = request.getSession(false);
        if (ses != null)
        	loggedInUser = (User) ses.getAttribute(WebKeys.CMS_USER);
    }
    
    public void init(HttpServletRequest req, HttpServletResponse response) {
        this.request = req;

    }

	public Product getProduct(String inode) {
		return (Product) UtilMethods.getProduct(inode);
	}
	
	@Deprecated
	public Product getProduct(long inode) {
		return getProduct(String.valueOf(inode));
	}
	
	public float getTotalApplicableDiscount(List<Holder> holders,List<DiscountCode> discounts,boolean partner)
	{
		return DiscountCodeFactory.getTotalApplicableDiscount(holders,discounts,partner);
	}

	@SuppressWarnings("unchecked")
	public List<Order> getProductOrdersByUser(String userId){
		if (userId == null)
			return new ArrayList<Order>();
		
		String inode;
		try {
			inode = String.valueOf(com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(userId,APILocator.getUserAPI().getSystemUser(), false).getInode());
		} catch (Exception e) {
			Logger.error(this, e.getMessage(), e);
			return new ArrayList<Order>();
		}
		
		return OrderFactory.getOrdersByUserInode(inode);
	}
	
	@SuppressWarnings("unchecked")
	public List<Order> getProductOrdersByUserInode(String inode){
		if (inode == null)
			return new ArrayList<Order>();
		
		return OrderFactory.getOrdersByUserInode(inode);
	}
	
	public String getProductNamebyId(String productInode){
		
		Product product = (Product) ProductFactory.getProduct(productInode); 
		return product.getTitle();
	}
	
	@Deprecated
    public String getProductNamebyId(long productInode){
		return getProductNamebyId(String.valueOf(productInode));
	}
	
	
	public Product getProductbyProductFormaId(String productFormatInode){
		
		ProductFormat productF = (ProductFormat) ProductFormatFactory.getProductFormat(productFormatInode); 
		return getProductById(productF.getProductInode());
	}
	
	@Deprecated
	public Product getProductbyProductFormaId(long productFormatInode){
		return getProductbyProductFormaId(String.valueOf(productFormatInode));
	}
	
	@Deprecated
	public Product getProductById(long productInode){
		return getProductById(String.valueOf(productInode));
		
	}
   public Product getProductById(String productInode){
	   Product product = getProduct(productInode); 
		return product;
	}
	
	public List<Map> getPaymentMethods () {
		List<Map> paymentsList = new ArrayList<Map> ();
		String[] paymentMethods = Config.getStringArrayProperty("ECOM_PAYMENT_TYPES");
		for (String paymentMethod : paymentMethods) {
			String value = Config.getStringProperty(paymentMethod);
			String friendlyName = Config.getStringProperty(paymentMethod + "_FN");
			Map paymentMethodMap = new HashMap ();
			paymentMethodMap.put("value", value);
			paymentMethodMap.put("friendlyName", friendlyName);
			paymentMethodMap.put("name", paymentMethod);
			paymentsList.add(paymentMethodMap);
		}
		return paymentsList;
	}
	
	public OrderForm loadOrderForm()
	{
		return UtilMethods.loadOrderForm(request);
	}
    
    public String getUserFullName(OrderForm orderForm)
    {    	
    	return UtilMethods.getUserFullName(orderForm);    	
    }
    
    public String getUserCompanyName(OrderForm orderForm)
    {    	
    	return UtilMethods.getUserCompanyName(request,orderForm);
    }
    
    	public String getMonth(String month){
		
		return getMonth(Integer.parseInt(month));
	}
	
	public String getMonth(int month){
		
		String strMonth = "";
		
		switch (month){
		
		case 0: strMonth = "January";
				break;
		case 1: strMonth = "February";
				break;
		case 2: strMonth = "March";
				break;
		case 3: strMonth = "April";
				break;
		case 4: strMonth = "May";
				break;
		case 5: strMonth = "June";
				break;
		case 6: strMonth = "July";
				break;
		case 7: strMonth = "August";
				break;
		case 8: strMonth = "September";
				break;
		case 9: strMonth = "October";
				break;
		case 10: strMonth = "November";
				break;
		case 11: strMonth = "December";
				break;
		
		}
		
		return strMonth;
	}
	
	/*Get quatity of random product by categories*/
	public List<Product> getRelatedProduct(String inode, String quantity) throws DotDataException, DotSecurityException{
		int productQuatity = Integer.parseInt(quantity);
		return getRelatedProduct(inode,productQuatity);
	}
	
	@Deprecated
	public List<Product> getRelatedProduct(long inode, int quantity) throws DotDataException, DotSecurityException{
			return getRelatedProduct(String.valueOf(inode),quantity);
	}
	
	/*Get quatity of random product by categories*/
	
	@SuppressWarnings("unchecked")
	public List<Product> getRelatedProduct(String inode, int quantity) throws DotDataException, DotSecurityException{
		
		List<Category> categoriesList = null;
		
		if(InodeUtils.isSet(inode)){
			try {
				Product source = (Product) InodeFactory.getInode(inode, Product.class);
				categoriesList = (List<Category>) InodeFactory.getParentsOfClass(source,Category.class);
			}
			catch (Exception e) {
				Contentlet source = conAPI.find(inode, loggedInUser, true);
				categoriesList = catAPI.getChildren(source, loggedInUser, true);
			}
		}
		
		List<Product> productList = ProductFactory.getAllProductsByCategoryList(categoriesList,quantity,"newid()","","",0,0);
		
		if(productList == null || productList.size() == 0){
			categoriesList = null;
			productList = ProductFactory.getAllProductsByCategoryList(categoriesList,quantity,"newid()","","",0,0);
		}

		return productList;
	}
	
	public User getUserByOrder(String orderUserInode){
		
		UserProxy orderUser;
		User user;
		try {
			orderUser = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(orderUserInode,APILocator.getUserAPI().getSystemUser(), false);
			user = APILocator.getUserAPI().loadUserById(orderUser.getUserId(),APILocator.getUserAPI().getSystemUser(),false);
		} catch (Exception e) {
			Logger.error(this, e.getMessage(), e);
			return null;
		}
		
		return user;
	}
	
	@SuppressWarnings("unchecked")
	public List<OrderItem> getOrderItemListByOrder(String orderInode){
		
		List<OrderItem> orderItemList = (List<OrderItem>) OrderItemFactory.getOrderItemsByOrderId(orderInode);
		return orderItemList;
		
	}
	
	public List<Product> getProducts(String page, String pageSize){
		int pageInt = 0;
		int pageSizeInt = 20;
		try{
			pageInt = Integer.parseInt(page);
		}
		catch(Exception e){}
		try{
			pageSizeInt = Integer.parseInt(pageSize);
		}
		catch(Exception e){}
			
		return getProducts(pageInt, pageSizeInt, null);
	}
	
	@SuppressWarnings("deprecation")
	public List<Product> getProducts(int page, int pageSize){

		return getProducts(page, pageSize, null);
	}
	

	@SuppressWarnings("unchecked")
	public List<Product> getProducts(int page, int pageSize, String condition){
		
		
		
		if(UtilMethods.isSet(condition)){
			condition  = " and showonweb = " +DbConnectionFactory.getDBTrue();
		}
		else{
			condition  = " showonweb = " +DbConnectionFactory.getDBTrue();
		}
		

		return (List<Product>) InodeFactory.getInodesOfClassByConditionAndOrderBy(Product.class, condition, "sort_order", pageSize, page * pageSize);
	}


	public ActionForm loadFeaturedProductByCategory(ActionForm lf,HttpServletRequest request)
	{
		lf = (lf != null ? lf : new ProductsForm());
		ProductFactory.loadFeaturedProductByCategory(lf,request);
		return lf;
	}
	
	public ActionForm loadProductByCategory(ActionForm lf,HttpServletRequest request)
	{
		lf = (lf != null ? lf : new ProductsForm());
		ProductFactory.loadProductByCategory(lf,request);
		return lf;
	}
	
	public ActionForm loadProduct(ActionForm lf,HttpServletRequest request)
	{
		lf = (lf != null ? lf : new ProductsForm());
		ProductsForm productsForm = (ProductsForm) lf;
		if (InodeUtils.isSet(request.getParameter("inode")))
		{
			productsForm.setInode(request.getParameter("inode"));
		}
		ProductFactory.loadProduct(lf,request);
		return lf;
	}
	
	public ActionForm loadTypeProducts(ActionForm lf, HttpServletRequest request)
	{
		lf = (lf != null ? lf : new ProductsForm());
		try {
			ProductFactory.loadTypeProducts(lf,request, loggedInUser, true);
		} catch (DotDataException e) {
			Logger.error(this, e.getMessage(), e);
		} catch (DotSecurityException e) {
			Logger.error(this, e.getMessage(), e);
		}
		return lf;
	}
	
//	public List loadProductFilesList(ActionForm lf,HttpServletRequest request)
	public List loadProductFilesList(HttpServletRequest request)
	{
//		Product product = (Product) request.getAttribute(WebKeys.PRODUCT_PRODUCT);
		Product product = new Product();
		product.setInode(request.getParameter("inode"));
		Identifier identifier;
		File file;
		
		ArrayList<File> files = new ArrayList<File>();
		List identifiers = InodeFactory.getChildrenClassByRelationType(product,Identifier.class,WebKeys.PRODUCT_FILES);
		Iterator it = identifiers.iterator();
		
		while (it.hasNext()) {
			identifier = (Identifier) it.next();
			file = (File) IdentifierFactory.getWorkingChildOfClass(identifier, File.class);
			files.add((File) InodeFactory.getInode(file.getInode(), File.class));
		}
		
		return files;
	}
}
