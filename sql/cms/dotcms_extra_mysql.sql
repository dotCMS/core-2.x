-- mysql
CREATE INDEX idx_tree ON tree (child, parent, relation_type);
CREATE INDEX idx_tree_1 ON tree (parent);
CREATE INDEX idx_tree_2 ON tree (child);
CREATE INDEX idx_tree_3 ON tree (relation_type); 
CREATE INDEX idx_tree_4 ON tree (parent, child, relation_type);
CREATE INDEX idx_tree_5 ON tree (parent, relation_type);
CREATE INDEX idx_tree_6 ON tree (child, relation_type);
CREATE INDEX idx_contentlet_1 ON contentlet (inode, live);
CREATE INDEX idx_contentlet_2 ON contentlet (inode, working);

CREATE INDEX idx_contentlet_3 ON contentlet (inode);


CREATE INDEX idx_identifier ON identifier (inode);
CREATE INDEX idx_permisision_4 ON permission (permission_type);


CREATE INDEX idx_permission_reference_2 ON permission_reference (reference_id);
CREATE INDEX idx_permission_reference_3 ON permission_reference (reference_id,permission_type);
CREATE INDEX idx_permission_reference_4 ON permission_reference (asset_id,permission_type);
CREATE INDEX idx_permission_reference_5 ON permission_reference (asset_id,reference_id,permission_type);
CREATE INDEX idx_permission_reference_6 ON permission_reference (permission_type);

CREATE UNIQUE INDEX idx_field_velocity_structure ON field (velocity_var_name,structure_inode); 

alter table tree add index (parent), add constraint FK36739EC4AB08AA foreign key (parent) references inode (inode);
alter table tree add index (child), add constraint FK36739E5A3F51C foreign key (child) references inode (inode);

alter table chain_state add constraint fk_state_chain foreign key (chain_id) references chain(id);
alter table chain_state add constraint fk_state_code foreign key (link_code_id) references chain_link_code(id);
alter table chain_state_parameter add constraint fk_parameter_state foreign key (chain_state_id) references chain_state(id);

alter table permission add constraint permission_inode_fk foreign key (inode_id) references inode(inode);
alter table permission add constraint permission_role_fk foreign key (roleid) references cms_role(id);

alter table permission_reference add constraint permission_asset_id_fk foreign key (asset_id) references inode(inode);
alter table permission_reference add constraint permission_reference_id_fk foreign key (reference_id) references inode(inode);

alter table contentlet add constraint FK_structure_inode foreign key (structure_inode) references structure(inode);

ALTER TABLE structure MODIFY fixed varchar(1) DEFAULT '0' NOT NULL;

ALTER TABLE field MODIFY fixed varchar(1) DEFAULT '0' NOT NULL;
ALTER TABLE field MODIFY read_only varchar(1) DEFAULT '1' NOT NULL;

ALTER TABLE campaign MODIFY active varchar(1) DEFAULT '0' NOT NULL;

insert into User_ (userId, companyId, createDate, password_, passwordEncrypted, passwordReset, firstName, middleName, lastName, male, birthday, emailAddress, skinId, dottedSkins, roundedSkins, greeting, layoutIds, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values ('dotcms.org.default', 'default', now(), 'password', '0', '0', '', '', '', '1', '1970-01-01', 'default@dotcms.org', '01', '0', '0', 'Welcome!', '', now(), 0, '0', '1');

create index addres_userid_index on address(userid);
create index tag_user_id_index on tag(user_id);
create index tag_inode_tagid on tag_inode(tag_id);
create index tag_inode_inode on tag_inode(inode);
CREATE TABLE `dist_journal` (
  `id` BIGINT  NOT NULL AUTO_INCREMENT,
  `object_to_index` VARCHAR(1024)  NOT NULL,
  `serverid` VARCHAR(64)  NOT NULL,
  `journal_type` INTEGER  NOT NULL,
  `time_entered` DATETIME  NOT NULL,
  PRIMARY KEY (`id`)
);
ALTER TABLE dist_journal ADD UNIQUE (object_to_index(255), serverid,journal_type);

create table plugin_property (
   plugin_id varchar(255) not null,
   propkey varchar(255) not null,
   original_value varchar(255) not null,
   current_value varchar(255) not null
);
alter table plugin_property add constraint fk_plugin_plugin_property foreign key (plugin_id) references plugin(id);

CREATE TABLE `dist_process` (`id` BIGINT  NOT NULL AUTO_INCREMENT,`object_to_index` VARCHAR(1024)  NOT NULL,`serverid` VARCHAR(64)  NOT NULL,`journal_type` INTEGER  NOT NULL,`time_entered` DATETIME  NOT NULL, PRIMARY KEY (`id`));
CREATE INDEX dist_process_index USING BTREE on dist_process (object_to_index (255), serverid,journal_type);

CREATE TABLE `dist_reindex_journal` (`id` BIGINT  NOT NULL AUTO_INCREMENT,`inode_to_index` VARCHAR(100)  NOT NULL,`ident_to_index` VARCHAR(100)  NOT NULL,`serverid` VARCHAR(64)  NOT NULL,`priority` INTEGER  NOT NULL,`time_entered` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, index_val varchar(325), dist_action integer NOT NULL DEFAULT 1, PRIMARY KEY (`id`));

CREATE INDEX dist_reindex_index1 USING BTREE on dist_reindex_journal (inode_to_index (100));
CREATE INDEX dist_reindex_index2 USING BTREE on dist_reindex_journal (dist_action);
CREATE INDEX dist_reindex_index3 USING BTREE on dist_reindex_journal (serverid);
CREATE INDEX dist_reindex_index4 USING BTREE on dist_reindex_journal (ident_to_index,serverid);
CREATE INDEX dist_reindex_index USING BTREE on dist_reindex_journal (serverid,dist_action);
CREATE INDEX dist_reindex_index5 USING BTREE ON dist_reindex_journal (priority, time_entered);
CREATE INDEX dist_reindex_index6 USING BTREE ON dist_reindex_journal (priority);

CREATE TABLE `quartz_log` (`id` BIGINT  NOT NULL AUTO_INCREMENT,`JOB_NAME` VARCHAR(255)  NOT NULL,`serverid` VARCHAR(64) ,`time_started` DATETIME  NOT NULL, PRIMARY KEY (`id`));


ALTER TABLE cms_role ADD UNIQUE (role_key);

alter table cms_role add constraint fkcms_role_parent foreign key (parent) references cms_role (id) ON DELETE CASCADE;
			

ALTER TABLE cms_layout ADD UNIQUE (layout_name);
ALTER TABLE portlet ADD UNIQUE (portletid);
ALTER TABLE cms_layouts_portlets ADD UNIQUE (portlet_id, layout_id);
alter table cms_layouts_portlets add constraint fkcms_layouts_portlets foreign key (layout_id) references cms_layout(id);

ALTER TABLE users_cms_roles ADD UNIQUE (role_id, user_id);
alter table users_cms_roles add constraint fkusers_cms_roles1 foreign key (role_id) references cms_role (id);
alter table users_cms_roles add constraint fkusers_cms_roles2 foreign key (user_id) references user_ (userid);
				
ALTER TABLE layouts_cms_roles ADD UNIQUE (role_id, layout_id);
alter table layouts_cms_roles add constraint fklayouts_cms_roles1 foreign key (role_id) references cms_role (id);
alter table layouts_cms_roles add constraint fklayouts_cms_roles2 foreign key (layout_id) references cms_layout (id);

alter table contentlet add constraint fk_folder foreign key (folder) references folder(inode);


create table dist_reindex_lock (dummy int);
create table dist_lock (dummy int);

create table import_audit (
	id bigint not null,
	start_date timestamp,
	userid varchar(255), 
	filename varchar(512),
	status int,
	last_inode varchar(100),
	records_to_import bigint,
	serverid varchar(255), 
	primary key (id)
	);

alter table category modify column category_velocity_var_name varchar(255) not null;

alter table import_audit add column warnings text,
	add column errors text,
	add column results text,
	add column messages text;


alter table structure modify host varchar(100) default 'SYSTEM_HOST' not null;
alter table structure modify folder varchar(100) default 'SYSTEM_FOLDER' not null;
alter table structure add constraint fk_structure_folder foreign key (folder) references folder(inode);

alter table structure modify column velocity_var_name varchar(255) not null;
alter table structure add constraint unique_struct_vel_var_name unique (velocity_var_name);

	
DROP PROCEDURE IF EXISTS load_records_to_index;
CREATE PROCEDURE load_records_to_index(IN server_id VARCHAR(100), IN records_to_fetch INT)
BEGIN
DECLARE c1 BIGINT;
DECLARE cid VARCHAR(100);
DECLARE last_time TIMESTAMP;
DECLARE isfirst BOOLEAN;
DECLARE found_recs BOOLEAN;
DECLARE v_id BIGINT;
DECLARE v_inode_to_index VARCHAR(100);
DECLARE v_ident_to_index VARCHAR(100);
DECLARE v_serverid VARCHAR(64);
DECLARE v_priority INT;
DECLARE v_time_entered TIMESTAMP;
DECLARE v_index_val VARCHAR(325);
DECLARE v_dist_action INT;
DECLARE v_not_found BOOL DEFAULT FALSE;
DECLARE count_n INT;

DECLARE cur1 CURSOR FOR
        SELECT id,inode_to_index,ident_to_index,serverid,priority,time_entered,index_val,dist_action FROM dist_reindex_journal  WHERE ((serverid = server_id AND dist_action = 2 AND time_entered > last_time) OR (serverid = server_id AND dist_action <> 3 AND time_entered > last_time)) ORDER BY priority ASC ,time_entered ASC LIMIT 1000;

DECLARE cur2 CURSOR FOR
        SELECT id,inode_to_index,ident_to_index,serverid,priority,time_entered,index_val,dist_action FROM dist_reindex_journal  WHERE (serverid = server_id AND dist_action = 2) OR (serverid = server_id AND dist_action <> 3) ORDER BY priority ASC ,time_entered ASC LIMIT 1000;

DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_not_found := TRUE;


DROP TEMPORARY TABLE IF EXISTS dj;
CREATE TEMPORARY TABLE IF NOT EXISTS dj
       (id BIGINT, inode_to_index VARCHAR(100), ident_to_index VARCHAR(100),
         serverid VARCHAR(64), priority INT, time_entered TIMESTAMP, index_val VARCHAR(325), dist_action INT);


SET c1 := 0;
SET last_time := NOW();
SET isfirst := TRUE;
while_label:WHILE(c1<11) DO
   IF (c1>0) THEN
     LEAVE while_label;
   END IF;
   IF(isfirst = FALSE) THEN
      SET found_recs := false;
        OPEN cur1;
        cur1_loop : LOOP
        FETCH cur1 INTO v_id,v_inode_to_index,v_ident_to_index,v_serverid,v_priority,v_time_entered,v_index_val,v_dist_action;
        IF (v_not_found) THEN
           SET v_not_found := FALSE;
           LEAVE cur1_loop;
        END IF;
        SET found_recs := TRUE;
				IF (c1 > (records_to_fetch - 1)) THEN
					LEAVE while_label;
				END IF;
		IF(v_dist_action = 2) THEN
		  SET c1 := c1 + 1;
          INSERT INTO dj VALUES(v_id,v_inode_to_index,v_ident_to_index,v_serverid,v_priority,v_time_entered,v_index_val,v_dist_action);
		ELSE
		  SET count_n := 0;
		  SELECT COUNT(inode) INTO count_n FROM inode WHERE inode = v_inode_to_index;
		  IF (count_n>0) THEN
			SET c1 := c1 + 1;
            INSERT INTO dj VALUES(v_id,v_inode_to_index,v_ident_to_index,v_serverid,v_priority,v_time_entered,v_index_val,v_dist_action);
		  END IF;
		END IF;
		SET last_time := v_time_entered;
		END LOOP;
      CLOSE cur1;
      IF (found_recs = FALSE) THEN
				LEAVE while_label;
			END IF;
   ELSE
      OPEN cur2;
      cur2_loop : LOOP
        FETCH cur2 INTO v_id,v_inode_to_index,v_ident_to_index,v_serverid,v_priority,v_time_entered,v_index_val,v_dist_action;
        IF (v_not_found) THEN
           SET v_not_found := FALSE;
           LEAVE cur2_loop;
        END IF;
				IF (c1 > (records_to_fetch - 1)) THEN
					LEAVE while_label;
				END IF;
        IF(v_dist_action = 2) THEN
		  SET c1 := c1 + 1;
          INSERT INTO dj VALUES(v_id,v_inode_to_index,v_ident_to_index,v_serverid,v_priority,v_time_entered,v_index_val,v_dist_action);
		ELSE
		  SET count_n := 0;
		  SELECT COUNT(inode) INTO count_n FROM inode WHERE inode = v_inode_to_index;
		  IF (count_n>0) THEN
			SET c1 := c1 + 1;
            INSERT INTO dj VALUES(v_id,v_inode_to_index,v_ident_to_index,v_serverid,v_priority,v_time_entered,v_index_val,v_dist_action);
		  END IF;
		END IF;
		SET last_time := v_time_entered;
		END LOOP;
      CLOSE cur2;
      SET isfirst := FALSE;
   END IF;
END WHILE while_label;

SELECT * FROM dj;

END
/

alter table structure add constraint fk_structure_host foreign key (host) references inode(inode);

create index idx_template3 on template (title);
create index idx_template4 on template (working);
create index idx_template5 on template (deleted);

CREATE INDEX idx_contentlet_4 ON contentlet (structure_inode);

alter table contentlet add constraint fk_user_contentlet foreign key (mod_user) references user_(userid);
alter table htmlpage add constraint fk_user_htmlpage foreign key (mod_user) references user_(userid);
alter table containers add constraint fk_user_containers foreign key (mod_user) references user_(userid);
alter table template add constraint fk_user_template foreign key (mod_user) references user_(userid);
alter table file_asset add constraint fk_user_file_asset foreign key (mod_user) references user_(userid);
alter table links add constraint fk_user_links foreign key (mod_user) references user_(userid);

ALTER TABLE tag ALTER COLUMN host_id set default 'SYSTEM_HOST';
alter table tag add constraint tag_tagname_host unique (tagname, host_id);
alter table tag_inode add constraint fk_tag_inode_tagid foreign key (tag_id) references tag (tag_id);
