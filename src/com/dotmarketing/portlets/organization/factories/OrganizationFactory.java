package com.dotmarketing.portlets.organization.factories;

import java.util.ArrayList;
import java.util.List;

import com.dotmarketing.business.APILocator;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.organization.model.Organization;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;
/**
 *
 * @author  will
 */
public class OrganizationFactory {

	private static CategoryAPI categoryAPI = APILocator.getCategoryAPI();
	
    public static CategoryAPI getCategoryAPI() {
		return categoryAPI;
	}

	public static void setCategoryAPI(CategoryAPI categoryAPI) {
		OrganizationFactory.categoryAPI = categoryAPI;
	}

	public static java.util.List getAllOrganizations() {
		DotHibernate dh = new DotHibernate(Organization.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.organization.model.Organization where type='organization' order by title");
		return dh.list();
	}
	public static java.util.List getAllOrganizations(String orderby) {
		DotHibernate dh = new DotHibernate(Organization.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.organization.model.Organization where type='organization' order by " + orderby);
		return dh.list();
	}

	public static java.util.List getAllFirstLevelOrganizations(String orderby) {
		DotHibernate dh = new DotHibernate(Organization.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.organization.model.Organization where type='organization' and (parent_organization = '0'or parent_organization is null) order by " + orderby);
		return dh.list();
	}

	public static java.util.List getChildrenOrganizations(Organization organization, String orderby) {
		return InodeFactory.getChildrenClass(organization, Organization.class, orderby);
	}
	public static Organization getFirstLevelOrganizationByPartnerURL(String partnerURL) {
		String condition = "partner_url = '" + partnerURL + "' and (parent_organization = '0' or parent_organization is null)";
		return (Organization) InodeFactory.getInodeOfClassByCondition(Organization.class, condition);
	}

	public static List getOrganizationsByZipCode(String zip) {
		String condition = "zip = '" + zip + "'";
		return InodeFactory.getInodesOfClassByCondition(Organization.class, condition);
	}

	public static List getOrganizationsByTitle(String title) {
		DotHibernate dh = new DotHibernate(Organization.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.organization.model.Organization where type='organization' and title = ? ");
		dh.setParam(title);
		return dh.list();
	}

	public static List getOrganizationsByTitleLike(String title) {
		if(!UtilMethods.isSet(title)){
			return new ArrayList();
		}
		DotHibernate dh = new DotHibernate(Organization.class);
		String condition = "lower(title) like '" + title.toLowerCase() + "%' and is_system = " + com.dotmarketing.db.DbConnectionFactory.getDBFalse();
		dh.setQuery("from inode in class com.dotmarketing.portlets.organization.model.Organization where type='organization' and " + condition + " order by title limit 20");
		return dh.list();
	}

	@SuppressWarnings("unchecked")
	public static List<Organization> getOrganizationsByZipCodeLike(String zip,boolean isSystem) {
		String condition = "zip like '" + zip + "%'";
		String system = (isSystem ? com.dotmarketing.db.DbConnectionFactory.getDBTrue() : com.dotmarketing.db.DbConnectionFactory.getDBFalse());
		condition += " and is_system = " + system;
		String orderBy = "title";
		return (List<Organization>)InodeFactory.getInodesOfClassByConditionAndOrderBy(Organization.class, condition, orderBy);
	}
	
	@SuppressWarnings("unchecked")
	public static List<Organization> getOrganizationsByZipCodeLike(String zip) {
		String condition = "zip like '" + zip + "%'";
		return (List<Organization>)InodeFactory.getInodesOfClassByCondition(Organization.class, condition);
	}

	public static List getOrganizationsByPartnerKey(String partnerKey,boolean isSystem) {
		String condition = "partner_key = '" + partnerKey + "'";
		String system = (isSystem ? com.dotmarketing.db.DbConnectionFactory.getDBTrue() : com.dotmarketing.db.DbConnectionFactory.getDBFalse());
		condition += " and is_system = " + system;
		return InodeFactory.getInodesOfClassByCondition(Organization.class, condition);
	}

	public static List getOrganizationsByPartnerKey(String partnerKey) {
		String condition = "partner_key = '" + partnerKey + "'";
		return InodeFactory.getInodesOfClassByCondition(Organization.class, condition);
	}

	public static java.util.List getOrganizationsSharingPartnerPortal(String partnerURL) {
		String condition = "partner_url = '" + partnerURL + "'";
		return InodeFactory.getInodesOfClassByCondition(Organization.class, condition);
	}

	public static java.util.List getFilteredOrganizations(String keywords, String category, String orderby, User user, boolean respectFrontendRoles) throws DotDataException, DotSecurityException {
		Category cat = null;
		String condition = "parent_organization = '0' or parent_organization is null ";

		if (UtilMethods.isSet(category)) {
			cat = categoryAPI.find(category, user, respectFrontendRoles);
		}

		if (UtilMethods.isSet(keywords)) {
			condition += " and lower(title) like '" + keywords.toLowerCase() + "%'";
		}
		if (cat!= null && InodeUtils.isSet(cat.getInode()) && condition !=null) {
			//both filters
			return InodeFactory.getChildrenClassByConditionAndOrderBy(cat,Organization.class,condition, orderby);
		}
		else if (cat!=null && InodeUtils.isSet(cat.getInode())) {
			//cat only
			return InodeFactory.getChildrenClass(cat,Organization.class,orderby);
		}
		else {
			//keywords only
			return InodeFactory.getInodesOfClassByCondition(Organization.class,condition,orderby);
		}
	}
	public static Organization getParentOrganization(Organization organization) {
		return (Organization) InodeFactory.getParentOfClass(organization, Organization.class);
	}

	public static java.util.List getAllSystems() {
		DotHibernate dh = new DotHibernate(Organization.class);
		dh.setQuery(
			"from inode in class com.dotmarketing.portlets.organization.model.Organization where type='organization' and is_system = " + com.dotmarketing.db.DbConnectionFactory.getDBTrue() + " order by title");
		return dh.list();
	}

	public static Organization newInstance() {
		Organization m = new Organization();
		return m;
	}

	public static void saveOrganization(Organization organization) {
		InodeFactory.saveInode(organization);
	}

	public static void deleteOrganization(Organization organization) {
		InodeFactory.deleteInode(organization);
	}

	public static Organization getOrganization(String inode) {
		return (Organization) InodeFactory.getInode(inode,Organization.class);
	}

	
	/**
	 * Get the organization by the partnerUrl
	 *
	 * @author Oswaldo Gallango
	 * @param partnerUrl String with the partner url i.e(/dotmarketing)
	 * @return a list of organization that matches with the partnerUrl
	 */
	@SuppressWarnings("unchecked")
	public static List<Organization> getOrganizationsByPartnerUrl(String partnerUrl) {
		String condition = "partner_url like '" + partnerUrl + "%'";
		return (List<Organization>)InodeFactory.getInodesOfClassByCondition(Organization.class, condition);
	}

}
