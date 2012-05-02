package com.dotmarketing.menubuilders;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Inode;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.portlets.folders.factories.FolderFactory;
import com.dotmarketing.portlets.folders.model.Folder;
import com.dotmarketing.portlets.htmlpages.model.HTMLPage;
import com.dotmarketing.portlets.links.model.Link;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.ConfigUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;

public class TopLinksMenuBuilder implements ViewTool {
	protected HttpServletRequest request;
    protected HttpServletResponse response;
    
    private static String velocityRootPath = ConfigUtils.getDynamicVelocityPath() + java.io.File.separator;
	private static String MENU_VTL_PATH = velocityRootPath + "menus" + java.io.File.separator;
	
	/**
     * Initializes this instance for the current request.
     * 
     */
    public void init(Object obj) {
        ViewContext context = (ViewContext) obj;
        this.request = context.getRequest();
        this.response = context.getResponse();
    }
    public String createMenu(Host host) {
    	return createMenu(host.getIdentifier());
    }

    public String createMenu(String hostId) {
    	
	   	Logger.debug(HorizontalMenuBuilder.class, "\n\n\nTopLinksMenuBuilder begins");

		java.util.List itemsList = new ArrayList();
		String folderPath = "";
		String fileName = "";
		boolean fileExists = true;
		
		StringBuffer stringbuf = new StringBuffer();
	   	
	   	java.io.File fileFolder = new java.io.File(MENU_VTL_PATH);
	   	if(!fileFolder.exists()){
	   	    fileFolder.mkdirs(); 
	   	}
	   	java.io.File file = null;
	   	
   		String condition = "show_on_menu = " +  DbConnectionFactory.getDBTrue();
		fileName = hostId + "_toplinks.vtl";
		file  = new java.io.File(MENU_VTL_PATH + java.io.File.separator + fileName);
		if (!file.exists()) {
			itemsList = FolderFactory.getFoldersByParentAndCondition(hostId,condition);
			fileExists = false;
		}
		String filePath = "dynamic" + java.io.File.separator + "menus" + java.io.File.separator + fileName;
	   	
	   	if (fileExists) {
	   		return filePath;
	   	}
	   	
		//gets menu items for this folder
		java.util.Iterator itemListIterator = itemsList.iterator();
	   	
		///FIRST LEVEL MENU ITEMS!!!!
		while (itemListIterator.hasNext()) {

			Inode itemChild = (Inode) itemListIterator.next();
			
			if (itemChild instanceof Folder) {
				stringbuf.append("#set ($VTLSERVLET_DECODED_URI=\"$UtilMethods.decodeURL($VTLSERVLET_URI)\")\n");
                stringbuf.append("#if ($UtilMethods.inString($VTLSERVLET_DECODED_URI,\""  + ((Folder)itemChild).getPath() + "\"))\n");
				stringbuf.append("<a class=\"active\" ");
                stringbuf.append("#else\n");
				stringbuf.append("<a ");
                stringbuf.append("#end \n");
				stringbuf.append("href=\"" + UtilMethods.encodeURIComponent(((Folder)itemChild).getPath()) + "index." + Config.getStringProperty("VELOCITY_PAGE_EXTENSION") + "\">"); 
				stringbuf.append(((Folder)itemChild).getTitle());
				stringbuf.append("</a>");
			}
			else if (itemChild instanceof Link) {
				if (((Link)itemChild).isWorking() && !((Link)itemChild).isDeleted()) {
					stringbuf.append("#set ($VTLSERVLET_DECODED_URI=\"$UtilMethods.decodeURL($VTLSERVLET_URI)\")\n");
                    stringbuf.append("#if ($UtilMethods.inString($VTLSERVLET_DECODED_URI,\""  +  ((Link)itemChild).getProtocal() + ((Link)itemChild).getUrl() + "\"))\n");
					stringbuf.append("<a class=\"active\" ");
                    stringbuf.append("#else\n");
					stringbuf.append("<a ");
                    stringbuf.append("#end \n");
					stringbuf.append("href=\"" + ((Link)itemChild).getProtocal() + UtilMethods.encodeURIComponent(((Link)itemChild).getUrl()) + "\" target=\"" + ((Link)itemChild).getTarget() + "\">\n");
					stringbuf.append(((Link)itemChild).getTitle() + "</a>");
				}
			}
			else if (itemChild instanceof HTMLPage) {
				if (((HTMLPage)itemChild).isWorking() && !((HTMLPage)itemChild).isDeleted()) {
					stringbuf.append("#set ($VTLSERVLET_DECODED_URI=\"$UtilMethods.decodeURL($VTLSERVLET_URI)\")\n");
                    stringbuf.append("#if ($UtilMethods.inString($VTLSERVLET_DECODED_URI,\"" + folderPath + ((HTMLPage)itemChild).getPageUrl() + "\"))\n");
					stringbuf.append("<a class=\"active\" ");
                    stringbuf.append("#else\n");
					stringbuf.append("<a ");
                    stringbuf.append("#end \n");
					stringbuf.append("href=\"" + folderPath + ((HTMLPage)itemChild).getPageUrl() + "\">\n");
					stringbuf.append(((HTMLPage)itemChild).getTitle() + "</a>");
				}
			}
			else if (itemChild instanceof File) {
				if (((File)itemChild).isWorking() && !((File)itemChild).isDeleted()) {
					stringbuf.append("#set ($VTLSERVLET_DECODED_URI=\"$UtilMethods.decodeURL($VTLSERVLET_URI)\")\n");
                    stringbuf.append("#if ($UtilMethods.inString($VTLSERVLET_DECODED_URI,\""  + folderPath + ((File)itemChild).getFileName() + "\"))\n");
					stringbuf.append("<a class=\"active\" ");
                    stringbuf.append("#else\n");
					stringbuf.append("<a ");
                    stringbuf.append("#end \n");
					stringbuf.append("href=\"" + UtilMethods.encodeURIComponent(folderPath + ((File)itemChild).getFileName()) + "\">\n");
					stringbuf.append(((File)itemChild).getTitle() + "</a>");
				}
			}				
			if (itemListIterator.hasNext()) {
				stringbuf.append(" &nbsp;|&nbsp;\n");
			}
		}

		try {
			
			if (stringbuf.length() > 0) {
				// Specifying explicitly a proper character set encoding
				FileOutputStream fo = new FileOutputStream(file);
				OutputStreamWriter out = new OutputStreamWriter(fo, UtilMethods.getCharsetConfiguration());
	            out.write(stringbuf.toString());
	            out.flush();
	            out.close();

				fo.close();
			}
			else {
		        Logger.debug(HorizontalMenuBuilder.class, "Error creating static top links menu!!!!!");
			}

		}
		catch (Exception fe) {
			return "";
		}
        Logger.debug(HorizontalMenuBuilder.class, "End of HorizontalMenuBuilder" + filePath);
        return filePath;
    }
    
}
