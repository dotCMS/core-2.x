package com.dotmarketing.quartz.job;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.dotmarketing.beans.Host;
import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.business.RoleFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.contentlet.business.ContentletAPI;
import com.dotmarketing.portlets.contentlet.model.Contentlet;
import com.dotmarketing.portlets.contentlet.util.ContentletHTMLUtil;
import com.dotmarketing.portlets.structure.model.Structure;
import com.dotmarketing.portlets.workflows.factories.WorkflowsFactory;
import com.dotmarketing.portlets.workflows.model.WorkflowHistory;
import com.dotmarketing.portlets.workflows.model.WorkflowTask;
import com.dotmarketing.util.Config;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilMethods;
import com.dotmarketing.util.WebKeys.WorkflowStatuses;
public class ContentReviewThread implements Runnable, Job {

	private ContentletAPI conAPI = APILocator.getContentletAPI();
	
    public ContentReviewThread() {
    }

    public void run() {
        try {
            Logger.debug(this, "Starting ContentsReview");
            DotHibernate.startTransaction();
            
            DotHibernate dh = new DotHibernate(Contentlet.class);
            dh.setSQLQuery("select {contentlet.*} from contentlet, inode contentlet_1_, structure "
                    + "where contentlet.inode = contentlet_1_.inode and " 
                    + "? >= contentlet.next_review and "
                    + "contentlet.review_interval is not null and contentlet.review_interval <> '' and "
                    + "contentlet.structure_inode = structure.inode and "
                    + "structure.reviewer_role is not null and structure.reviewer_role <> '' and " 
                    + "contentlet.working = ? ");
            dh.setParam(new Date());
            dh.setParam(true);
            List<Contentlet> contentlets = dh.list();
            HashMap<String, Structure> structures = new HashMap<String, Structure>();
            String systemUserId = "System";
            for (Contentlet cont : contentlets) {
                try {
                	cont.setNextReview(conAPI.getNextReview(cont, APILocator.getUserAPI().getSystemUser(), false));
                    Structure st = structures.get(cont.getStructureInode());
                    if (st == null) {
                        st = (Structure) InodeFactory.getInode(cont.getStructureInode(), Structure.class);
                        structures.put(st.getInode(), st);
                    }

                    WorkflowTask task = new WorkflowTask();

                    Identifier id = IdentifierFactory.getIdentifierByInode(cont);
                    task.setTitle("Content: " + id.getInode() + " needs to be reviewed.");

                    task.setDescription(ContentletHTMLUtil.toPrettyHTMLString(cont));

                    task.setBelongsTo(st.getReviewerRole());
                    task.setModDate(new Date());
                    task.setCreationDate(new Date());
                    task.setCreatedBy(systemUserId);
                    task.setAssignedTo("Nobody");
                    task.setDueDate(null);
                    task.setWebasset(cont.getInode());
                    task.setStatus(WorkflowStatuses.OPEN.toString());
                    
                    InodeFactory.saveInode(task);

                    WorkflowHistory hist = new WorkflowHistory();
                    hist.setChangeDescription("Review Content Task Creation");
                    hist.setCreationDate(new Date());
                    hist.setMadeBy(systemUserId);
                    InodeFactory.saveInode(hist);
                    task.addChild(hist);
                    
                    String body = _buildWorkflowEmailBody (task, "New Review Task");
                    WorkflowsFactory.sendWorkflowChangeEmails(task, "A new Review Content Task was created", body, null);
                    
                } catch (Exception e) {
                    Logger.error(this, "Error ocurred trying to create the review task for contenlet: "
                            + cont.getInode(), e);
                }
            }

        } catch (Exception e) {
            Logger.error(this, "Error ocurred trying to review contents.", e);
        } finally {
            DotHibernate.commitTransaction();
        }
    }

    private String _buildWorkflowEmailBody (WorkflowTask task, String change) throws DotDataException, DotSecurityException {

        Host host = APILocator.getHostAPI().findDefaultHost(APILocator.getUserAPI().getSystemUser(), false);
        String ref = "http://" + host.getHostname() + Config.getStringProperty("WORKFLOWS_URL") + "&inode=" + String.valueOf(task.getInode());

        StringBuffer buffer = new StringBuffer ();

        try {
            String roleName = APILocator.getRoleAPI().loadRoleById(task.getBelongsTo()).getName();
            
            buffer.append(
                  "<table align=\"center\" border=\"1\" width=\"50%\">" +
                  "    <tr>" +
                  "        <td align=\"center\" colspan=\"2\"><b>" + change + "</b></td>" +
                  "    </tr>" +
                  "    <tr>" +
                  "        <td width=\"15%\" nowrap><b>Task</b></td><td><a href=\"" + ref + "\">" + task.getTitle() + "</a></td>" +
                  "    </tr>" +
                  "    <tr>" +
                  "        <td  nowrap><b>Created</b></td><td>" + UtilMethods.dateToHTMLDate(task.getCreationDate()) + "</td>" +
                  "    </tr>" +
                  "    <tr>" +
                  "        <td  nowrap><b>Author</b></td><td>" + UtilMethods.getUserFullName(task.getCreatedBy()) + "</td>" +
                  "    </tr>" +
                  "    <tr>" +
                  "        <td  nowrap><b>Assignee Group</b></td><td>" + roleName + "</td>" +
                  "    </tr>" +
                  "</table>"  
            );
        } catch (Exception e) {
            Logger.warn(RoleFactory.class, "_buildWorkflowEmailBody: Error getting role", e);
        }
        
        return buffer.toString();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Thread#destroy()
     */
    public void destroy() {
        DotHibernate.closeSession();
    }
    
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	Logger.debug(this, "Running ContentReviewThread - " + new Date());

    	try {
			run();
		} catch (Exception e) {
			Logger.info(this, e.toString());
		} finally {
			DotHibernate.closeSession();
		}
	}
}
