package com.dotmarketing.cms.registration.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.cms.registration.struts.RegistrationForm;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.organization.factories.OrganizationFactory;
import com.dotmarketing.portlets.organization.model.Organization;
import com.dotmarketing.util.InodeUtils;

/**
 * 
 * @author Oswaldo
 *
 */
public class AddFacilityAction extends DispatchAction
{
    public ActionForward unspecified(ActionMapping mapping, ActionForm lf, HttpServletRequest request,HttpServletResponse response) 
    throws Exception 
    {
        RegistrationForm registrationForm = (RegistrationForm) lf;
        String organizationInode = registrationForm.getOrganizationInodeAux();
        Organization organization = OrganizationFactory.getOrganization(organizationInode);
        //Copy the variable from the bean to the form
        String title = organization.getTitle();
        String street1 = organization.getStreet1();
        String street2 = organization.getStreet2();
        String city = organization.getCity();      
        String state = organization.getState();
        String zip = organization.getZip();
        String phone = organization.getPhone();
        String fax = organization.getFax();
        organizationInode = organization.getInode();
        //Set the variable in the form;
        registrationForm.setOrganizationTitle(title);
        registrationForm.setOrganizationStreet1(street1);
        registrationForm.setOrganizationStreet2(street2);
        registrationForm.setOrganizationCity(city);
        registrationForm.setOrganizationState(state);
        registrationForm.setOrganizationZip(zip);
        registrationForm.setOrganizationPhone(phone);
        registrationForm.setOrganizationFax(fax);
        registrationForm.setOrganizationInodeAux(organizationInode);
        
        //Action Forward        
        return mapping.findForward("open");        
    }
    
    public ActionForward saveOrganization(ActionMapping mapping, ActionForm lf, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        //Made the casting to the actualy form class
        RegistrationForm registrationForm = (RegistrationForm) lf;
        //Validate form
        //Validate if the user fill the password and confirm password
        ActionErrors ae;
        ae = registrationForm.validateOrganization();
       
        if ((ae != null) && (ae.size() > 0)) 
        {
            saveMessages(request, ae);
            ActionForward af = mapping.findForward("fail");
            return af;
        }
        //End Validate form

        //Save the Organization in the DB
        //Copy the variable from the bean to the form
        String title = registrationForm.getOrganizationTitle();
        String street1 = registrationForm.getOrganizationStreet1();
        String street2 = registrationForm.getOrganizationStreet2();
        String city = registrationForm.getOrganizationCity();      
        String state = registrationForm.getOrganizationState();
        String zip = registrationForm.getOrganizationZip();
        String phone = registrationForm.getOrganizationPhone();
        String fax = registrationForm.getOrganizationFax();
        String organizationInode = registrationForm.getOrganizationInodeAux();
        //Load the organization
        Organization organization = OrganizationFactory.getOrganization(organizationInode);
        //Set the variable in the DAO;
        organization.setTitle(title);
        organization.setStreet1(street1);
        organization.setStreet2(street2);
        organization.setCity(city);
        organization.setState(state);
        organization.setZip(zip);
        organization.setPhone(phone);
        organization.setFax(fax);
        //Save the otganization in the DB
        OrganizationFactory.saveOrganization(organization);
        registrationForm.setOrganizationInodeAux(organization.getInode());
        
        //Delete the old categories
        if (InodeUtils.isSet(organization.getInode()))
		{			
			List<Category> categories = InodeFactory.getParentsOfClass(organization,Category.class);			
			for(int i = 0;i < categories.size();i++)
			{
				categories.get(i).deleteChild(organization);
			}			
		}

        //Save the new categories
		String[] arr = registrationForm.getCategory();
		if (arr != null) 
		{
			for (int i = 0; i < arr.length; i++) 
			{
				Category node = (Category) InodeFactory.getInode(arr[i],Category.class);
				node.addChild(organization);
			}
		}              
        return mapping.findForward("success");
    }
}
