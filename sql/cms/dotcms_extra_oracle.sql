--oracle
CREATE INDEX idx_tree_1 ON tree (parent);
CREATE INDEX idx_tree_2 ON tree (child);
CREATE INDEX idx_tree_3 ON tree (relation_type);
CREATE INDEX idx_tree_4 ON tree (parent, child, relation_type);
CREATE INDEX idx_tree_5 ON tree (parent, relation_type);
CREATE INDEX idx_tree_6 ON tree (child, relation_type);
CREATE INDEX idx_contentlet_1 ON contentlet (inode, live);
CREATE INDEX idx_contentlet_2 ON contentlet (inode, working);



CREATE INDEX idx_permisision_4 ON permission (permission_type);


CREATE INDEX idx_permission_reference_2 ON permission_reference (reference_id);
CREATE INDEX idx_permission_reference_3 ON permission_reference (reference_id,permission_type);
CREATE INDEX idx_permission_reference_4 ON permission_reference (asset_id,permission_type);
CREATE INDEX idx_permission_reference_5 ON permission_reference (asset_id,reference_id,permission_type);
CREATE INDEX idx_permission_reference_6 ON permission_reference (permission_type);

CREATE UNIQUE INDEX idx_field_velocity_structure ON field (velocity_var_name,structure_inode); 

alter table tree add constraint FK36739EC4AB08AA foreign key (parent) references inode;
alter table tree add constraint FK36739E5A3F51C foreign key (child) references inode;

alter table chain_state add constraint fk_state_chain foreign key (chain_id) references chain(id);
alter table chain_state add constraint fk_state_code foreign key (link_code_id) references chain_link_code(id);
alter table chain_state_parameter add constraint fk_parameter_state foreign key (chain_state_id) references chain_state(id);

alter table permission add constraint permission_inode_fk foreign key (inode_id) references inode(inode);
alter table permission add constraint permission_role_fk foreign key (roleid) references cms_role(id);

alter table permission_reference add constraint permission_asset_id_fk foreign key (asset_id) references inode(inode);
alter table permission_reference add constraint permission_reference_id_fk foreign key (reference_id) references inode(inode);

alter table contentlet add constraint FK_structure_inode foreign key (structure_inode) references structure(inode);

ALTER TABLE structure MODIFY fixed DEFAULT 0;

ALTER TABLE field MODIFY fixed DEFAULT 0;
ALTER TABLE field MODIFY read_only DEFAULT 0;

ALTER TABLE campaign MODIFY active DEFAULT 0;

insert into User_ (userId, companyId, createDate, password_, passwordEncrypted, passwordReset, firstName, middleName, lastName, male, birthday, emailAddress, skinId, dottedSkins, roundedSkins, greeting, layoutIds, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values ('dotcms.org.default', 'default', sysdate, 'password', '0', '0', '', '', '', '1', to_date('1970-01-01 00:00:00','YYYY-MM-DD HH24:MI:SS'), 'default@dotcms.org', '01', '0', '0', 'Welcome!', '', sysdate, 0, '0', '1');

create index addres_userid_index on address(userid);
create index tag_user_id_index on tag(user_id);
create index tag_inode_tagid on tag_inode(tag_id);
create index tag_inode_inode on tag_inode(inode);
CREATE TABLE "DIST_JOURNAL" ( "ID" INTEGER NOT NULL ,
"OBJECT_TO_INDEX" VARCHAR2(255), "SERVERID" VARCHAR2(64),
"JOURNAL_TYPE" INTEGER, "TIME_ENTERED" TIMESTAMP, PRIMARY KEY ("ID")
VALIDATE , UNIQUE ("OBJECT_TO_INDEX", "SERVERID", "JOURNAL_TYPE")
VALIDATE );
CREATE SEQUENCE dist_journal_id_seq
START WITH 1
INCREMENT BY 1;
create trigger DIST_JOURNAL_trg
before insert on DIST_JOURNAL
for each row
when (new.id is null)
begin
    select dist_journal_id_seq.nextval into :new.id from dual;
end;
/

CREATE TABLE dist_process ( ID INTEGER NOT NULL , OBJECT_TO_INDEX VARCHAR2(255), SERVERID VARCHAR2(64), JOURNAL_TYPE INTEGER, TIME_ENTERED TIMESTAMP, PRIMARY KEY (ID) VALIDATE ); 
CREATE SEQUENCE dist_process_id_seq START WITH 1 INCREMENT BY 1;
create trigger dist_process_trg
before insert on dist_process 
for each row 
when (new.id is null) 
begin 
select dist_process_id_seq.nextval into :new.id from dual;
end;
/

CREATE INDEX dist_process_index on dist_process (object_to_index, serverid,journal_type);


CREATE TABLE dist_reindex_journal ( ID INTEGER NOT NULL , INODE_TO_INDEX varchar2(100),IDENT_TO_INDEX varchar2(100), SERVERID VARCHAR2(64), priority INTEGER, TIME_ENTERED TIMESTAMP DEFAULT CURRENT_TIMESTAMP, index_val varchar2(325) ,dist_action INTEGER DEFAULT 1 NOT NULL, PRIMARY KEY (ID) VALIDATE);
		
CREATE INDEX dist_reindex_index1 on dist_reindex_journal (inode_to_index);
CREATE INDEX dist_reindex_index2 on dist_reindex_journal (dist_action);
CREATE INDEX dist_reindex_index3 on dist_reindex_journal (serverid);
CREATE INDEX dist_reindex_index4 on dist_reindex_journal (ident_to_index,serverid);
CREATE INDEX dist_reindex_index on dist_reindex_journal (serverid,dist_action);
CREATE INDEX dist_reindex_index5 ON dist_reindex_journal (priority, time_entered);
CREATE INDEX dist_reindex_index6 ON dist_reindex_journal (priority);

CREATE SEQUENCE dist_reindex_id_seq START WITH 1 INCREMENT BY 1;
		
create trigger dist_reindex_journal_trg
		before insert on dist_reindex_journal
		for each row
		when (new.id is null)
		begin
		select dist_reindex_id_seq.nextval into :new.id from dual;
		end;
/
		

CREATE TABLE quartz_log ( ID INTEGER NOT NULL , JOB_NAME VARCHAR2(255), SERVERID VARCHAR2(64),  TIME_STARTED TIMESTAMP, PRIMARY KEY (ID) VALIDATE ); 

create table plugin_property (
   plugin_id varchar2(255) not null,
   propkey varchar2(255) not null,
   original_value varchar2(255) not null,
   current_value varchar2(255) not null,
   primary key (plugin_id, propkey)
);

alter table plugin_property add constraint fk_plugin_plugin_property foreign key (plugin_id) references plugin(id);

CREATE SEQUENCE quartz_log_id_seq START WITH 1 INCREMENT BY 1;
create trigger quartz_log_trg
before insert on quartz_log 
for each row 
when (new.id is null) 
begin 
select quartz_log_id_seq.nextval into :new.id from dual;
end;
/

CREATE OR REPLACE TRIGGER check_identifier_host_inode
BEFORE INSERT OR UPDATE ON identifier
FOR EACH ROW
DECLARE
BEGIN
    dbms_output.put_line('uri: ' || SUBSTR(:new.uri,0,7));
    dbms_output.put_line('host_inode: ' || :new.host_inode);
    IF SUBSTR(:new.uri,0,7) <> 'content' AND (:new.host_inode is NULL OR :new.host_inode = '') THEN
    	RAISE_APPLICATION_ERROR(-20000, 'Cannot insert/update a null or empty host inode for this kind of identifier');
    END IF;
END;
/

ALTER TABLE cms_role ADD CONSTRAINT cms_role2_unique UNIQUE (role_key);
ALTER TABLE cms_role ADD CONSTRAINT cms_role3_unique UNIQUE (db_fqn);
alter table cms_role add constraint fkcms_role_parent foreign key (parent) references cms_role;

ALTER TABLE cms_layout ADD CONSTRAINT cms_layout_unique_1 UNIQUE (layout_name);

ALTER TABLE portlet ADD CONSTRAINT portlet_unique_1 UNIQUE (portletid);
ALTER TABLE cms_layouts_portlets ADD CONSTRAINT cms_layouts_portlets_unq_1 UNIQUE (portlet_id, layout_id);
alter table cms_layouts_portlets add constraint fkcms_layouts_portlets foreign key (layout_id) references cms_layout;

ALTER TABLE users_cms_roles ADD CONSTRAINT users_cms_roles1_unique UNIQUE (role_id, user_id);
alter table users_cms_roles add constraint fkusers_cms_roles1 foreign key (role_id) references cms_role;
alter table users_cms_roles add constraint fkusers_cms_roles2 foreign key (user_id) references user_;
		
ALTER TABLE layouts_cms_roles ADD CONSTRAINT layouts_cms_roles1_unique UNIQUE (role_id, layout_id);		
alter table layouts_cms_roles add constraint fklayouts_cms_roles1 foreign key (role_id) references cms_role;
alter table layouts_cms_roles add constraint fklayouts_cms_roles2 foreign key (layout_id) references cms_layout;

alter table contentlet add constraint fk_folder foreign key (folder) references folder(inode);


create table import_audit ( 
	id integer not null,
	start_date timestamp,
	userid varchar(255), 
	filename varchar(512), 
	status integer,
	last_inode varchar(100), 
	records_to_import integer,
	serverid varchar(255),
	primary key (id)
	);

alter table category modify (category_velocity_var_name varchar2(255) not null);

alter table import_audit add( warnings nclob,
	errors nclob,
	results nclob,
	messages nclob);
	
CREATE TYPE dj_table AS OBJECT
(    
     ID NUMBER, 
     INODE_TO_INDEX VARCHAR2(100), 
     IDENT_TO_INDEX VARCHAR2(100), 
     SERVERID VARCHAR2(64), 
     PRIORITY NUMBER, 
     TIME_ENTERED TIMESTAMP, 
     INDEX_VAL VARCHAR2(325), 
     DIST_ACTION  NUMBER
);
CREATE TYPE dj_table_type AS TABLE OF dj_table;

alter table structure modify host default 'SYSTEM_HOST';
alter table structure modify folder default 'SYSTEM_FOLDER';
alter table structure add constraint fk_structure_folder foreign key (folder) references folder(inode);

alter table structure modify (velocity_var_name varchar2(255) not null);
alter table structure add constraint unique_struct_vel_var_name unique (velocity_var_name);


CREATE OR REPLACE TRIGGER structure_host_folder_trigger
BEFORE INSERT OR UPDATE ON structure
FOR EACH ROW
DECLARE
   folderInode varchar2(100);
   hostInode varchar2(100);
BEGIN
    IF (:NEW.host <> 'SYSTEM_HOST' AND :NEW.folder <> 'SYSTEM_FOLDER') THEN
        select folder.host_inode, folder.inode INTO hostInode, folderInode from folder where folder.inode = :NEW.folder;
          IF (:NEW.host <> hostInode) THEN
                RAISE_APPLICATION_ERROR(-20000, 'Cannot assign host/folder to structure, folder does not belong to given host');
          END IF;
    ELSE
       IF(:NEW.host IS NULL OR :NEW.host = '' OR :NEW.host = 'SYSTEM_HOST' OR :NEW.folder IS NULL OR :NEW.folder = '' OR :NEW.folder = 'SYSTEM_FOLDER') THEN
          IF(:NEW.host = 'SYSTEM_HOST' OR :NEW.host IS NULL OR :NEW.host = '') THEN
               :NEW.host := 'SYSTEM_HOST';
               :NEW.folder := 'SYSTEM_FOLDER';
          END IF;
          IF(:NEW.folder = 'SYSTEM_FOLDER' OR :NEW.folder IS NULL OR :NEW.folder = '') THEN
             :NEW.folder := 'SYSTEM_FOLDER';
          END IF;
       END IF;
    END IF;
END;
/


CREATE OR REPLACE FUNCTION load_records_to_index(server_id VARCHAR2, records_to_fetch NUMBER)
   RETURN dj_table_type IS
   v_dj  dj_table_type := dj_table_type();
   c1 NUMBER;
   v_count NUMBER;
   cid VARCHAR2(100);
   last_time TIMESTAMP;
   first BOOLEAN;
   found_recs BOOLEAN;
BEGIN
	IF (server_id = NULL) THEN
          RAISE_APPLICATION_ERROR(-20000, 'YOU MUST PASS IN A SERVERID');
	END IF;
        c1 := 0;
        SELECT SYSDATE INTO last_time FROM DUAL; 
        first := true;
        WHILE (c1 < 11) LOOP
        IF (c1 > 0) THEN
            EXIT;
        END IF;
 IF (first = false) THEN
            found_recs := false;
            FOR dj IN (SELECT * from dist_reindex_journal  where (serverid = server_id AND dist_action = 2 and time_entered > last_time) OR (serverid = server_id and dist_action <> 3 and time_entered > last_time) AND rownum <= 1000 ORDER BY priority ASC ,time_entered ASC) LOOP            
                found_recs := true;
                IF (c1 > (records_to_fetch - 1)) THEN
                    EXIT;
                END IF;
                IF(dj.dist_action=2) THEN
                   v_dj.extend;
                   c1 := c1 + 1;
                   v_dj(c1) := dj_table(dj.id, dj.inode_to_index, dj.ident_to_index, dj.serverid, dj.priority,dj.time_entered, dj.index_val, dj.dist_action);
                ELSE
                   SELECT COUNT(inode.inode) INTO v_count FROM inode WHERE inode = dj.inode_to_index;
                   IF (v_count>0) THEN
                      v_dj.extend;
                      c1 := c1 + 1;
                      v_dj(c1) := dj_table(dj.id, dj.inode_to_index, dj.ident_to_index, dj.serverid, dj.priority,dj.time_entered, dj.index_val, dj.dist_action);
                   END IF;
                END IF;
                last_time := dj.time_entered;
            END LOOP;
            IF (found_recs = false) THEN
                EXIT;
            END IF;
         ELSE
            FOR dj IN (SELECT * from dist_reindex_journal  where (serverid = server_id AND dist_action = 2) OR (serverid = server_id and dist_action <> 3) AND rownum <= 1000 ORDER BY priority ASC ,time_entered ASC) LOOP            
                IF c1 > (records_to_fetch - 1) THEN
                   EXIT; 
                END IF;
                IF(dj.dist_action=2) THEN
                   v_dj.extend;
                   c1 := c1 + 1;
                   v_dj(c1) := dj_table(dj.id, dj.inode_to_index, dj.ident_to_index, dj.serverid, dj.priority,dj.time_entered, dj.index_val, dj.dist_action);
                ELSE
                   SELECT COUNT(inode.inode) INTO v_count FROM inode WHERE inode = dj.inode_to_index;
                   IF (v_count>0) THEN
                      v_dj.extend;
                      c1 := c1 + 1;
                      v_dj(c1) := dj_table(dj.id, dj.inode_to_index, dj.ident_to_index, dj.serverid, dj.priority,dj.time_entered, dj.index_val, dj.dist_action);
                   END IF;
                END IF;
                last_time := dj.time_entered;
            END LOOP;
            first := false;              
        END IF;
    END LOOP; 
RETURN v_dj; 
END;
/

ALTER TABLE clickstream MODIFY start_date TIMESTAMP;
ALTER TABLE clickstream MODIFY end_date TIMESTAMP;
ALTER TABLE clickstream_request MODIFY timestampper TIMESTAMP;
ALTER TABLE clickstream_404 MODIFY timestampper TIMESTAMP;
ALTER TABLE analytic_summary_period MODIFY full_date TIMESTAMP;
ALTER TABLE analytic_summary MODIFY avg_time_on_site TIMESTAMP;
ALTER TABLE analytic_summary_workstream MODIFY mod_date TIMESTAMP;
ALTER TABLE analytic_summary_visits MODIFY visit_time TIMESTAMP;
ALTER TABLE dashboard_user_preferences MODIFY mod_date TIMESTAMP;

alter table structure add constraint fk_structure_host foreign key (host) references inode(inode);

create index idx_template3 on template (lower(title));
create index idx_template4 on template (working);
create index idx_template5 on template (deleted);

CREATE INDEX idx_contentlet_4 ON contentlet (structure_inode);

alter table contentlet add constraint fk_user_contentlet foreign key (mod_user) references user_(userid);
alter table htmlpage add constraint fk_user_htmlpage foreign key (mod_user) references user_(userid);
alter table containers add constraint fk_user_containers foreign key (mod_user) references user_(userid);
alter table template add constraint fk_user_template foreign key (mod_user) references user_(userid);
alter table file_asset add constraint fk_user_file_asset foreign key (mod_user) references user_(userid);
alter table links add constraint fk_user_links foreign key (mod_user) references user_(userid);

ALTER table tag MODIFY host_id default 'SYSTEM_HOST';
alter table tag add constraint tag_tagname_host unique (tagname, host_id);
alter table tag_inode add constraint fk_tag_inode_tagid foreign key (tag_id) references tag (tag_id);
