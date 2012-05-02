/*
 * Created on Mar 28, 2005
 */
package com.dotmarketing.portlets.jobs.cms.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import com.dotmarketing.portlets.jobs.factories.EmailFactory;
import com.dotmarketing.portlets.jobs.factories.ResumeFactory;
import com.dotmarketing.portlets.jobs.model.Resume;
import com.dotmarketing.portlets.jobs.struts.ResumeForm;
import com.dotmarketing.util.Logger;
import com.liferay.util.FileUtil;
import com.liferay.util.servlet.UploadServletRequest;

/**
 * @author Maru
 */
public class AddResumeAction  extends DispatchAction{

	public ActionForward unspecified(ActionMapping mapping, ActionForm lf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	    ActionForward af = null;
	    HttpSession sess = request.getSession();
	    Logger.debug(this, "isLoggedIn: " + (String)sess.getAttribute("isLoggedIn"));
	    if (sess.getAttribute("isLoggedIn") != null && sess.getAttribute("isLoggedIn").equals("true")) {
			ResumeForm form = (ResumeForm) lf;
			Resume resume =  ResumeFactory.getResume(request.getParameter("inode"));
	        //copies back into the form
	        BeanUtils.copyProperties(form, resume);
	        request.setAttribute("resumeForm",form);
	        af = (mapping.findForward("addResumePage"));
	    } else {
	        response.sendRedirect("/career_services/login.vsp?referer=/career_services/add_resume.vsp");
	        af = (mapping.findForward(""));
	    }
		return af;
	}

	public ActionForward save(ActionMapping mapping, ActionForm lf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		//Getting and setting request attributes
		ResumeForm form = (ResumeForm) lf;
		Resume resume =  ResumeFactory.getResume(request.getParameter("inode"));
		BeanUtils.copyProperties(resume, form);
		
		//Checking errors
		ActionMessages aes = form.validate(mapping, request);
		if(aes != null && aes.size() > 0){
	        request.setAttribute("resumeForm",form);
			saveMessages(request,aes);
			return mapping.getInputForward();
		}
        resume = ResumeFactory.save(resume);

        //saves file
        String fileName = saveFile(form, request,resume);
        Logger.debug(this, "\n\n\n\nfileName=" + fileName);
        request.setAttribute("resumeFileName",fileName);
        
        //copies back into the form
        BeanUtils.copyProperties(form, resume);
        request.setAttribute("resumeForm",form);
        
        //Forwarding to the page
		ActionForward af = mapping.findForward("addResumePreviewPage");
		
		return af;
	}

	public ActionForward receipt(ActionMapping mapping, ActionForm lf, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ResumeForm form = (ResumeForm) lf;
		Resume resume =  ResumeFactory.getResume(request.getParameter("inode"));
        //copies back into the form
        BeanUtils.copyProperties(form, resume);
        request.setAttribute("resumeForm",form);
        //Forwarding to the page
		ActionForward af = mapping.findForward("addResumeReceiptPage");
		return af;
	}

	public ActionForward success(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ResumeForm resume = (ResumeForm) form;
		Resume newResume =  ResumeFactory.getResume(resume.getInode()+"");
		
		//send confirmation email.
		EmailFactory.sendCareerPostingReceipt("" + newResume.getInode(),newResume.getEmail(),"addResume");

		ActionForward af = (mapping.findForward("addResumePageThankYou"));
		return af;
	}
	private String saveFile (ResumeForm form, HttpServletRequest request, Resume resume) {
		
		try {
			UploadServletRequest uploadReq = (UploadServletRequest)request;
			byte[] bytes = FileUtil.getBytes(uploadReq.getFile("resumedoc"));
			String fileName = uploadReq.getFileName("resumedoc");

			if (bytes!=null) {
/*
				FileUpload fileUpload = new FileUpload();
				fileUpload.setFileName(fileName);
				fileUpload.setFileSize(bytes.length - 2);
				InodeFactory.saveInode(fileUpload);
				resume.addChild(fileUpload);
				
				String dir = Config.CONTEXT.getRealPath(Config.getStringProperty("PATH_TO_RESUMES"));
				//make the needed directories
				new File(dir).mkdirs();
	
				//get the new file handle
				String filePath = dir + File.separatorChar + fileUpload.getInode() + "." + UtilMethods.getFileExtension(fileName);
				File file = new File(filePath);
	
				//wipe out old files
				if (file.exists() || file.isDirectory()) {
					file.delete();
				}
	
				//Saving the new working data
				FileChannel writeCurrentChannel = new FileOutputStream(filePath).getChannel();
				writeCurrentChannel.truncate(0);
				ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
				buffer.put(bytes);
				buffer.position(0);
				writeCurrentChannel.write(buffer);
				writeCurrentChannel.force(false);
				writeCurrentChannel.close();
			*/
			}
			return fileName;
		}
		catch (Exception e) {
			Logger.error(AddResumeAction.class,e.getMessage());
		}
		return "";
	}
}
