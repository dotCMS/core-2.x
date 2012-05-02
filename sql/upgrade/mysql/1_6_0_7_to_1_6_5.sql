ALTER TABLE structure ADD structuretype int default 0;
update structure set structuretype = 1 where content = 1;
update structure set structuretype = 3 where content = 0;
ALTER TABLE structure DROP COLUMN content;
ALTER TABLE user_proxy ADD chapter_officer VARCHAR(255);
ALTER TABLE report_asset ADD web_form_report TINYINT(1) default 0;
UPDATE report_asset SET web_form_report=0;
update inode set type = 'communication' where inode in (select inode from communication);
CREATE TABLE `calendar_reminder` (
  `user_id` VARCHAR(100) NOT NULL,
  `event_id` BIGINT(20) UNSIGNED NOT NULL,
  `send_date` DATETIME NOT NULL,
  PRIMARY KEY (`user_id`, `event_id`, `send_date`)
);

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
ALTER TABLE links MODIFY `link_code` TEXT;

update campaign set active=0 where active is null;