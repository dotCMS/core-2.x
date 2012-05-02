exec ('UPDATE report_asset SET web_form_report=0;');
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[calendar_reminder] (
	[user_id] [varchar](100) COLLATE SQL_Latin1_General_CP1_CI_AI NOT NULL,
	[event_id] [numeric](19, 0) NOT NULL,
	[send_date] [datetime] NOT NULL,
 CONSTRAINT [PK_calendar_reminder] PRIMARY KEY CLUSTERED 
(
	[user_id] ASC,
	[event_id] ASC,
	[send_date] ASC
) 

) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
update report_asset set ds = 'jdbc/dotCMSPool' where ds like 'jdbc/LiferayPool';
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
</portlet-preferences>'
WHERE portletid='EXT_COMMUNICATIONS_MANAGER' AND companyid='dotcms.org';

update portletpreferences  set preferences = '' where portletid = 'EXT_COMMUNICATIONS_MANAGER';
delete from role_ where name like 'Calendar Admin';

delete from qrtz_simple_triggers where trigger_name like 'com.liferay.%';
delete from qrtz_triggers where job_name like 'com.liferay.%';
delete from qrtz_job_details where job_class_name like 'com.liferay.%';
ALTER TABLE links ALTER COLUMN link_code TEXT;

update campaign set active=0 where active is null;