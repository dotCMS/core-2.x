<%@page import="com.dotmarketing.portlets.categories.business.CategoryAPI"%>
<%@page import="com.dotmarketing.business.APILocator"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="com.dotmarketing.portlets.entities.model.Entity"%>
<%@page import="com.dotmarketing.portlets.entities.factories.EntityFactory"%>
<%@page import="com.dotmarketing.portlets.categories.model.Category"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.liferay.portal.language.LanguageUtil"%>

<%
	CategoryAPI catAPI = APILocator.getCategoryAPI();
	List myUserCategories = new ArrayList();
	Entity entity = EntityFactory.getEntity("UserProxy");
	List<Category> userCategories = EntityFactory.getEntityCategories(entity);

%>


<table class="listingTable">
<%
	Iterator userCatIt = userCategories.iterator();

	while(userCatIt.hasNext()) {
		
		Category cat = (Category) userCatIt.next();
		if(cat.hasChildren() && catAPI.canUseCategory(cat, user, false)){

			List<Category> childCats  = catAPI.getChildren(cat, user, false); 
			StringBuffer buffy = new StringBuffer();
			boolean canUseAnyChildCats = false;
	
			if(childCats!=null) { 
				buffy.append("<select name=\"categories\" multiple=\"multiple\" style=\"width: 250px\">");
				Iterator childCatIter = childCats.iterator();
				while(childCatIter.hasNext()){
					Category childCatUnk = (Category) childCatIter.next();
					if(catAPI.canUseCategory(childCatUnk,user,false)) { 
						buffy.append("<option value=\"" +  childCatUnk.getInode() +  "\"" + ">" + childCatUnk.getCategoryName() + "</option>");
						canUseAnyChildCats = true; 
					} 
				}
				buffy.append("</select>");
			}
			if(canUseAnyChildCats) { 
%>
	<tr>
        <th valign="top"><%= cat.getCategoryName() %>:</th>
        <td id="userCategorySelectsWrapper">
        	<%= buffy.toString() %>
        </td>
	</tr>
<% 
			}
		}	
 	}
%>

</table>

<% if(userCategories.size() > 0) { %>
	<div class="buttonRow">
        <button dojoType="dijit.form.Button" onClick="updateUserCategories();">
           <%= LanguageUtil.get(pageContext, "update") %>
        </button>
	</div>
<% } else { %>
	<div class="noResultsMessage">
		<%= LanguageUtil.get(pageContext, "You-cannot-categorize-a-user-until-you-create-and-tag-the-UserProxy-Entity-in-the-Entity-Manager") %><br/>
		<%= LanguageUtil.get(pageContext, "Click") %> <a href="javascript:addUserProxyEntity('<%=entity.getInode()%>');"><%= LanguageUtil.get(pageContext, "here") %></a> <%= LanguageUtil.get(pageContext, "to-add-categories-to-the-User-Proxy-Entity") %>
	</div>
<% } %>