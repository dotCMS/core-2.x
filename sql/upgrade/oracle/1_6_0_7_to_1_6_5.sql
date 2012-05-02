ALTER TABLE structure ADD structuretype int;
update structure set structuretype = 1 where content = 1;
update structure set structuretype = 3 where content = 0;
ALTER TABLE structure DROP COLUMN content;
ALTER TABLE user_proxy ADD chapter_officer VARCHAR2(255);
update inode set type = 'communication' where inode in (select inode from communication);
ALTER TABLE report_asset ADD web_form_report NUMBER(1,0) default 0;
UPDATE report_asset SET web_form_report=0;
create table calendar_reminder
(
   user_id VARCHAR2(100) not null,
   event_id NUMBER(20) not null,
   send_date DATE not null
);

alter table calendar_reminder add constraint calendar_reminder_pk primary key(user_id,event_id,send_date);
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

update campaign set active=0 where active is null;

delete from qrtz_simple_triggers where trigger_name like 'com.liferay.%';
delete from qrtz_triggers where job_name like 'com.liferay.%';
delete from qrtz_job_details where job_class_name like 'com.liferay.%';
ALTER TABLE links ADD linkcodetmp clob;
update links i1 set linkcodetmp = (select link_code from links i2 where i2.inode = i1.inode);
ALTER TABLE links DROP COLUMN link_code;
ALTER TABLE links RENAME COLUMN linkcodetmp TO link_code;