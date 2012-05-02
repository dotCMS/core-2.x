package com.dotmarketing.portlets.banners.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.util.InodeUtils;

/** @author Hibernate CodeGenerator */
public class Banner extends Inode implements Serializable {

	private String[] categories;

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
    private String body;

    /** nullable persistent field */
    private boolean active;

    /** nullable persistent field */
    private int nmbrViews;

    /** nullable persistent field */
    private int nmbrClicks;

	/** nullable persistent field */
	private String image;

	/** nullable persistent field */
	private String htmlpage;
	
	/** nullable persistent field */
	private String path;
	
	/** nullable persistent field */
	private String placement;

	
    /** default constructor */
    public Banner() {
    	this.setType("Banner");
    	startDate = new java.util.Date();
    	endDate = new java.util.Date();
    }

	/**
	 * @return Returns the path.
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path The path to set.
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/**
	 * @return Returns the placement.
	 */
	public String getPlacement() {
		return placement;
	}
	/**
	 * @param placement The placement to set.
	 */
	public void setPlacement(String placement) {
		this.placement = placement;
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


    public int getNmbrViews() {
        return this.nmbrViews;
    }

    public void setNmbrViews(int nmbrViews) {
        this.nmbrViews = nmbrViews;
    }
    public int getNmbrClicks() {
        return this.nmbrClicks;
    }

    public void setNmbrClicks(int nmbrClicks) {
        this.nmbrClicks = nmbrClicks;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
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
	 * Returns the inode.
	 * @return String
	 */
	public String getInode() {
		if(InodeUtils.isSet(inode))
			return inode;
		
		return "";
	}

	/**
	 * Sets the inode.
	 * @param inode The inode to set
	 */
	public void setInode(String inode) {
		this.inode = inode;
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
}
