package com.dotmarketing.portlets.organization.ajax;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.organization.factories.OrganizationFactory;
import com.dotmarketing.portlets.organization.model.Organization;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;


/**
 * @author David
 */
public class OrganizationAjax {

	public String generatePartnerKey (String orgName) {
		if (orgName.length()>=5) {
			orgName = orgName.substring(0,4);
		}
		List organizations = new ArrayList();
		String partnerKey = "";
		
		do {
			long random = Math.round(Math.random() * 900) + 100;
			partnerKey = orgName.toUpperCase() + "-" + random; 
			organizations = OrganizationFactory.getOrganizationsByPartnerKey(partnerKey);
		} while (organizations.size() >= 1);
		
		return partnerKey;
	}
	
	public Map getOrganizationMap(String inode) {
		Organization organization = OrganizationFactory.getOrganization(inode);
		return getOrganizationMap (organization);
	}
	
	private Map<String,String> getOrganizationMap (Organization organization) {
		Map<String,String> map = new HashMap<String,String> ();
		map.put("partnerKey",organization.getPartnerKey());
		map.put("title", organization.getTitle().trim());
		map.put("partnerUrl",organization.getPartnerUrl());
		map.put ("inode", String.valueOf(organization.getInode()));
		map.put ("ceoName",organization.getCeoName());
		map.put ("city",organization.getCity());
		map.put ("country",organization.getCountry());
		map.put ("fax",organization.getFax());
		map.put ("phone",organization.getPhone());
		map.put ("state",organization.getState());
		map.put ("street1",organization.getStreet1());
		map.put ("street2",organization.getStreet2());
		map.put ("title",organization.getTitle());
		map.put ("zip",organization.getZip());

		//gets partner logo
		if (InodeUtils.isSet(organization.getPartnerLogo())) {
			Identifier identifier = (Identifier) InodeFactory.getInode(organization.getPartnerLogo(),Identifier.class);
			File file = (File) IdentifierFactory.getWorkingChildOfClass(identifier,File.class);
			map.put("logoImage", String.valueOf(file.getInode()));
		}
		return map;
	}
	
	public List<Map> getOrganizationsByZipCodeLike (String zip) {
		boolean isSystem = false;
		List<Organization> organizations = OrganizationFactory.getOrganizationsByZipCodeLike(zip,isSystem);
		ArrayList<Map> orgsList = new ArrayList<Map> ();
		for (Organization org : organizations) {
			try {
				orgsList.add(getOrganizationMap(org));
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			}			
		}
		return orgsList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map> getOrganizationsByTitle (String title) {
		List<Organization> organizations = OrganizationFactory.getOrganizationsByTitleLike(title);
		ArrayList<Map> orgsList = new ArrayList<Map> ();
		for (Organization org : organizations) {
			try {
				orgsList.add(getOrganizationMap(org));
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			}			
		}
		return orgsList;
	}
	
	@SuppressWarnings("unchecked")
	public List<Map> getOrganizationsByPartnerKey (String key) {
		boolean isSystem = false;
		List<Organization> organizations = OrganizationFactory.getOrganizationsByPartnerKey(key,isSystem);
		ArrayList<Map> orgsList = new ArrayList<Map> ();
		for (Organization org : organizations) {
			try {
				orgsList.add(getOrganizationMap(org));
			} catch (Exception e) {
				Logger.error(this, e.getMessage(), e);
			}			
		}
		return orgsList;
	}
}