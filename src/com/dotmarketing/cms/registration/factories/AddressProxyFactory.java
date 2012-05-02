package com.dotmarketing.cms.registration.factories;

import com.dotmarketing.beans.Tree;
import com.dotmarketing.cms.registration.model.AddressProxy;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.util.Logger;
import com.liferay.portal.model.Address;

/**
 * 
 * @author Salvador Di Nardo
 */
public class AddressProxyFactory {

	public static AddressProxy getAddressProxy(String addressId) 
	{
		try 
		{
			DotHibernate dh = new DotHibernate(Tree.class);
			dh.setQuery("from address_proxy in class com.dotmarketing.cms.registration.model.AddressProxy where addressId = ?");
			dh.setParam(addressId);
			return (AddressProxy) dh.load();
		} 
		catch (Exception e) 
		{
			Logger.warn(AddressProxyFactory.class, "getUserProxy failed:" + e, e);
		}
		return new AddressProxy();
	}

	public static AddressProxy getAddressProxy(Address address) 
	{		
		return getAddressProxy(address.getAddressId());
	}
}
