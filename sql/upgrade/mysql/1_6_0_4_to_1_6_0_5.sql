--Delete the old entries

delete
from portlet
where portletid in
('EXT_VIRTUAL_LINKS','EXT_USERMANAGER','EXT_4','EXT_6','EXT_13','EXT_BROWSER','9','EXT_STRUCTURE','EXT_11','EXT_CMS_MAINTENANCE','EXT_LANG','EXT_WEBFORMS','EXT_16','EXT_19','EXT_COMMUNICATIONS_MANAGER','EXT_REPORTMANAGER');

-- Inserting backend menu definitions

INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_VIRTUAL_LINKS', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Virtual Links</name>
			  	<icon>/html/skin/image/dotcms/icons/server_link.gif</icon>
			  	<assoc-portlet>EXT_VIRTUAL_LINKS</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/virtuallinks/view_virtuallinks</value>
				  		</param>
				  	</params>
		  	</menu-item>
    	</menu-items>
    </value> 
  </preference>
</portlet-preferences>', false, 'Administrator,Power User,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_USERMANAGER', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_USERMANAGER</assoc-portlet>
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>User</name>
					  	<icon>/html/skin/image/dotcms/icons/user_add.gif</icon>
					  	<assoc-portlet>EXT_USERMANAGER</assoc-portlet>
						  	<params>
						  		<param>
						  			<name>struts_action</name>
						  			<value>/ext/usermanager/edit_usermanager</value>
						  		</param>
						  		<param>
						  			<name>cmd</name>
						  			<value>load_register_user</value>
						  		</param>
						  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Users</name>
			  	<icon>/html/skin/image/dotcms/icons/user.gif</icon>
			  	<assoc-portlet>EXT_USERMANAGER</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/usermanager/view_usermanagerlist</value>
				  		</param>
				  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>', false, 'Mailing Lists Administrator,User Manager Administrator,User Manager Editor,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_4', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
	    <!-- New Menu Items-->
	    <menu-item position="top">
	    	<name>New</name>
	    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
	    	<assoc-portlet />
	    	<params />
	    	<submenu>
			  	<menu-item>
				  	<name>Category</name>
				  	<icon>/html/skin/image/dotcms/icons/chart_organisation_children_add.gif</icon>
				  	<assoc-portlet>EXT_4</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/categories/view_category</value>
				  		</param>
				  	</params>
			  	</menu-item>
	    	</submenu>
	    </menu-item>
	    <!-- Non-New Menu Items-->
	  	<menu-item>
		  	<name>Categories</name>
		  	<icon>/html/skin/image/dotcms/icons/chart_organisation_children.gif</icon>
		  	<assoc-portlet>EXT_4</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/categories/view_category</value>
		  		</param>
		  	</params>
	  	</menu-item>
    </menu-items>
   </value> 
  </preference>
</portlet-preferences>', true, 'CMS Administrator,CMS User,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_6', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
	    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet />
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Category Group</name>
					  	<icon>/html/skin/image/dotcms/icons/chart_organisation_add.gif</icon>
					  	<assoc-portlet>EXT_6</assoc-portlet>
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/ext/entities/edit_entity</value>
					  		</param>
					  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Category Groups</name>
			  	<icon>/html/skin/image/dotcms/icons/chart_organisation.gif</icon>
			  	<assoc-portlet>EXT_6</assoc-portlet>
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/ext/entities/view_entities</value>
			  		</param>
			  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>', true, 'CMS Administrator,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_13', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences></portlet-preferences>', true, 'CMS Administrator,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_BROWSER', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
 <preference>
  <name>MenuItems</name> 
  <value>
    <!-- New Menu Items-->
    <menu-items>
	    <menu-item position="top">
	    	<name>New</name>
	    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
	    	<assoc-portlet />
	    	<params />
	    	<submenu>
			  	<menu-item>
				  	<name>HTML Pages</name>
				  	<icon>/html/skin/image/dotcms/icons/page_add.gif</icon>
				  	<assoc-portlet>EXT_15</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/htmlpages/edit_htmlpage</value>
				  		</param>
				  	</params>
			  	</menu-item>
			  	<menu-item>
				  	<name>Files</name>
				  	<icon>/html/skin/image/dotcms/icons/page_white_add.gif</icon>
				  	<assoc-portlet>EXT_3</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/files/edit_file</value>
				  		</param>
				  	</params>
			  	</menu-item>
			  	<menu-item>
				  	<name>Menu Link</name>
				  	<icon>/html/skin/image/dotcms/icons/link_add.gif</icon>
				  	<assoc-portlet>EXT_18</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/links/edit_link</value>
				  		</param>
				  		<param>
				  			<name>cmd</name>
				  			<value>edit</value>
				  		</param>
				  	</params>
			  	</menu-item>
			  	<menu-item>
				  	<name>Container</name>
				  	<icon>/html/skin/image/dotcms/icons/package_add.gif</icon>
				  	<assoc-portlet>EXT_12</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/containers/edit_container</value>
				  		</param>
				  	</params>
			  	</menu-item>
			  	<menu-item>
				  	<name>Template</name>
				  	<icon>/html/skin/image/dotcms/icons/layout_add.gif</icon>
				  	<assoc-portlet>EXT_13</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/templates/edit_template</value>
				  		</param>
				  	</params>
			  	</menu-item>
			  </submenu>
			</menu-item>
	    <!-- Non-New Menu Items-->
	  	<menu-item>
		  	<name>Browser</name>
		  	<icon>/html/skin/image/dotcms/icons/application_side_list_world.gif</icon>
		  	<assoc-portlet></assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/browser/view_browser</value>
		  		</param>
		  	</params>
	  	</menu-item>
	  	<menu-item>
		  	<name>HTML Pages</name>
		  	<icon>/html/skin/image/dotcms/icons/page.gif</icon>
		  	<assoc-portlet>EXT_15</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/htmlpages/view_htmlpages</value>
		  		</param>
		  	</params>
	  	</menu-item>
	  	<menu-item>
		  	<name>Files</name>
		  	<icon>/html/skin/image/dotcms/icons/page_white_stack.gif</icon>
		  	<assoc-portlet>EXT_3</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/files/view_files</value>
		  		</param>
		  	</params>
	  	</menu-item>
	  	<menu-item>
		  	<name>Containers</name>
		  	<icon>/html/skin/image/dotcms/icons/package.gif</icon>
		  	<assoc-portlet>EXT_12</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/containers/view_containers</value>
		  		</param>
		  	</params>
	  	</menu-item>
	  	<menu-item>
		  	<name>Templates</name>
		  	<icon>/html/skin/image/dotcms/icons/layout.gif</icon>
		  	<assoc-portlet>EXT_13</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/templates/view_templates</value>
		  		</param>
		  	</params>
	  	</menu-item>
	  	<menu-item>
		  	<name>Virtual Links</name>
		  	<icon>/html/skin/image/dotcms/icons/server_link.gif</icon>
		  	<assoc-portlet>EXT_VIRTUAL_LINKS</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/virtuallinks/view_virtuallinks</value>
		  		</param>
		  	</params>
	  	</menu-item>
	  	<menu-item>
		  	<name>Menu Link</name>
		  	<icon>/html/skin/image/dotcms/icons/link.gif</icon>
		  	<assoc-portlet>EXT_18</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/links/view_links</value>
		  		</param>
		  	</params>
	  	</menu-item>
   </menu-items>
  </value> 
 </preference>
</portlet-preferences>', false, 'CMS Administrator,CMS User,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('9', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet />
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Group</name>
					  	<icon>/html/skin/image/dotcms/icons/group_add.gif</icon>
					  	<assoc-portlet>9</assoc-portlet>
					  	<render />
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/admin/list_groups</value>
					  		</param>
					  	</params>
				  	</menu-item>
				  	<menu-item>
					  	<name>Role</name>
					  	<icon>/html/skin/image/dotcms/icons/status_online_add.gif</icon>
					  	<assoc-portlet>9</assoc-portlet>
					  	<render />
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/admin/list_roles</value>
					  		</param>
					  	</params>
				  	</menu-item>
		    	</submenu>
				 </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Permissions</name>
			  	<icon>/html/skin/image/dotcms/icons/lock.gif</icon>
			  	<assoc-portlet>9</assoc-portlet>
			  	<render />
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/admin/view</value>
			  		</param>
			  	</params>
			  	<submenu>
				  	<menu-item>
					  	<name>Groups</name>
					  	<icon>/html/skin/image/dotcms/icons/group_key.gif</icon>
					  	<assoc-portlet>9</assoc-portlet>
					  	<render />
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/admin/list_groups</value>
					  		</param>
					  	</params>
				  	</menu-item>
				  	<menu-item>
					  	<name>Roles</name>
					  	<icon>/html/skin/image/dotcms/icons/status_online_key.gif</icon>
					  	<assoc-portlet>9</assoc-portlet>
					  	<render />
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/admin/list_roles</value>
					  		</param>
					  	</params>
				  	</menu-item>
				  	<menu-item>
					  	<name>Portlets</name>
					  	<icon>/html/skin/image/dotcms/icons/application_cascade_key.gif</icon>
					  	<assoc-portlet>9</assoc-portlet>
					  	<render />
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/admin/list_portlets</value>
					  		</param>
					  	</params>
				  	</menu-item>
			  	</submenu>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>', true, 'Administrator,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_STRUCTURE', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
	    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet />
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Structure</name>
					  	<icon>/html/skin/image/dotcms/icons/table_add.gif</icon>
					  	<assoc-portlet>EXT_STRUCTURE</assoc-portlet>
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/ext/structure/edit_structure</value>
					  		</param>
					  	</params>
				  	</menu-item>
				  	<menu-item>
					  	<name>Relationship</name>
					  	<icon>/html/skin/image/dotcms/icons/table_relationship_add.gif</icon>
					  	<assoc-portlet>EXT_STRUCTURE</assoc-portlet>
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/ext/structure/edit_relationship</value>
					  		</param>
					  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Structures</name>
			  	<icon>/html/skin/image/dotcms/icons/table_multiple.gif</icon>
			  	<assoc-portlet>EXT_STRUCTURE</assoc-portlet>
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/ext/structure/view_structure</value>
			  		</param>
			  	</params>
		  	</menu-item>
		  	<menu-item>
			  	<name>Relationships</name>
			  	<icon>/html/skin/image/dotcms/icons/table_relationship.gif</icon>
			  	<assoc-portlet>EXT_STRUCTURE</assoc-portlet>
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/ext/structure/view_relationships</value>
			  		</param>
			  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>', false, 'CMS Administrator,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_11', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_11</assoc-portlet>
		    	<show-contentlets />
		    	<params />
		    	<submenu/>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Search Contents</name>
			  	<icon>/html/skin/image/dotcms/icons/newspaper_search.gif</icon>
			  	<assoc-portlet>EXT_11</assoc-portlet>
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/ext/contentlet/view_contentlets</value>
			  		</param>
			  	</params>
		  	</menu-item>
		  	<menu-item>
			  	<name>Import Content</name>
			  	<icon>/html/skin/image/dotcms/icons/newspaper_up.gif</icon>
			  	<assoc-portlet>EXT_11</assoc-portlet>
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/ext/contentlet/import_contentlets</value>
			  		</param>
			  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>', true, 'CMS Administrator,CMS User,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_CMS_MAINTENANCE', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Maintenance</name>
			  	<icon>/html/skin/image/dotcms/icons/wrench.gif</icon>
			  	<assoc-portlet>EXT_CMS_MAINTENANCE</assoc-portlet>
			  	<render />
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/cmsmaintenance/view_cms_maintenance</value>
				  		</param>
				  	</params>
		  	</menu-item>
 	   </menu-items>
    </value> 
  </preference>
</portlet-preferences>', true, 'CMS Administrator,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_LANG', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_LANG</assoc-portlet>
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Dictionary Term</name>
					  	<icon>/html/skin/image/dotcms/icons/book_open_add.gif</icon>
					  	<assoc-portlet>EXT_LANG</assoc-portlet>
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/ext/languages_manager/edit_languages</value>
					  		</param>
					  		<param>
					  			<name>id</name>
					  			<value></value>
					  		</param>
					  	</params>
				  	</menu-item>
				  </submenu>
		  	</menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Dictionaries</name>
			  	<icon>/html/skin/image/dotcms/icons/book.gif</icon>
			  	<assoc-portlet>EXT_LANG</assoc-portlet>
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/ext/languages_manager/view_languages_manager</value>
			  		</param>
			  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>', true, 'CMS Administrator,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_WEBFORMS', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Web Forms</name>
			  	<icon>/html/skin/image/dotcms/icons/application_form.gif</icon>
			  	<assoc-portlet>EXT_WEBFORMS</assoc-portlet>
			  	<show-webforms />
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/webforms/view_webforms</value>
				  		</param>
				  	</params>
		  	</menu-item>
    	</menu-items>
    </value> 
  </preference>
</portlet-preferences>', true, 'CMS Administrator,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_16', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_16</assoc-portlet>
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Mailing List</name>
					  	<icon>/html/skin/image/dotcms/icons/email_group_add.gif</icon>
					  	<assoc-portlet>EXT_16</assoc-portlet>
						  	<params>
						  		<param>
						  			<name>struts_action</name>
						  			<value>/ext/usermanager/view_usermanagerlist</value>
						  		</param>
						  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Mailing Lists</name>
			  	<icon>/html/skin/image/dotcms/icons/email_group.gif</icon>
			  	<assoc-portlet>EXT_16</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/mailinglists/view_mailinglists</value>
				  		</param>
				  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>', false, 'Mailing Lists Administrator,Mailing List Editor,User Manager Administrator,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_19', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_19</assoc-portlet>
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Campaign</name>
					  	<icon>/html/skin/image/dotcms/icons/transmit_blue_clock_add.gif</icon>
					  	<assoc-portlet>EXT_19</assoc-portlet>
					  	<create-campaign />
						  	<params>
						  		<param>
						  			<name>struts_action</name>
						  			<value>/ext/campaigns/edit_campaign</value>
						  		</param>
						  		<param>
						  			<name>cmd</name>
						  			<value>edit</value>
						  		</param>
						  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Campaigns</name>
			  	<icon>/html/skin/image/dotcms/icons/transmit_blue_clock.gif</icon>
			  	<assoc-portlet>EXT_19</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/campaigns/view_campaigns</value>
				  		</param>
				  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>', false, 'Campaign Manager Admin,Campaign Manager Viewer,Campaign Manager Editor,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_COMMUNICATIONS_MANAGER', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_COMMUNICATIONS_MANAGER</assoc-portlet>
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Communication</name>
					  	<icon>/html/skin/image/dotcms/icons/transmit_blue_page_add.gif</icon>
					  	<assoc-portlet>EXT_19</assoc-portlet>
					  	<create-campaign />
						  	<params>
						  		<param>
						  			<name>struts_action</name>
						  			<value>/ext/communications/edit_communication</value>
						  		</param>
						  		<param>
						  			<name>cmd</name>
						  			<value>edit</value>
						  		</param>
						  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Communications</name>
			  	<icon>/html/skin/image/dotcms/icons/transmit_blue_page.gif</icon>
			  	<assoc-portlet>EXT_COMMUNICATIONS_MANAGER</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/communications/view_communications</value>
				  		</param>
				  	</params>
		  	</menu-item>
    	</menu-items>
    </value> 
  </preference>
</portlet-preferences>', false, 'Campaign Manager Admin,Campaign Manager Editor,', true);
INSERT INTO portlet (portletid, groupid, companyid, defaultpreferences, narrow, roles, active_) VALUES ('EXT_REPORTMANAGER', 'SHARED_KEY', 'dotcms.org', '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_REPORTMANAGER</assoc-portlet>
		    	<create-report />
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Report</name>
					  	<icon>/html/skin/image/dotcms/icons/report_add.gif</icon>
					  	<assoc-portlet>EXT_REPORTMANAGER</assoc-portlet>
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/ext/report/edit_report</value>
					  		</param>
					  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Reports</name>
			  	<icon>/html/skin/image/dotcms/icons/report.gif</icon>
			  	<assoc-portlet>EXT_REPORTMANAGER</assoc-portlet>
			  	<show-reports />
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/report/view_reports</value>
				  		</param>
				  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>', false, 'CMS Administrator,Report Administrator,Report Editor,Report Viewer,', true);


-- Updating backend menu definitions

UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Virtual Links</name>
			  	<icon>/html/skin/image/dotcms/icons/server_link.gif</icon>
			  	<assoc-portlet>EXT_VIRTUAL_LINKS</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/virtuallinks/view_virtuallinks</value>
				  		</param>
				  	</params>
		  	</menu-item>
    	</menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_VIRTUAL_LINKS' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_USERMANAGER</assoc-portlet>
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>User</name>
					  	<icon>/html/skin/image/dotcms/icons/user_add.gif</icon>
					  	<assoc-portlet>EXT_USERMANAGER</assoc-portlet>
						  	<params>
						  		<param>
						  			<name>struts_action</name>
						  			<value>/ext/usermanager/edit_usermanager</value>
						  		</param>
						  		<param>
						  			<name>cmd</name>
						  			<value>load_register_user</value>
						  		</param>
						  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Users</name>
			  	<icon>/html/skin/image/dotcms/icons/user.gif</icon>
			  	<assoc-portlet>EXT_USERMANAGER</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/usermanager/view_usermanagerlist</value>
				  		</param>
				  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_USERMANAGER' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
	    <!-- New Menu Items-->
	    <menu-item position="top">
	    	<name>New</name>
	    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
	    	<assoc-portlet />
	    	<params />
	    	<submenu>
			  	<menu-item>
				  	<name>Category</name>
				  	<icon>/html/skin/image/dotcms/icons/chart_organisation_children_add.gif</icon>
				  	<assoc-portlet>EXT_4</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/categories/view_category</value>
				  		</param>
				  	</params>
			  	</menu-item>
	    	</submenu>
	    </menu-item>
	    <!-- Non-New Menu Items-->
	  	<menu-item>
		  	<name>Categories</name>
		  	<icon>/html/skin/image/dotcms/icons/chart_organisation_children.gif</icon>
		  	<assoc-portlet>EXT_4</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/categories/view_category</value>
		  		</param>
		  	</params>
	  	</menu-item>
    </menu-items>
   </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_4' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
	    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet />
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Category Group</name>
					  	<icon>/html/skin/image/dotcms/icons/chart_organisation_add.gif</icon>
					  	<assoc-portlet>EXT_6</assoc-portlet>
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/ext/entities/edit_entity</value>
					  		</param>
					  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Category Groups</name>
			  	<icon>/html/skin/image/dotcms/icons/chart_organisation.gif</icon>
			  	<assoc-portlet>EXT_6</assoc-portlet>
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/ext/entities/view_entities</value>
			  		</param>
			  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_6' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
 <preference>
  <name>MenuItems</name> 
  <value>
    <!-- New Menu Items-->
    <menu-items>
	    <menu-item position="top">
	    	<name>New</name>
	    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
	    	<assoc-portlet />
	    	<params />
	    	<submenu>
			  	<menu-item>
				  	<name>HTML Pages</name>
				  	<icon>/html/skin/image/dotcms/icons/page_add.gif</icon>
				  	<assoc-portlet>EXT_15</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/htmlpages/edit_htmlpage</value>
				  		</param>
				  	</params>
			  	</menu-item>
			  	<menu-item>
				  	<name>Files</name>
				  	<icon>/html/skin/image/dotcms/icons/page_white_add.gif</icon>
				  	<assoc-portlet>EXT_3</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/files/edit_file</value>
				  		</param>
				  	</params>
			  	</menu-item>
			  	<menu-item>
				  	<name>Menu Link</name>
				  	<icon>/html/skin/image/dotcms/icons/link_add.gif</icon>
				  	<assoc-portlet>EXT_18</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/links/edit_link</value>
				  		</param>
				  		<param>
				  			<name>cmd</name>
				  			<value>edit</value>
				  		</param>
				  	</params>
			  	</menu-item>
			  	<menu-item>
				  	<name>Container</name>
				  	<icon>/html/skin/image/dotcms/icons/package_add.gif</icon>
				  	<assoc-portlet>EXT_12</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/containers/edit_container</value>
				  		</param>
				  	</params>
			  	</menu-item>
			  	<menu-item>
				  	<name>Template</name>
				  	<icon>/html/skin/image/dotcms/icons/layout_add.gif</icon>
				  	<assoc-portlet>EXT_13</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/templates/edit_template</value>
				  		</param>
				  	</params>
			  	</menu-item>
			  </submenu>
			</menu-item>
	    <!-- Non-New Menu Items-->
	  	<menu-item>
		  	<name>Browser</name>
		  	<icon>/html/skin/image/dotcms/icons/application_side_list_world.gif</icon>
		  	<assoc-portlet></assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/browser/view_browser</value>
		  		</param>
		  	</params>
	  	</menu-item>
	  	<menu-item>
		  	<name>HTML Pages</name>
		  	<icon>/html/skin/image/dotcms/icons/page.gif</icon>
		  	<assoc-portlet>EXT_15</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/htmlpages/view_htmlpages</value>
		  		</param>
		  	</params>
	  	</menu-item>
	  	<menu-item>
		  	<name>Files</name>
		  	<icon>/html/skin/image/dotcms/icons/page_white_stack.gif</icon>
		  	<assoc-portlet>EXT_3</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/files/view_files</value>
		  		</param>
		  	</params>
	  	</menu-item>
	  	<menu-item>
		  	<name>Containers</name>
		  	<icon>/html/skin/image/dotcms/icons/package.gif</icon>
		  	<assoc-portlet>EXT_12</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/containers/view_containers</value>
		  		</param>
		  	</params>
	  	</menu-item>
	  	<menu-item>
		  	<name>Templates</name>
		  	<icon>/html/skin/image/dotcms/icons/layout.gif</icon>
		  	<assoc-portlet>EXT_13</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/templates/view_templates</value>
		  		</param>
		  	</params>
	  	</menu-item>
	  	<menu-item>
		  	<name>Virtual Links</name>
		  	<icon>/html/skin/image/dotcms/icons/server_link.gif</icon>
		  	<assoc-portlet>EXT_VIRTUAL_LINKS</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/virtuallinks/view_virtuallinks</value>
		  		</param>
		  	</params>
	  	</menu-item>
	  	<menu-item>
		  	<name>Menu Link</name>
		  	<icon>/html/skin/image/dotcms/icons/link.gif</icon>
		  	<assoc-portlet>EXT_18</assoc-portlet>
		  	<params>
		  		<param>
		  			<name>struts_action</name>
		  			<value>/ext/links/view_links</value>
		  		</param>
		  	</params>
	  	</menu-item>
   </menu-items>
  </value> 
 </preference>
</portlet-preferences>'
WHERE portletid='EXT_BROWSER' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet />
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Group</name>
					  	<icon>/html/skin/image/dotcms/icons/group_add.gif</icon>
					  	<assoc-portlet>9</assoc-portlet>
					  	<render />
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/admin/list_groups</value>
					  		</param>
					  	</params>
				  	</menu-item>
				  	<menu-item>
					  	<name>Role</name>
					  	<icon>/html/skin/image/dotcms/icons/status_online_add.gif</icon>
					  	<assoc-portlet>9</assoc-portlet>
					  	<render />
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/admin/list_roles</value>
					  		</param>
					  	</params>
				  	</menu-item>
		    	</submenu>
				 </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Permissions</name>
			  	<icon>/html/skin/image/dotcms/icons/lock.gif</icon>
			  	<assoc-portlet>9</assoc-portlet>
			  	<render />
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/admin/view</value>
			  		</param>
			  	</params>
			  	<submenu>
				  	<menu-item>
					  	<name>Groups</name>
					  	<icon>/html/skin/image/dotcms/icons/group_key.gif</icon>
					  	<assoc-portlet>9</assoc-portlet>
					  	<render />
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/admin/list_groups</value>
					  		</param>
					  	</params>
				  	</menu-item>
				  	<menu-item>
					  	<name>Roles</name>
					  	<icon>/html/skin/image/dotcms/icons/status_online_key.gif</icon>
					  	<assoc-portlet>9</assoc-portlet>
					  	<render />
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/admin/list_roles</value>
					  		</param>
					  	</params>
				  	</menu-item>
				  	<menu-item>
					  	<name>Portlets</name>
					  	<icon>/html/skin/image/dotcms/icons/application_cascade_key.gif</icon>
					  	<assoc-portlet>9</assoc-portlet>
					  	<render />
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/admin/list_portlets</value>
					  		</param>
					  	</params>
				  	</menu-item>
			  	</submenu>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='9' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
	    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet />
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Structure</name>
					  	<icon>/html/skin/image/dotcms/icons/table_add.gif</icon>
					  	<assoc-portlet>EXT_STRUCTURE</assoc-portlet>
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/ext/structure/edit_structure</value>
					  		</param>
					  	</params>
				  	</menu-item>
				  	<menu-item>
					  	<name>Relationship</name>
					  	<icon>/html/skin/image/dotcms/icons/table_relationship_add.gif</icon>
					  	<assoc-portlet>EXT_STRUCTURE</assoc-portlet>
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/ext/structure/edit_relationship</value>
					  		</param>
					  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Structures</name>
			  	<icon>/html/skin/image/dotcms/icons/table_multiple.gif</icon>
			  	<assoc-portlet>EXT_STRUCTURE</assoc-portlet>
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/ext/structure/view_structure</value>
			  		</param>
			  	</params>
		  	</menu-item>
		  	<menu-item>
			  	<name>Relationships</name>
			  	<icon>/html/skin/image/dotcms/icons/table_relationship.gif</icon>
			  	<assoc-portlet>EXT_STRUCTURE</assoc-portlet>
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/ext/structure/view_relationships</value>
			  		</param>
			  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_STRUCTURE' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_11</assoc-portlet>
		    	<show-contentlets />
		    	<params />
		    	<submenu/>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Search Contents</name>
			  	<icon>/html/skin/image/dotcms/icons/newspaper_search.gif</icon>
			  	<assoc-portlet>EXT_11</assoc-portlet>
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/ext/contentlet/view_contentlets</value>
			  		</param>
			  	</params>
		  	</menu-item>
		  	<menu-item>
			  	<name>Import Content</name>
			  	<icon>/html/skin/image/dotcms/icons/newspaper_up.gif</icon>
			  	<assoc-portlet>EXT_11</assoc-portlet>
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/ext/contentlet/import_contentlets</value>
			  		</param>
			  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_11' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Maintenance</name>
			  	<icon>/html/skin/image/dotcms/icons/wrench.gif</icon>
			  	<assoc-portlet>EXT_CMS_MAINTENANCE</assoc-portlet>
			  	<render />
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/cmsmaintenance/view_cms_maintenance</value>
				  		</param>
				  	</params>
		  	</menu-item>
 	   </menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_CMS_MAINTENANCE' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_LANG</assoc-portlet>
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Dictionary Term</name>
					  	<icon>/html/skin/image/dotcms/icons/book_open_add.gif</icon>
					  	<assoc-portlet>EXT_LANG</assoc-portlet>
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/ext/languages_manager/edit_languages</value>
					  		</param>
					  		<param>
					  			<name>id</name>
					  			<value></value>
					  		</param>
					  	</params>
				  	</menu-item>
				  </submenu>
		  	</menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Dictionaries</name>
			  	<icon>/html/skin/image/dotcms/icons/book.gif</icon>
			  	<assoc-portlet>EXT_LANG</assoc-portlet>
			  	<params>
			  		<param>
			  			<name>struts_action</name>
			  			<value>/ext/languages_manager/view_languages_manager</value>
			  		</param>
			  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_LANG' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Web Forms</name>
			  	<icon>/html/skin/image/dotcms/icons/application_form.gif</icon>
			  	<assoc-portlet>EXT_WEBFORMS</assoc-portlet>
			  	<show-webforms />
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/webforms/view_webforms</value>
				  		</param>
				  	</params>
		  	</menu-item>
    	</menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_WEBFORMS' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_16</assoc-portlet>
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Mailing List</name>
					  	<icon>/html/skin/image/dotcms/icons/email_group_add.gif</icon>
					  	<assoc-portlet>EXT_16</assoc-portlet>
						  	<params>
						  		<param>
						  			<name>struts_action</name>
						  			<value>/ext/usermanager/view_usermanagerlist</value>
						  		</param>
						  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Mailing Lists</name>
			  	<icon>/html/skin/image/dotcms/icons/email_group.gif</icon>
			  	<assoc-portlet>EXT_16</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/mailinglists/view_mailinglists</value>
				  		</param>
				  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_16' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_19</assoc-portlet>
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Campaign</name>
					  	<icon>/html/skin/image/dotcms/icons/transmit_blue_clock_add.gif</icon>
					  	<assoc-portlet>EXT_19</assoc-portlet>
					  	<create-campaign />
						  	<params>
						  		<param>
						  			<name>struts_action</name>
						  			<value>/ext/campaigns/edit_campaign</value>
						  		</param>
						  		<param>
						  			<name>cmd</name>
						  			<value>edit</value>
						  		</param>
						  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Campaigns</name>
			  	<icon>/html/skin/image/dotcms/icons/transmit_blue_clock.gif</icon>
			  	<assoc-portlet>EXT_19</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/campaigns/view_campaigns</value>
				  		</param>
				  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_19' AND companyid='dotcms.org';

UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_COMMUNICATIONS_MANAGER</assoc-portlet>
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Communication</name>
					  	<icon>/html/skin/image/dotcms/icons/transmit_blue_page_add.gif</icon>
					  	<assoc-portlet>EXT_19</assoc-portlet>
					  	<create-campaign />
						  	<params>
						  		<param>
						  			<name>struts_action</name>
						  			<value>/ext/communications/edit_communication</value>
						  		</param>
						  		<param>
						  			<name>cmd</name>
						  			<value>edit</value>
						  		</param>
						  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Communications</name>
			  	<icon>/html/skin/image/dotcms/icons/transmit_blue_page.gif</icon>
			  	<assoc-portlet>c</assoc-portlet>
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/communications/view_communications</value>
				  		</param>
				  	</params>
		  	</menu-item>
    	</menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_COMMUNICATIONS_MANAGER' AND companyid='dotcms.org';


UPDATE portlet SET defaultpreferences = '<portlet-preferences>
  <preference>
  <name>MenuItems</name> 
  <value>
    <menu-items>
		    <!-- New Menu Items-->
		    <menu-item position="top">
		    	<name>New</name>
		    	<icon>/html/skin/image/dotcms/icons/page_new.gif</icon>
		    	<assoc-portlet>EXT_REPORTMANAGER</assoc-portlet>
		    	<create-report />
		    	<params />
		    	<submenu>
				  	<menu-item>
					  	<name>Report</name>
					  	<icon>/html/skin/image/dotcms/icons/report_add.gif</icon>
					  	<assoc-portlet>EXT_REPORTMANAGER</assoc-portlet>
					  	<params>
					  		<param>
					  			<name>struts_action</name>
					  			<value>/ext/report/edit_report</value>
					  		</param>
					  	</params>
				  	</menu-item>
		    	</submenu>
		    </menu-item>
		    <!-- Non-New Menu Items-->
		  	<menu-item>
			  	<name>Reports</name>
			  	<icon>/html/skin/image/dotcms/icons/report.gif</icon>
			  	<assoc-portlet>EXT_REPORTMANAGER</assoc-portlet>
			  	<show-reports />
				  	<params>
				  		<param>
				  			<name>struts_action</name>
				  			<value>/ext/report/view_reports</value>
				  		</param>
				  	</params>
		  	</menu-item>
	    </menu-items>
    </value> 
  </preference>
</portlet-preferences>'
WHERE portletid='EXT_REPORTMANAGER' AND companyid='dotcms.org';