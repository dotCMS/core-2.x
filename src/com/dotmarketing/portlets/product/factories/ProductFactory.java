package com.dotmarketing.portlets.product.factories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.cms.product.struts.ProductsForm;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.entities.factories.EntityFactory;
import com.dotmarketing.portlets.entities.model.Entity;
import com.dotmarketing.portlets.product.model.Product;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
/**
 *
 * @author  david
 */
public class ProductFactory {

	public static java.util.List getAllProducts() {
		DotHibernate dh = new DotHibernate(Product.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.product.model.Product where type='ecom_product' order by title");
		return dh.list();
	}
	
	public static java.util.List getAllProducts(String orderby) {
		DotHibernate dh = new DotHibernate(Product.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.product.model.Product where type='ecom_product' order by " + orderby);
		return dh.list();
	}
	
	public static java.util.List getAllProducts(String[] productType,String keyword,String orderBy,String direction) {
		String inProduct = "";
		if(productType != null)
		{
			for(int i = 0;i < productType.length;i++)
			{
				if (!(productType[i].equals("0")))
				{
					inProduct += "'" + productType[i] + "',";
				}
			}
			if (!inProduct.equals(""))
			{
				inProduct = inProduct.substring(0,inProduct.lastIndexOf(","));
			}
		}
		
		DotHibernate dh = new DotHibernate(Product.class);
		String query = "";
		if (inProduct.equals(""))
		{
			query = "select {ecom_product.*} from ecom_product, inode ecom_product_1_ where ecom_product_1_.type='ecom_product' and ecom_product.inode = ecom_product_1_.inode";
		}
		else
		{
			query = "select {ecom_product.*} from ecom_product, inode ecom_product_1_, tree where " +
					"ecom_product_1_.type='ecom_product' and ecom_product.inode = ecom_product_1_.inode and tree.parent = ecom_product.inode";
		}
		
		if (UtilMethods.isSet(keyword))
		{
			keyword = "%" + keyword.toLowerCase() + "%";
			query += " and lower(title) like '" + keyword + "'";
		}
		if (!inProduct.equals(""))
		{
			query += " and tree.child in (" + inProduct + ") ";				
		}
		if (UtilMethods.isSet(orderBy) && UtilMethods.isSet(direction))
		{
			query += " order by " + orderBy + " " + direction;
		}		
		dh.setSQLQuery(query);		
		return dh.list();			
	}
	

	public static Product newInstance() {
		Product dc = new Product();
		return dc;
	}

	public static void saveProduct(Product product) {
		InodeFactory.saveInode(product);
	}

	public static void deleteProduct(Product product) {
		List<ProductFormat> formats = ProductFormatFactory.getAllFormatsByProduct(product);
		for (ProductFormat format : formats) {
			ProductFormatFactory.deleteProductFormat(format);
		}
		InodeFactory.deleteInode(product);
	}
	
	public static void copyProduct(Product product) {
		CategoryAPI catAPI = APILocator.getCategoryAPI();
		//Create, populate and save the new product in the DB
		Product copyProduct = new Product();
		try
		{			
			//BeanUtils.copyProperties(copyProduct,product);
			copyProduct.setTitle(product.getTitle());
			copyProduct.setShortDescription(product.getShortDescription());
			copyProduct.setLongDescription(product.getLongDescription());
			copyProduct.setReqShipping(product.getReqShipping());
			copyProduct.setFeatured(product.getFeatured());			
			copyProduct.setComments(product.getComments());
		}
		catch(Exception ex)
		{
			Logger.debug(ProductFactory.class,ex.toString());
		}
		String title = copyProduct.getTitle();
		title += " COPY";
		copyProduct.setTitle(title);
		copyProduct.setInode(null);
		ProductFactory.saveProduct(copyProduct);		
		
		//Copy the new child formats
		List<ProductFormat> formats = ProductFormatFactory.getAllFormatsByProduct(product);
		for (ProductFormat format : formats) {
			ProductFormatFactory.copyProductFormat(copyProduct,format);
		}
		//Copy the Images
//		IMAGES
		//Small Image
		Identifier identifier = (Identifier) InodeFactory.getChildOfClassByRelationType(product,Identifier.class,WebKeys.PRODUCT_SMALL_IMAGE);
		if(InodeUtils.isSet(identifier.getInode())){
		copyProduct.addChild(identifier,WebKeys.PRODUCT_SMALL_IMAGE);
		}
		//Medium Image
		identifier = (Identifier) InodeFactory.getChildOfClassByRelationType(product,Identifier.class,WebKeys.PRODUCT_MEDIUM_IMAGE);
		if(InodeUtils.isSet(identifier.getInode())){
		copyProduct.addChild(identifier,WebKeys.PRODUCT_MEDIUM_IMAGE);
		}
		
		//Large Image
		identifier = (Identifier) InodeFactory.getChildOfClassByRelationType(product,Identifier.class,WebKeys.PRODUCT_LARGE_IMAGE);
		if(InodeUtils.isSet(identifier.getInode())){
		copyProduct.addChild(identifier,WebKeys.PRODUCT_LARGE_IMAGE);
		}				          
		//Copy the Files      
		List identifiers = InodeFactory.getChildrenClassByRelationType(product,Identifier.class,WebKeys.PRODUCT_FILES);
		Iterator it = identifiers.iterator();
		while (it.hasNext()) {
			identifier = (Identifier)it.next();
			copyProduct.addChild(identifier,WebKeys.PRODUCT_FILES);
		}
		//Product Type
		List<Category> categories = new ArrayList<Category>();
		try {
			categories = catAPI.getParents(product, false,WebKeys.PRODUCT_PRODUCTS_TYPE,APILocator.getUserAPI().getSystemUser(), false);
		} catch (DotDataException e) {
			Logger.error(ProductFactory.class, e.getMessage(), e);
		} catch (DotSecurityException e) {
			Logger.error(ProductFactory.class, e.getMessage(), e);
		}        
        for(int i = 0;i < categories.size();i++)
        {
        	Category category = (Category) categories.get(i);
        	copyProduct.addParent(category,WebKeys.PRODUCT_PRODUCTS_TYPE);
        }
        //Product Topics
        categories = InodeFactory.getParentsOfClassByRelationType(product,Category.class,WebKeys.PRODUCT_CATEGORIES);        
        for(int i = 0;i < categories.size();i++)
        {
        	Category category = (Category) categories.get(i);
        	copyProduct.addParent(category,WebKeys.PRODUCT_CATEGORIES);
        }
        //Copy related products
        List<Product> relatedProducts = InodeFactory.getChildrenClassByRelationType(product,Product.class,WebKeys.PRODUCT_RELATED);
        for(Product relatedProduct : relatedProducts)
        {
        	copyProduct.addChild(relatedProduct,WebKeys.PRODUCT_RELATED);
        }
	}

	public static Product getProduct(String inode) {
		return (Product) InodeFactory.getInode(inode,Product.class);
	}
	
	public static List<Product> getAllProductsByCategory(String categoryInode,String orderBy,String filter,int page,int pageSize,String direction, String condition)
	{
		List<Product> listProducts = null;
		try {
            String tableName = ((Inode) Product.class.newInstance()).getType();
            DotHibernate dh = new DotHibernate(Product.class);
            String sql = "SELECT {" + tableName + ".*} "
            			 + "from " + tableName + " " + tableName + ", "
            			 + "inode " + tableName  + "_1_ "
				 + "WHERE " + tableName + "_1_.inode = " + tableName + ".inode and " + tableName + "_1_.type='ecom_product' and " + tableName  + ".inode IN ("
				 + "SELECT DISTINCT " + tableName  + "_1_.inode FROM "
				 + "tree tree,tree tree1,tree tree2,entity entity, category category where tree.parent = " 
				 + tableName  + "_1_.inode and tree.child = category.inode and category.inode = tree1.child and "
				 + "tree1.parent = tree2.parent and tree2.child = entity.inode "
            	 + "and entity.entity_name = ? ";
            
            if (UtilMethods.isSet(condition)) {
            	sql += "and " + condition;
            }
            
            //Category
            String sqlCategory = "";
            if (InodeUtils.isSet(categoryInode))
            {
            	sqlCategory = "and category.inode = ? ";
            }
            
            //Filter by title
            String sqlFilter = "";
            if(UtilMethods.isSet(filter))
            {
            	sqlFilter = "and " + tableName + ".title like '%" + filter + "%' ";        
            }
            
            //Order
            String sqlOrder = ""; 
            if (UtilMethods.isSet(orderBy))
            {
            	direction = (UtilMethods.isSet(direction) ? direction : "ASC");
            	sqlOrder = "order by " + tableName + "." + orderBy + " " + direction + " ";            
            }
            
            //The final SQL statement; 
            sql = sql + sqlCategory + sqlFilter + ") " + sqlOrder;
            
            //The page number to retrieve
            if(pageSize > 0 && page >= 0)
            {            	
            	dh.setFirstResult(page * pageSize);
            	dh.setMaxResults(pageSize);
            }
            
            //Run the SQL Statement
            dh.setSQLQuery(sql);
            dh.setParam(WebKeys.PRODUCT_PRODUCTS_TYPE);
            if (InodeUtils.isSet(categoryInode))
            {
            	dh.setParam(categoryInode);
            }         
            listProducts = dh.list();                    
        } catch (Exception e) {
            Logger.error(ProductFactory.class, "getChildrenClass failed:" + e, e);
            throw new DotRuntimeException(e.toString());
        }      	
		return listProducts;		
	}
	
	public static List<Product> getAllFeaturedProductsByCategory(String categoryInode,String orderBy,String filter,int page,int pageSize,String direction)
	{		
		//boolean trueValue = true; 
		List<Product> listProducts = null;
		try {
            String tableName = ((Inode) Product.class.newInstance()).getType();
            DotHibernate dh = new DotHibernate(Product.class);
            String sql = "SELECT {" + tableName + ".*} "
            			 + "from " + tableName + " " + tableName + ", "
            			 + "tree tree, "
            			 + "tree tree1, "
            			 + "tree tree2, "
            			 + "entity entity, "
            			 + "inode " + tableName + "_1_, "
            			 + "category category "
            			 + "where " + tableName + ".featured = " + DbConnectionFactory.getDBTrue() + " "  
            			 + "and tree.child = " + tableName + ".inode "
            			 + "and " + tableName + "_1_.inode = " + tableName + ".inode "
            			 + "and " + tableName + "_1_.type='ecom_product' "
            			 + "and tree.parent = category.inode "
            			 + "and category.inode = tree1.child " 
            	         + "and tree1.parent = tree2.parent "
            	         + "and tree2.child = entity.inode "
            	         + "and entity.entity_name = ? "
            	         + "and showOnWeb = " + DbConnectionFactory.getDBTrue() + " ";
            
            //Category
            String sqlCategory = "";
            if (InodeUtils.isSet(categoryInode))
            {
            	sqlCategory = "and category.inode = ? ";
            }
            
            //Filter by title
            String sqlFilter = "";
            if(UtilMethods.isSet(filter))
            {
            	sqlFilter = "and " + tableName + ".title like '%" + filter + "%' ";        
            }
            
            //Order
            String sqlOrder = ""; 
            if (UtilMethods.isSet(orderBy))
            {
            	sqlOrder = "order by " + orderBy + " " + direction + " ";            
            }
            
            //The final SQL statement; 
            sql = sql + sqlCategory + sqlFilter + sqlOrder;
            
            //The page number to retrieve
            if(pageSize > 0 && page >= 0)
            {            	
            	dh.setFirstResult(page * pageSize);
            	dh.setMaxResults(pageSize);
            }
            
            //Run the SQL Statement
            dh.setSQLQuery(sql);
            //dh.setParam(DbConnectionFactory.getDBTrue());
            dh.setParam(WebKeys.PRODUCT_PRODUCTS_TYPE);
            if (InodeUtils.isSet(categoryInode))
            {
            	dh.setParam(categoryInode);
            }         
            listProducts = dh.list();
        } catch (Exception e) {
            Logger.error(ProductFactory.class, "getChildrenClass failed:" + e, e);
            throw new DotRuntimeException(e.toString());
        }      	
		return listProducts;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Product> getAllProductsByCategoryList(List<Category> categories,int topProductNumber,String orderBy,String direction,String filter,int page,int pageSize)
	{
		List<Product> listProducts = null;
		try {
            String tableName = ((Inode) Product.class.newInstance()).getType();
            DotHibernate dh = new DotHibernate(Product.class);
            String sql = "SELECT";
           if(topProductNumber > 0){
        	   sql = sql + " top "+ topProductNumber;
           }
            sql = sql + " {" + tableName + ".*} "
            			 + "from " + tableName + " " + tableName + ", "
            			 + "tree tree, "
            			 + "tree tree1, "
            			 + "tree tree2, "
            			 + "entity entity, "
            			 + "inode " + tableName + "_1_, "
            			 + "category category "
            			 + "where tree.child = " + tableName + ".inode "
            			 + "and " + tableName + "_1_.inode = " + tableName + ".inode "
            			 + "and " + tableName + "_1_.type='ecom_product' "
            			 + "and tree.parent = category.inode "
            			 + "and category.inode = tree1.child " 
            	         + "and tree1.parent = tree2.parent "
            	         + "and tree2.child = entity.inode "
            	         + "and showOnWeb = " + DbConnectionFactory.getDBTrue() + " ";
            	         //+ "and entity.entity_name = ? ";
            
            //Category
            String sqlCategory = "";
            if (categories != null && categories.size() > 0)
            {
            	sqlCategory = "and category.inode in (";
            	if(categories.size() >1){
	            	for(int i = 0 ; i < categories.size() - 1 ; i++ ){
	            		
	            		sqlCategory = sqlCategory + categories.get(i).getInode() + ",";
	            	}
            	}
            	sqlCategory = sqlCategory + categories.get(0).getInode() + ") ";
            }
            
            //Filter by title
            String sqlFilter = "";
            if(UtilMethods.isSet(filter))
            {
            	sqlFilter = "and " + tableName + ".title like '%" + filter + "%' ";        
            }
            
            //Order
            String sqlOrder = ""; 
            if (UtilMethods.isSet(orderBy))
            {
            	sqlOrder = "order by " + orderBy + " " + direction + " ";            
            }
            
            //The final SQL statement; 
            sql = sql + sqlCategory + sqlFilter + sqlOrder;
            
            //The page number to retrieve
            if(pageSize > 0 && page >= 0)
            {            	
            	dh.setFirstResult(page * pageSize);
            	dh.setMaxResults(pageSize);
            }
            
            //Run the SQL Statement
            dh.setSQLQuery(sql);
           
            listProducts = dh.list();                    
        } catch (Exception e) {
            Logger.error(ProductFactory.class, "getChildrenClass failed:" + e, e);
            throw new DotRuntimeException(e.toString());
        }      	
		return listProducts;		
	}
	
	public static void loadFeaturedProductByCategory(ActionForm lf,HttpServletRequest request)
	{
		ProductsForm productsForm = (ProductsForm) lf;
		String categoryInode = productsForm.getCategoryInode();
		String orderBy = productsForm.getOrderBy();
		if (!UtilMethods.isSet(orderBy)) {
			orderBy = "ecom_product.sort_order";
		}
		int page = productsForm.getPage();
		int pageSize = productsForm.getPageSize();
		String direction = productsForm.getDirection();
		if (!UtilMethods.isSet(direction)) {
			direction = "asc";
		}
		String filter = productsForm.getFilter();
		//Retrieve the products
		List<Product> listProducts = ProductFactory.getAllFeaturedProductsByCategory(categoryInode,orderBy,filter,page,pageSize,direction);
		
		//Save the list of products
		productsForm.setListProducts(listProducts);
	}
	
	public static void loadProductByCategory(ActionForm lf,HttpServletRequest request)
	{
		ProductsForm productsForm = (ProductsForm) lf;
		String categoryInode = productsForm.getCategoryInode();
		String orderBy = productsForm.getOrderBy();
		int page = productsForm.getPage();
		int pageSize = productsForm.getPageSize();
		String direction = productsForm.getDirection();
		String filter = productsForm.getFilter();
		
		//Retrieve the products		
		orderBy = (UtilMethods.isSet(orderBy) ? orderBy : "sort_order");
		String condition = "showOnWeb = " + DbConnectionFactory.getDBTrue() + " ";
		List<Product> listProducts = ProductFactory.getAllProductsByCategory(categoryInode,orderBy,filter,page,pageSize,direction,condition);
		
		//Save the list of products
		productsForm.setListProducts(listProducts);
	}
	
	public static void loadProduct(ActionForm lf,HttpServletRequest request)
	{
		ProductsForm productsForm = (ProductsForm) lf;
		String productInode = productsForm.getInode();
		
		//Retrieve the products
		List<Product> listProducts = new ArrayList();
		Product product = ProductFactory.getProduct(productInode);
		listProducts.add(product);
		
		//Save the list of products
		productsForm.setListProducts(listProducts);
	}
	
	public static void loadTypeProducts(ActionForm lf,HttpServletRequest request, com.liferay.portal.model.User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException
	{
		CategoryAPI catAPI = APILocator.getCategoryAPI();
		ProductsForm productsForm = (ProductsForm) lf;
		
		String typeProductEntityName = WebKeys.PRODUCT_PRODUCTS_TYPE;
		Entity entity = EntityFactory.getEntity(typeProductEntityName);
			
		//Retrieve the products
		List<Category> listTypeProducts = catAPI.getParents(entity, user, respectFrontendRoles);
		//This is not a generic methods, it supose it has only one master category and only one level
		Category masterCategory = listTypeProducts.get(0);
		
		listTypeProducts = catAPI.getChildren(masterCategory, user, respectFrontendRoles);
		
		//Save the list of products
		productsForm.setListTypeProducts(listTypeProducts);
	}
}
