package org.dotcms.servlet;

import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dotmarketing.common.db.DotConnect;

public class TrackbackServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String INSERTSQL = "insert into dotcms_trackback (time,hostname,default_host,ipaddress,host_aliases,version,build,remote_server,remote_address) values(?,?,?,?,?,?,?,?,?)";
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		DotConnect dc = new DotConnect();
		dc.setSQL(INSERTSQL);
		dc.addParam(Calendar.getInstance().getTime());
		dc.addParam(req.getParameter("hostname"));
		dc.addParam(req.getParameter("defaultHost"));
		dc.addParam(req.getParameter("ipAddr"));
		dc.addParam(req.getParameter("allHosts"));
		dc.addParam(req.getParameter("version"));
		dc.addParam(Integer.parseInt(req.getParameter("build")));
		dc.addParam(req.getRemoteHost());
		dc.addParam(req.getRemoteAddr());
		dc.getResult();
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		doGet(req, resp);
	}
}
