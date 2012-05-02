package com.dotmarketing.cms.product.model;

import java.util.ArrayList;
import java.util.List;

import com.dotmarketing.portlets.discountcode.model.DiscountCode;
import com.dotmarketing.portlets.product.factories.ProductFormatFactory;
import com.dotmarketing.portlets.product.model.ProductFormat;
import com.dotmarketing.util.InodeUtils;
import com.liferay.portal.model.User;

public class ShoppingCart {
	String inode = "";
	private User user;
	private List<Holder> holders;
	private List<DiscountCode> discountCodes;
	
	public ShoppingCart()
	{
		holders = new ArrayList<Holder>();
		discountCodes = new ArrayList<DiscountCode>();
	}

	public List<Holder> getHolders() {
		return holders;
	}
	public void setHolders(List<Holder> holders) {
		this.holders = holders;
	}
	public List<DiscountCode> getDiscountCodes() {
		return discountCodes;
	}

	public void setDiscountCodes(List<DiscountCode> discountCodes) {
		this.discountCodes = discountCodes;
	}

	/**
	 * Find an entry in the shopping cart
	 * @param formatInode
	 * @return
	 */
	public Holder findHolder(String formatInode)
	{
		Holder holder = null;
		for(Holder holderAux : holders)
		{
			if (holderAux.getFormat().getInode().equalsIgnoreCase(formatInode))
			{
				holder = holderAux;
				break;
			}
		}
		//If not found add a new entry
		if (holder == null)
		{
			holder = new Holder();
			ProductFormat format = ProductFormatFactory.getProductFormat(formatInode);
			if(InodeUtils.isSet(format.getInode())){
				holder.setFormat(format);
				holders.add(holder);
			}
		}
		return holder;
	}
	
	private Holder findHolderByHolderInode(String holderInode)
	{
		Holder holder = null;
		for(Holder holderAux : holders)
		{
			if (holderAux.getInode().equalsIgnoreCase(holderInode))
			{
				holder = holderAux;
				break;
			}
		}		
		return holder;
	}
	
	/**
	 * Add an item to the shoppingCart
	 * @param quantity
	 * @param formatInode
	 */
	public void addItem(int quantity,String formatInode)
	{	
		Holder holder = findHolder(formatInode);
		int originalquantity = holder.getQuantity();
		int newQuantity = originalquantity + quantity;
		holder.setQuantity(newQuantity);		
	}
	
	/**
	 * Set the quantity of an entry in the shoppingCart
	 * @param quantity
	 * @param formatInode
	 */
	public void setQuantityItem(int quantity,String formatInode)
	{	
		Holder holder = findHolder(formatInode);
		holder.setQuantity(quantity);
	}
	
	public void setQuantityItemByHolderInode(int quantity,String HolderInode)
	{	
		Holder holder = findHolderByHolderInode(HolderInode);
		if(holder != null)
		{
			holder.setQuantity(quantity);
		}
	}
	
	/**
	 * Delete an item to the shoppingCart
	 * @param quantity
	 * @param formatInode
	 */
	public void deleteItem(int quantity,String formatInode)
	{	
		Holder holder = findHolder(formatInode);
		int originalquantity = holder.getQuantity();
		int newQuantity = originalquantity - quantity;
		newQuantity = (newQuantity <= 0 ? 0 : newQuantity);
		holder.setQuantity(newQuantity); 
	}
	
	public void removeItem(String formatInode)
	{
		for(int i = 0;i < holders.size();i++)
		{
			Holder holderAux = holders.get(i);
			if (holderAux.getFormat().getInode().equalsIgnoreCase(formatInode))
			{
				holders.remove(i);
				break;
			}
		}		
	}
	
	/**
	 * Clear the shoppingCart	
	 */
	public void clear()
	{	
		inode = "";
		user = null;
		holders.clear();		
		discountCodes.clear();
	}
	
	/**
	 * return how many items do you have in the shopping cart
	 */
	public int numberItems()
	{
		int returnValue = 0;
		for(Holder holder : holders)
		{
			returnValue += holder.getQuantity();
		}
		return returnValue;
	}
	
	public void addDiscount(DiscountCode discountCode)
	{
		if(!discountCodes.contains(discountCode))
		{
			discountCodes.add(discountCode);
		}
	}
	
	public DiscountCode findDiscountCode(String discountId)
	{
		DiscountCode returnDiscount = null;
		for(DiscountCode discountCode : discountCodes)
		{
			if (discountCode.getCodeId().equals(discountId))
			{
				returnDiscount = discountCode;
				break;
			}
		}
		return returnDiscount;
	}
	
	public void deleteDiscountCode(String discountId)
	{
		for(int i = 0; i < discountCodes.size();i++)
		{
			DiscountCode discountCode = discountCodes.get(i);
			if (discountCode.getCodeId().equals(discountId))
			{
				discountCodes.remove(i);
				break;
			}
		}
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getInode() {
		if(InodeUtils.isSet(inode))
    		return inode;
    	
    	return "";
	}

	public void setInode(String inode) {
		this.inode = inode;
	}
}