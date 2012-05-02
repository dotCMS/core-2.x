package com.dotmarketing.cms.downloadfiles.action;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.files.factories.FileFactory;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.util.Logger;

/**
 * 
 * @author Oswaldo
 *
 */
public class DownloadFilesAction extends DispatchAction{
	
	
	public ActionForward unspecified(ActionMapping mapping, ActionForm jf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		return mapping.findForward("");
	}
	
	public ActionForward downloadFile(ActionMapping mapping, ActionForm jf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String inode = request.getParameter("inode");	
		
		
		Identifier iden = IdentifierFactory.getIdentifierByInode(InodeFactory.getInode(inode, File.class));
		//Identifier iden = (Identifier) InodeFactory.getInode(inode, Identifier.class);
		File assetFile = (File) IdentifierFactory.getWorkingChildOfClass(iden, File.class);
		response.setContentType(assetFile.getMimeType());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + assetFile.getFileName() + "\"");
		
		ServletOutputStream out = response.getOutputStream();
		try {
			FileInputStream fis = new FileInputStream(FileFactory.getRealAssetPath(assetFile));
			BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] buf = new byte[1024];
			int i = 0;
			
			while ((i = bis.read(buf)) != -1) {
				out.write(buf, 0, i);
			}
			bis.close();
			fis.close();
			out.close();
		}
		catch (Exception e) {
			Logger.error(this, "File not Found", e);
			response.sendError(404, "File not Found");
		}			
		
		return mapping.findForward("");
	}
	
	
}
