package com.dotmarketing.servlets.image;

import static com.dotmarketing.business.PermissionAPI.PERMISSION_READ;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.FastDateFormat;

import sun.awt.image.codec.JPEGImageEncoderImpl;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.DotIdentifierStateException;
import com.dotmarketing.business.PermissionAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.cache.FileCache;
import com.dotmarketing.cache.IdentifierCache;
import com.dotmarketing.cache.LiveCache;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.portlets.files.factories.FileFactory;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Constants;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys;
import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.User;
import com.sun.image.codec.jpeg.JPEGEncodeParam;

/**
 * This servlet resize an image proportionally without placing that image into a
 * box background. The image generated is with the .png extension
 * 
 * @author Oswaldo
 * 
 */
public class ResizeImageServlet extends HttpServlet {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private static PermissionAPI permissionAPI = APILocator.getPermissionAPI();

    /* This is a thread safe date formatter */
    FastDateFormat df = FastDateFormat.getInstance(Constants.RFC2822_FORMAT, TimeZone.getTimeZone("GMT"), Locale.US);
    /**
     * resize an image proportionally without placing that image into a box
     * background. The image generated is with the .png extension
     */
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        long time = System.currentTimeMillis();
        
        HttpSession session = request.getSession(false);
		User user = null;
		try {
			if (session != null)
				user = (com.liferay.portal.model.User) session.getAttribute(com.dotmarketing.util.WebKeys.CMS_USER);
		} catch (Exception nsue) {
			Logger.warn(this, "Exception trying to getUser: " + nsue.getMessage(), nsue);
		}

        // getFile Inode (inode param)
        String inode = request.getParameter("inode");
        String identifier = request.getParameter("id");
        Identifier ident = null;
        
        if( InodeUtils.isSet(identifier) ) {
        	// If identifier was given, get the live inode from it
			try{
				ident = IdentifierCache.getIdentifierFromIdentifierCache(identifier);
				String path = LiveCache.getPathFromCache(ident.getURI(), ident.getHostId());
        		inode = UtilMethods.getFileName(path);
			}catch(Exception ex){
				Logger.debug(ResizeImageServlet.class, "Identifier not found going to try as a File Asset");
				inode = identifier;
			}
        } else if( InodeUtils.isSet(inode) ) {
        	//Testing if the inode is really an identifier or an inode, for backward compatibility 
        	//this code tries to search on both caches identifiers and file before hit database
        	//DOTCMS-1619
        	try {
				ident = IdentifierCache.loadFromCacheOnly(inode);
	        	if(ident != null && (InodeUtils.isSet(ident.getInode()))) {
	        		//it's an identifier
    				String path = LiveCache.getPathFromCache(ident.getURI(), ident.getHostId());
            		inode = UtilMethods.getFileName(path);
	        	} else {
	        		//it might be an inode
	                File file = (File) FileCache.getFileByInode(inode);
	                if(file == null || (!InodeUtils.isSet(file.getInode()))) {
	                	//Finally it's not a file and we did the first round trip to database 
	                	//then we do the second round trip to database to find the identifier,
	                	//but after this the next hit will find it cached.
	                	ident = IdentifierCache.getIdentifierFromIdentifierCache(inode);
	                	if(ident != null && (InodeUtils.isSet(ident.getInode())) ) {
	        				String path = LiveCache.getPathFromCache(ident.getURI(), ident.getHostId());
	                		inode = UtilMethods.getFileName(path);
	                	}
	                }
	        	}
			} catch (DotIdentifierStateException e) {
			} catch (NumberFormatException e) {
			} catch (DotDataException e) {
			}
        	
        } else {
            String url = request.getParameter("url");
            if(url == null || !UtilMethods.isSet(url))
                url = request.getParameter("path");
            	
            if(UtilMethods.isSet(url)) {
                //If path is the dotasset portlet 
                if(url.startsWith("/dotAsset")) {
                	
            		StringTokenizer _st = new StringTokenizer(url, "/");
            		String _fileName = null;
            		while(_st.hasMoreElements()){
            			_fileName = _st.nextToken();
            		}
                    inode = UtilMethods.getFileName(_fileName); // Sets the identifier
                    
        			try{
        				ident = IdentifierCache.getIdentifierFromIdentifierCache(inode);
        				String path = LiveCache.getPathFromCache(ident.getURI(), ident.getHostId());
                		inode = UtilMethods.getFileName(path);
        			}catch(Exception ex){
        				Logger.debug(ResizeImageServlet.class, "Identifier not found going to try as a File Asset");
        			}
                
                } else { 
            		//If it's a regular path
                	Host currentHost;
    				try {
    					currentHost = WebAPILocator.getHostWebAPI().getCurrentHost(request);
    				} catch (PortalException e) {
    					Logger.error(ResizeImageServlet.class, e.getMessage(), e);
    					throw new ServletException(e.getMessage(), e);
    				} catch (SystemException e) {
    					Logger.error(ResizeImageServlet.class, e.getMessage(), e);
    					throw new ServletException(e.getMessage(), e);
    				} catch (DotDataException e) {
    					Logger.error(ResizeImageServlet.class, e.getMessage(), e);
    					throw new ServletException(e.getMessage(), e);
    				} catch (DotSecurityException e) {
    					Logger.error(ResizeImageServlet.class, e.getMessage(), e);
    					throw new ServletException(e.getMessage(), e);
    				}
                	String path = LiveCache.getPathFromCache(url, currentHost);
                	inode = UtilMethods.getFileName(path);
                }            	
            } else {
            	response.sendError(404);
            	return;
            }


        }

        try {
            if (!InodeUtils.isSet(inode)) {
                response.sendError(404);
                return;
            }

        } catch (NumberFormatException e) {
            Logger.error(this, "service: invalid inode = " + inode + " given to the service.");
            response.sendError(404);
            return;
        }
        
        try {
			//Checking permissions
        	/**
        	 * Build a fake proxy file object so we
        	 * can get inheritable permissions on it
        	 * without having to hit cache or db
        	 */
        	
            com.dotmarketing.portlets.files.model.File fProxy = new com.dotmarketing.portlets.files.model.File();
            if(ident!=null && UtilMethods.isSet(ident.getInode())){
            	fProxy.setIdentifier(ident.getInode());
            }
            else{
            	fProxy.setIdentifier(inode);
            }      

            if (!permissionAPI.doesUserHavePermission(fProxy, PERMISSION_READ, user)) {
            	if(user == null)
            		//Sending user to unauthorized the might send him to login
            		response.sendError(401, "The requested file is unauthorized");
            	else
            		//sending the user to forbidden
            		response.sendError(403, "The requested file is forbidden");
           		return;
            }
		} catch (DotDataException e1) {
			Logger.error(this,e1.getMessage());
			response.sendError(500,e1.getMessage());
			return;
		}



        String h = request.getParameter("h");
        String w = request.getParameter("w");

        String maxh = request.getParameter("maxh");
        String maxw = request.getParameter("maxw");

        int height = Config.getIntProperty("DEFAULT_HEIGHT");
        int width = Config.getIntProperty("DEFAULT_WIDTH");
        int imageWidth = 0;
        int imageHeight = 0;
        double imageRatio = 0;

        try {

            if (inode != null && inode.length() > 0 && InodeUtils.isSet(inode)) {

                File file = (File) FileCache.getFileByInode(inode);

                if (InodeUtils.isSet(file.getInode())) {
                    // Set the size
                    if (UtilMethods.isSet(h) || UtilMethods.isSet(w)) {

                        if (UtilMethods.isInt(h) && UtilMethods.isInt(w)) {

                            height = (Integer.parseInt(h) > 0 ? Integer.parseInt(h) : Config.getIntProperty("DEFAULT_HEIGHT"));
                            width = (Integer.parseInt(w) > 0 ? Integer.parseInt(w) : Config.getIntProperty("DEFAULT_WIDTH"));

                        } else if (UtilMethods.isInt(h)) {

                            height = (Integer.parseInt(h) > 0 ? Integer.parseInt(h) : Config.getIntProperty("DEFAULT_HEIGHT"));

                            // determine thumbnail size from WIDTH and HEIGHT
                            imageWidth = file.getWidth() == 0?width:file.getWidth();
                            imageHeight = file.getHeight() == 0?height:file.getHeight();

                            // JIRA: http://jira.dotmarketing.net/browse/DOTCMS-1340
                            width = (int) Math.ceil((imageWidth * height)/imageHeight);

                        } else if (UtilMethods.isInt(w)) {

                            width = (Integer.parseInt(w) > 0 ? Integer.parseInt(w) : Config.getIntProperty("DEFAULT_WIDTH"));

                            // determine thumbnail size from WIDTH and HEIGHT
                            imageWidth = file.getWidth() == 0?width:file.getWidth();
                            imageHeight = file.getHeight() == 0?height:file.getHeight();
                            
                            // JIRA: http://jira.dotmarketing.net/browse/DOTCMS-1340
                            height = (int) Math.ceil((imageHeight * width)/imageWidth);

                        }

                    } else if (UtilMethods.isSet(maxh) && UtilMethods.isSet(maxw)) {

                        int maxhint = Integer.parseInt(maxh);
                        int maxwint = Integer.parseInt(maxw);
                        int decrease = 0;

                        imageWidth = file.getWidth() == 0?width:file.getWidth();
                        imageHeight = file.getHeight() == 0?height:file.getHeight();

                        imageRatio = (double) imageWidth / (double) imageHeight;
                        boolean imageFinishied = true;

                        width = maxwint;
                        height = maxhint;

                        do {

                            if (width <= height) {

                                width = width - decrease;
                                height = (int) Math.ceil((width / imageRatio));

                            } else {

                                height = height - decrease;
                                width = (int) (height * imageRatio);
                            }

                            decrease = 1;

                            if (height <= maxhint && width <= maxwint) {
                                imageFinishied = false;
                            }

                        } while (imageFinishied);

                    }

                    // gets file extension
                    String suffix = file.getExtension();

                    // gets the real path to the assets directory
                    String filePath = FileFactory.getRealAssetsRootPath();

                    // creates the path where to save the working file based on
                    // the inode
                    String workingFileInodePath = String.valueOf(inode);
                    if (workingFileInodePath.length() == 1) {
                        workingFileInodePath = workingFileInodePath + "0";
                    }

                    // creates the path with inode{1} + inode{2}
                    workingFileInodePath = workingFileInodePath.substring(0, 1) + java.io.File.separator + workingFileInodePath.substring(1, 2);

                    String thumbExtension = WebKeys.GENERATED_FILE  +file.getInode() +  height  + "_w_" + width ;

                    // Set the new tumbnail path
                    String thumbnailFilePath = filePath + java.io.File.separator + workingFileInodePath + java.io.File.separator + thumbExtension + "." 
                            + suffix;

                    java.io.File thumbFile = null;
                    synchronized (inode.intern()) {
						
                        thumbFile = new java.io.File(thumbnailFilePath);
                        Logger.debug(this, "Checking resized image for " + thumbnailFilePath);
                        if (!thumbFile.exists() || (request.getParameter("nocache") != null)) {
                            Logger.debug(this, "File doesn't exists creating it");
                            com.dotmarketing.util.ImageResizeUtils.resizeImage(filePath + java.io.File.separator + workingFileInodePath
                                    + java.io.File.separator, inode, suffix, thumbExtension, width, height);
                            thumbFile = new java.io.File(thumbnailFilePath);
                            Logger.debug(this, "File created thumbFile.exists() = " + thumbFile.exists());
                        }

                    }
                    if(thumbFile ==null){
                    	response.sendError(404);
                    	return;
                    }
                    Logger.debug(this, "Streaming the image");
                    
                    
                    
                    //  -------- HTTP HEADER/ MODIFIED SINCE CODE -----------//
                    
                    long _lastModified = thumbFile.lastModified();
                    long _fileLength = thumbFile.length();
					String _eTag = "dot:" + inode + "-" + _lastModified/1000 + "-" + _fileLength;
                    String ifModifiedSince = request.getHeader("If-Modified-Since");
                    String ifNoneMatch = request.getHeader("If-None-Match");

                    /*
                     * If the etag matches then the file is the same
                     *
                    */
                    
                    if(ifNoneMatch != null){
                        if(_eTag.equals(ifNoneMatch) || ifNoneMatch.equals("*")){
                            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED );
                            thumbFile = null;
                            return;
                        }
                    }
                    /* Using the If-Modified-Since Header */
                    if(ifModifiedSince != null){
					    try{
					    	java.text.SimpleDateFormat httpDate = new java.text.SimpleDateFormat(Constants.RFC2822_FORMAT, Locale.US);
					    	httpDate.setTimeZone(TimeZone.getDefault());
					        Date ifModifiedSinceDate = httpDate.parse(ifModifiedSince);
					        
					        if(_lastModified <= ifModifiedSinceDate.getTime()){

					            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED );
					            thumbFile = null;
					            return;
					        }
					    }
					    catch(Exception e){}
					}

                    response.setHeader("Content-Length", String.valueOf(_fileLength));                   
                    response.setHeader("Last-Modified", df.format(_lastModified));
                    response.setHeader("ETag", "\"" + _eTag +"\"");
                    // Set the expiration time
                    GregorianCalendar expiration = new GregorianCalendar();
                    expiration.add(java.util.Calendar.YEAR, 1);
                    response.setHeader("Expires", df.format(expiration.getTime()));
                    response.setHeader("Cache-Control", "max-age=31104000");
                    // END Set the expiration time
                    
                    //  -------- /HTTP HEADER/ MODIFIED SINCE CODE -----------//
                    
                    
                    
                    // set the content type and get the output stream
                    response.setContentType(this.getServletContext().getMimeType(thumbnailFilePath));
                    Logger.debug(this, "Image mime type " + this.getServletContext().getMimeType(thumbnailFilePath));

                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(thumbFile));
                    OutputStream os = response.getOutputStream();
                    byte[] buf = new byte[4096];
                    int i = 0;

                    while ((i = bis.read(buf)) != -1) {
                        os.write(buf, 0, i);
                    }
                    
                    os.flush();
                    os.close();
                    bis.close();
                    Logger.debug(this.getClass(), "time to serve ResizeImage thumbnail: " + (System.currentTimeMillis() - time) + "ms");
                } else {
                    // set the content type and get the output stream
                    response.setContentType("image/jpeg");
                    // Construct the image
                    OutputStream os = response.getOutputStream();
                    JPEGImageEncoderImpl jpegEncode = new JPEGImageEncoderImpl(os);
                    BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = bufferedImage.createGraphics();
                    Color bgColor = new Color(255, 255, 255);
                    graphics.setBackground(bgColor);
                    JPEGEncodeParam encodeParam = jpegEncode.getDefaultJPEGEncodeParam(bufferedImage);
                    encodeParam.setQuality(1, true);
                    jpegEncode.setJPEGEncodeParam(encodeParam);
                    jpegEncode.encode(bufferedImage);
                }

            }
        } catch (Exception e) {
            Logger.error(ResizeImageServlet.class, "Error creating thumbnail from ResizeImage servlet: " + e.getMessage());
        }
        return;
    }
}
