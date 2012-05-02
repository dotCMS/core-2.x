package com.dotmarketing.cms.inquiry.struts;


import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.FormSpamFilter;
import com.dotmarketing.util.UtilMethods;



public class InquiryForm extends ValidatorForm {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    String password;
    String dispatch;
    boolean rememberMe;
    String password2;
    String firstName;
    String lastName;
    String middleName;
    String address1;
    String address2;
    String address3;
    String city;
    String country;
    String state;
    String phone;
    String[] categories;
    String zip;
    boolean alumni;
    String degree;
    String comments;
	/* Dot additions */
    String organization;
    String email; 
    String title;
    String message;
	String ssn;
	String status;
	String cid;
    boolean optOut;
	String totalText;
	String recurTotalText; 
	String delivery;	
	String enterpriseSupport;
	String hoursSupport;
	String[] organizationType; 

    private String website;
    private boolean mailSubscription;

	public String getDelivery() {
		return delivery;
	}

	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}

	public String getEnterpriseSupport() {
		return enterpriseSupport;
	}

	public void setEnterpriseSupport(String enterpriseSupport) {
		this.enterpriseSupport = enterpriseSupport;
	}

	public String getHoursSupport() {
		return hoursSupport;
	}

	public void setHoursSupport(String hoursSupport) {
		this.hoursSupport = hoursSupport;
	}

	public String getRecurTotalText() {
		return recurTotalText;
	}

	public void setRecurTotalText(String recurTotalText) {
		this.recurTotalText = recurTotalText;
	}

	public String getTotalText() {
		return totalText;
	}

	public void setTotalText(String totalText) {
		this.totalText = totalText;
	}	
    /**
     * @return Returns the address1.
     */
    public String getAddress1() {
        return this.address1;
    }
    /**
     * @param address1 The address1 to set.
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    /**
     * @return Returns the address2.
     */
    public String getAddress2() {
        return this.address2;
    }
    /**
     * @param address2 The address2 to set.
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    /**
     * @return Returns the address3.
     */
    public String getAddress3() {
        return this.address3;
    }
    /**
     * @param address3 The address3 to set.
     */
    public void setAddress3(String address3) {
        this.address3 = address3;
    }
    /**
     * @return Returns the alumni.
     */
    public boolean isAlumni() {
        return this.alumni;
    }
    /**
     * @param alumni The alumni to set.
     */
    public void setAlumni(boolean alumni) {
        this.alumni = alumni;
    }
    /**
     * @return Returns the city.
     */
    public String getCity() {
        return this.city;
    }
    /**
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }
    /**
     * @return Returns the comments.
     */
    public String getComments() {
        return this.comments;
    }
    /**
     * @param comments The comments to set.
     */
    public void setComments(String comments) {
        this.comments = comments;
    }
  
    /**
     * @return Returns the degree.
     */
    public String getDegree() {
        return this.degree;
    }
    /**
     * @param degree The degree to set.
     */
    public void setDegree(String degree) {
        this.degree = degree;
    }

    /**
     * @return Returns the firstName.
     */
    public String getFirstName() {
        return this.firstName;
    }
    /**
     * @param firstName The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * @return Returns the howHeard.
     */
    public String getHowHeard() {
        return this.howHeard;
    }
    /**
     * @param howHeard The howHeard to set.
     */
    public void setHowHeard(String howHeard) {
        this.howHeard = howHeard;
    }

  
    /**
     * @return Returns the lastName.
     */
    public String getLastName() {
        return this.lastName;
    }
    /**
     * @param lastName The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * @return Returns the middleName.
     */
    public String getMiddleName() {
        return this.middleName;
    }
    /**
     * @param middleName The middleName to set.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }
    /**
     * @return Returns the password2.
     */
    public String getPassword2() {
        return this.password2;
    }
    /**
     * @param password2 The password2 to set.
     */
    public void setPassword2(String password2) {
        this.password2 = password2;
    }
    /**
     * @return Returns the phone.
     */
    public String getPhone() {
        return this.phone;
    }
    /**
     * @param phone The phone to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    /**
     * @return Returns the state.
     */
    public String getState() {
        return this.state;
    }
    /**
     * @param state The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }
    /**
     * @return Returns the zip.
     */
    public String getZip() {
        return this.zip;
    }
    /**
     * @param zip The zip to set.
     */
    public void setZip(String zip) {
        this.zip = zip;
    }
    String howHeard;
    

	/** default constructor */
    public InquiryForm() {
    }

	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {          
        ActionErrors errors = new ActionErrors();             	
      	if (!UtilMethods.isSet(email))
      	{
      		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("message.contentlet.required","eMail"));    		
      	}
      	if (!UtilMethods.isSet(firstName)) 
      	{    		
      		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("message.contentlet.required","First Name"));    		
      	}
      	if (!UtilMethods.isSet(lastName)) 
      	{
      		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("message.contentlet.required","Last Name"));
      	}
      	if (!UtilMethods.isSet(organization)) 
      	{
      		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("message.contentlet.required","Organization"));
      	}
      	
      	if(FormSpamFilter.isSpamRequest(request)){
     		errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("Potential Spam Message"));
      	}
      	
      	return errors;

    }

	

    /**
     * @return Returns the rememberMe.
     */
    public boolean isRememberMe() {
        return this.rememberMe;
    }
    /**
     * @param rememberMe The rememberMe to set.
     */
    public void setRememberMe(boolean rememberMe) {
        this.rememberMe = rememberMe;
    }
    /**
     * @return Returns the dispatch.
     */
    public String getDispatch() {
        return this.dispatch;
    }
    /**
     * @param dispatch The dispatch to set.
     */
    public void setDispatch(String dispatch) {
        this.dispatch = dispatch;
    }
    /**
     * @return Returns the password.
     */
    public String getPassword() {
        return this.password;
    }
    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
        return this.country;
    }
    /**
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }
    /**
     * @return Returns the cid.
     */
    public String getCid() {
        return this.cid;
    }
    /**
     * @param cid The cid to set.
     */
    public void setCid(String cid) {
        this.cid = cid;
    }
    /**
     * @return Returns the ssn.
     */
    public String getSsn() {
        return this.ssn;
    }
    /**
     * @param ssn The ssn to set.
     */
    public void setSsn(String ssn) {
        this.ssn = ssn;
    }
    /**
     * @return Returns the status.
     */
    public String getStatus() {
        return this.status;
    }
    /**
     * @param status The status to set.
     */
    public void setStatus(String status) {
        this.status = status;
    }
  

    public boolean isOptOut() {
        return this.optOut;
    }
    public void setOptOut(boolean optOut) {
        this.optOut = optOut;
    }
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String[] getCategories() {
		return categories;
	}

	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	public void setCategories(String categories) {
		if(categories !=null){
			this.categories = categories.split(",");
		}
		

	}

	public String[] getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(String[] organizationType) {
		this.organizationType = organizationType;
	}
	
	
}
