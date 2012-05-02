package com.dotmarketing.portlets.banners.struts;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.validator.ValidatorForm;

import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.liferay.portal.util.Constants;

public class BannerForm extends ValidatorForm {

    private String[] categories;

    /** identifier field */
    private String inode;

	/** identifier field */
    private String parent;

    /** nullable persistent field */
    private String title;

	/** nullable persistent field */
    private String caption;
    
    /** nullable persistent field */
    private String imageFile;

    /** nullable persistent field */
    private int imageWidth;

    /** nullable persistent field */
    private int imageHeight;

    /** nullable persistent field */
    private String altText;

    /** nullable persistent field */
    private boolean newWindow;

    /** nullable persistent field */
    private String link;

    /** nullable persistent field */
    private java.util.Date startDate;

    /** nullable persistent field */
    private java.util.Date endDate;

    /** nullable persistent field */
    private String webStartDate;

    /** nullable persistent field */
    private String webEndDate;
    
    /** nullable persistent field */
    private String body;

    /** nullable persistent field */
    private boolean active;

    /** nullable persistent field */
	private String selectedimage;

    /** nullable persistent field */
	private String imageExtension;

	/** nullable persistent field */
	private String image;

    /** nullable persistent field */
    private String selectedparent;

    /** nullable persistent field */
    private String selectedparentPath;

	/** nullable persistent field */
	private String htmlpage;

    /** nullable persistent field */
    private String selectedhtmlpage;
	
	/** default constructor */
    public BannerForm() {
    }

    public String getInode() {
    	if(InodeUtils.isSet(this.inode))
    		return this.inode;
    	
    	return "";
    }

    public void setInode(String inode) {
        this.inode = inode;
    }
    public java.lang.String getTitle() {
        return this.title;
    }

    public void setTitle(java.lang.String title) {
        this.title = title;
    }
    public java.lang.String getImageFile() {
        return this.imageFile;
    }

    public void setImageFile(java.lang.String imageFile) {
        this.imageFile = imageFile;
    }
    public int getImageWidth() {
        return this.imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }
    public int getImageHeight() {
        return this.imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
    public java.lang.String getAltText() {
        return this.altText;
    }

    public void setAltText(java.lang.String altText) {
        this.altText = altText;
    }
    public java.lang.String getLink() {
        return this.link;
    }

    public void setLink(java.lang.String link) {
        this.link = link;
    }
    public java.util.Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }
    public java.util.Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }
    public java.lang.String getBody() {
        return this.body;
    }

    public void setBody(java.lang.String body) {
        this.body = body;
    }
	/*public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if ("save".equals(dispatch)) {
            return super.validate(mapping, request);
        }

        return null;
    }*/
	/**
	 * Returns the webEndDate.
	 * @return String
	 */
	public String getWebEndDate() {
		return webEndDate;
	}

	/**
	 * Returns the webStartDate.
	 * @return String
	 */
	public String getWebStartDate() {
		return webStartDate;
	}

	/**
	 * Sets the webEndDate.
	 * @param webEndDate The webEndDate to set
	 */
	public void setWebEndDate(String webEndDate) {
		this.webEndDate = webEndDate;
		try {
			this.endDate = new SimpleDateFormat("MM/dd/yyyy").parse(webEndDate);			
		} catch(ParseException ex) {
		}
	}

	/**
	 * Sets the webStartDate.
	 * @param webStartDate The webStartDate to set
	 */
	public void setWebStartDate(String webStartDate) {
		this.webStartDate = webStartDate;
		try {
			this.startDate = new SimpleDateFormat("MM/dd/yyyy").parse(webStartDate);			
		} catch(ParseException ex) {
		}		
	}

	/**
	 * Returns the active.
	 * @return boolean
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Returns the newWindow.
	 * @return boolean
	 */
	public boolean isNewWindow() {
		return newWindow;
	}

	/**
	 * Sets the active.
	 * @param active The active to set
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Sets the newWindow.
	 * @param newWindow The newWindow to set
	 */
	public void setNewWindow(boolean newWindow) {
		this.newWindow = newWindow;
	}

	/**
	 * Returns the categories.
	 * @return String[]
	 */
	public String[] getCategories() {
		return categories;
	}

	/**
	 * Sets the categories.
	 * @param categories The categories to set
	 */
	public void setCategories(String[] categories) {
		this.categories = categories;
	}
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        if(request.getParameter("cmd")!=null && request.getParameter("cmd").equals(Constants.ADD)) {
            Logger.debug(this, "Banner validation!!!!!!" + mapping.getForward());
            return super.validate(mapping, request);
        }
        return null;
    }
	/**
	 * Returns the caption.
	 * @return String
	 */
	public String getCaption() {
		return caption;
	}

	/**
	 * Sets the caption.
	 * @param caption The caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	
	
	
	/**
	 * @return Returns the image.
	 */
	public String getImage() {
		return image;
	}
	/**
	 * @param image The image to set.
	 */
	public void setImage(String image) {
		this.image = image;
	}
	/**
	 * @return Returns the selectedimage.
	 */
	public String getSelectedimage() {
		return selectedimage;
	}
	/**
	 * @param selectedimage The selectedimage to set.
	 */
	public void setSelectedimage(String selectedimage) {
		this.selectedimage = selectedimage;
	}
	
	
	/**
	 * @return Returns the imageExtension.
	 */
	public String getImageExtension() {
		return imageExtension;
	}
	/**
	 * @param imageExtension The imageExtension to set.
	 */
	public void setImageExtension(String imageExtension) {
		this.imageExtension = imageExtension;
	}
	
	/**
	 * @return Returns the parent.
	 */
	public String getParent() {
		return parent;
	}
	/**
	 * @param parent The parent to set.
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}
	/**
	 * @return Returns the selectedparent.
	 */
	public String getSelectedparent() {
		return selectedparent;
	}
	/**
	 * @param selectedparent The selectedparent to set.
	 */
	public void setSelectedparent(String selectedparent) {
		this.selectedparent = selectedparent;
	}
	/**
	 * @return Returns the selectedparentPath.
	 */
	public String getSelectedparentPath() {
		return selectedparentPath;
	}
	/**
	 * @param selectedparentPath The selectedparentPath to set.
	 */
	public void setSelectedparentPath(String selectedparentPath) {
		this.selectedparentPath = selectedparentPath;
	}
	
	/**
	 * @return Returns the htmlpage.
	 */
	public String getHtmlpage() {
		return htmlpage;
	}
	/**
	 * @param htmlpage The htmlpage to set.
	 */
	public void setHtmlpage(String htmlpage) {
		this.htmlpage = htmlpage;
	}
	/**
	 * @return Returns the selectedhtmlpage.
	 */
	public String getSelectedhtmlpage() {
		return selectedhtmlpage;
	}
	/**
	 * @param selectedhtmlpage The selectedhtmlpage to set.
	 */
	public void setSelectedhtmlpage(String selectedhtmlpage) {
		this.selectedhtmlpage = selectedhtmlpage;
	}
}
