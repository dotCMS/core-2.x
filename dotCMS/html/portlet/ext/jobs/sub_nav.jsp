<%
/**
 * Copyright (c) 2000-2002 Liferay Corporation
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
%>

<%@ include file="/html/portlet/ext/jobs/init.jsp" %>

<table border="0" cellpadding="4" cellspacing="0" width="100%">
<tr class="beta">
	<td align="center">
	<font class="beta" size="2">

		<a class="beta" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_jobs" /></portlet:renderURL>">
		<%= LanguageUtil.get(pageContext, "view-jobs") %></a> |
	
		<a class="beta" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/edit_job" /></portlet:actionURL>">
		<%= LanguageUtil.get(pageContext, "add-job") %></a></font> |
		
		<a class="beta" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_resumes" /></portlet:renderURL>">
		<%= LanguageUtil.get(pageContext, "view-resumes") %></a></font> |

		<a class="beta" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/edit_resume" /></portlet:actionURL>">
		<%= LanguageUtil.get(pageContext, "add-resume") %></a></font> |

		<a class="beta" href="<portlet:renderURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/view_searchfirms" /></portlet:renderURL>">
		<%= LanguageUtil.get(pageContext, "view-searchfirms") %></a></font> |

		<a class="beta" href="<portlet:actionURL windowState="<%= WindowState.MAXIMIZED.toString() %>"><portlet:param name="struts_action" value="/ext/jobs/edit_searchfirm" /></portlet:actionURL>">
		<%= LanguageUtil.get(pageContext, "add-searchfirm") %></a></font>
		
	</font>
	</td>
</tr>
</table>