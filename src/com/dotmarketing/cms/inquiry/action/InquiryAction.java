package com.dotmarketing.cms.inquiry.action;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.beans.UserProxy;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.NoSuchUserException;
import com.dotmarketing.business.Role;
import com.dotmarketing.cms.factories.PublicAddressFactory;
import com.dotmarketing.cms.factories.PublicCompanyFactory;
import com.dotmarketing.cms.factories.PublicEncryptionFactory;
import com.dotmarketing.cms.inquiry.struts.InquiryForm;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.ClickstreamFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.user.factories.UserCommentsFactory;
import com.dotmarketing.portlets.user.model.UserComment;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.Address;
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
/**
 * Please do not create any more "Contact Us" like methods.  
 * Use this class instead, unless we have a special case.
 */
public class InquiryAction extends DispatchAction {
	public ActionForward unspecified(ActionMapping mapping, ActionForm lf, HttpServletRequest request, HttpServletResponse response) throws Exception {


		ActionForward af = (mapping.findForward("inquiryPage"));

		return af;

	}

	public ActionForward inquiry(ActionMapping mapping, ActionForm lf, HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			// Start the transaction
			DotHibernate.startTransaction();
			InquiryForm form = (InquiryForm) lf;

			ActionErrors ae = form.validate(mapping, request);
			if ((ae != null) && (ae.size() > 0)) {
				saveMessages(request, ae);
				ActionForward af = (mapping.findForward("inquiryPage"));
				return af;
			}

			createAccount(form, request);
			//sendEmail(form, request);

			// Committ the transaction
			DotHibernate.commitTransaction();

			ActionMessages am = new ActionMessages();
			am.add(Globals.ERROR_KEY, new ActionMessage("message.contactUsThankYou"));
			saveMessages(request, am);

			ActionForward af = (mapping.findForward("thankYouPage"));
			return af;
		} catch (Exception ex) {
			Logger.error(this, ex.toString());
			DotHibernate.rollbackTransaction();

			ActionMessages am = new ActionMessages();
			am.add(Globals.ERROR_KEY, new ActionMessage("message.contactUsError"));
			saveMessages(request, am);

			ActionForward af = (mapping.findForward("inquiryPage"));
			return af;

		}
	}

	
	private void createAccount(InquiryForm form, HttpServletRequest request) throws NoSuchUserException, DotDataException, DotSecurityException {

		User user = APILocator.getUserAPI().loadByUserByEmail(form.getEmail(), APILocator.getUserAPI().getSystemUser(), false);
		User defaultUser = APILocator.getUserAPI().getDefaultUser();
		Date today = new Date();

		if (user.isNew() || (!user.isNew() && user.getLastLoginDate() == null)) {
			// ### CREATE USER ###
			Company company = PublicCompanyFactory.getDefaultCompany();
			user.setEmailAddress(form.getEmail().trim().toLowerCase());
			user.setFirstName(UtilMethods.webifyString(form.getFirstName()));
			user.setMiddleName(form.getMiddleName() == null ? "" : form.getMiddleName());
			user.setLastName(form.getLastName() == null ? "" : form.getLastName());
			user.setNickName("");
			user.setCompanyId(company.getCompanyId());
			user.setPasswordEncrypted(true);
			user.setComments(form.getComments());
			user.setGreeting("Welcome, " + user.getFullName() + "!");

			// Set defaults values
			if (user.isNew()) {
				String pass = PublicEncryptionFactory.getRandomPassword();
				form.setPassword(pass);
				user.setPassword(PublicEncryptionFactory.digestString(form.getPassword()));
				user.setLanguageId(defaultUser.getLanguageId());
				user.setTimeZoneId(defaultUser.getTimeZoneId());
				user.setSkinId(defaultUser.getSkinId());
				user.setDottedSkins(defaultUser.isDottedSkins());
				user.setRoundedSkins(defaultUser.isRoundedSkins());
				user.setResolution(defaultUser.getResolution());
				user.setRefreshRate(defaultUser.getRefreshRate());
				user.setLayoutIds("");
				user.setActive(true);
				user.setCreateDate(today);
			}
			APILocator.getUserAPI().save(user,APILocator.getUserAPI().getSystemUser(),false);
			// ### END CREATE USER ###

			// ### CREATE USER_PROXY ###
			UserProxy userProxy = com.dotmarketing.business.APILocator.getUserProxyAPI().getUserProxy(user.getUserId(),APILocator.getUserAPI().getSystemUser(), false);
			userProxy.setPrefix("");
			userProxy.setTitle(form.getTitle());
			userProxy.setOrganization(form.getOrganization());
			userProxy.setUserId(user.getUserId());
			com.dotmarketing.business.APILocator.getUserProxyAPI().saveUserProxy(userProxy,APILocator.getUserAPI().getSystemUser(), false);
			// ### END CRETE USER_PROXY ###

			// get the old categories, wipe them out
			/*
			 * List<Category> categories =
			 * InodeFactory.getParentsOfClass(userProxy, Category.class); for
			 * (int i = 0; i < categories.size(); i++) {
			 * categories.get(i).deleteChild(userProxy); }
			 */
			
			
			// Save the new categories
			String[] arr = form.getCategories();
			if (arr != null) {
				for (int i = 0; i < arr.length; i++) {
					Category node = (Category) InodeFactory.getInode(arr[i], Category.class);
					node.addChild(userProxy);
				}
			}

			// ### CREATE ADDRESS ###
			try {
				List<Address> addresses = PublicAddressFactory.getAddressesByUserId(user.getUserId());
				Address address = (addresses.size() > 0 ? addresses.get(0) : PublicAddressFactory.getInstance());
				address.setStreet1(form.getAddress1() == null ? "" : form.getAddress1());
				address.setStreet2(form.getAddress2() == null ? "" : form.getAddress2());
				address.setCity(form.getCity() == null ? "" : form.getCity());
				address.setState(form.getState() == null ? "" : form.getState());
				address.setZip(form.getZip() == null ? "" : form.getZip());
				// address.setPhone(createAccountForm.getPhone());
				String phone = form.getPhone();
				address.setPhone(phone == null ? "" : phone);
				address.setUserId(user.getUserId());
				address.setCompanyId(company.getCompanyId());
				PublicAddressFactory.save(address);
			} catch (Exception ex) {
				
				Logger.error(this,ex.getMessage(),ex);
			}

			Role defaultRole = com.dotmarketing.business.APILocator.getRoleAPI().loadRoleByKey(Config.getStringProperty("CMS_VIEWER_ROLE"));
			String roleId = defaultRole.getId();
			if (InodeUtils.isSet(roleId)) {
				com.dotmarketing.business.APILocator.getRoleAPI().addRoleToUser(roleId, user);
			}
		}
		// ### END CREATE ADDRESS ###

		// ### BUILD THE USER COMMENT ###
		if (UtilMethods.isSet(form.getComments())) {
			UserComment comment = new UserComment();

			comment.setComment(form.getMessage());
			comment.setDate(today);
			comment.setMethod(UserComment.METHOD_WEB);
			comment.setType(UserComment.TYPE_INCOMING);
			comment.setSubject("User Message");
			comment.setCommentUserId(user.getUserId());
			comment.setUserId(user.getUserId());
			comment.setCommunicationId(null);
			UserCommentsFactory.saveUserComment(comment);
		}

		// ### END BUILD THE USER COMMENT ###

		/* associate user with their clickstream request */
		if(Config.getBooleanProperty("ENABLE_CLICKSTREAM_TRACKING", false)){
			ClickstreamFactory.setClickStreamUser(user.getUserId(), request);
		}

	}

}