--postgres
CREATE INDEX idx_tree ON tree USING btree (child, parent, relation_type);
CREATE INDEX idx_tree_1 ON tree USING btree (parent);
CREATE INDEX idx_tree_2 ON tree USING btree (child);
CREATE INDEX idx_tree_3 ON tree USING btree (relation_type);
CREATE INDEX idx_tree_4 ON tree USING btree (parent, child, relation_type);
CREATE INDEX idx_tree_5 ON tree USING btree (parent, relation_type);
CREATE INDEX idx_tree_6 ON tree USING btree (child, relation_type);
CREATE INDEX idx_contentlet_1 ON contentlet USING btree (inode, live);
CREATE INDEX idx_contentlet_2 ON contentlet USING btree (inode, working);

CREATE INDEX idx_contentlet_3 ON contentlet USING btree (inode);

CREATE INDEX idx_identifier ON identifier USING btree (inode);
CREATE INDEX idx_permisision_4 ON permission USING btree (permission_type);

CREATE INDEX idx_permission_reference_2 ON permission_reference USING btree(reference_id);
CREATE INDEX idx_permission_reference_3 ON permission_reference USING btree(reference_id,permission_type);
CREATE INDEX idx_permission_reference_4 ON permission_reference USING btree(asset_id,permission_type);
CREATE INDEX idx_permission_reference_5 ON permission_reference USING btree(asset_id,reference_id,permission_type);
CREATE INDEX idx_permission_reference_6 ON permission_reference USING btree(permission_type);

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


ALTER TABLE structure ALTER fixed SET NOT NULL;
ALTER TABLE structure ALTER fixed SET DEFAULT false;

ALTER TABLE field ALTER fixed SET NOT NULL;
ALTER TABLE field ALTER fixed SET DEFAULT false;
ALTER TABLE field ALTER read_only SET NOT NULL;
ALTER TABLE field ALTER read_only SET DEFAULT false;

ALTER TABLE campaign ALTER active SET DEFAULT false;

insert into User_ (userId, companyId, createDate, password_, passwordEncrypted, passwordReset, firstName, middleName, lastName, male, birthday, emailAddress, skinId, dottedSkins, roundedSkins, greeting, layoutIds, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values ('dotcms.org.default', 'default', current_timestamp, 'password', 'f', 'f', '', '', '', 't', '01/01/1970', 'default@dotcms.org', '01', 'f', 'f', 'Welcome!', '', current_timestamp, 0, 'f', 't');
create index addres_userid_index on address(userid);
create index tag_user_id_index on tag(user_id);
create index tag_inode_tagid on tag_inode(tag_id);
create index tag_inode_inode on tag_inode(inode);
-- These two indexes are here instead of the hibernate file because Oracle by default creates an index on a unique field.  So creating an index would try to create the same index twice.
create index idx_chain_link_code_classname on chain_link_code (class_name);
create index idx_chain_key_name on chain (key_name);
CREATE TABLE dist_journal
(
  id bigserial NOT NULL,
  object_to_index character varying(1024) NOT NULL,
  serverid character varying(64),
  journal_type integer NOT NULL,
  time_entered timestamp without time zone NOT NULL,
  CONSTRAINT dist_journal_pkey PRIMARY KEY (id),
  CONSTRAINT dist_journal_object_to_index_key UNIQUE (object_to_index, serverid, journal_type)
);

create table plugin_property (
   plugin_id varchar(255) not null,
   propkey varchar(255) not null,
   original_value varchar(255) not null,
   current_value varchar(255) not null,
   primary key (plugin_id, propkey)
);
alter table plugin_property add constraint fk_plugin_plugin_property foreign key (plugin_id) references plugin(id);

CREATE TABLE dist_process ( id bigserial NOT NULL, object_to_index character varying(1024) NOT NULL, serverid character varying(64), journal_type integer NOT NULL, time_entered timestamp without time zone NOT NULL, CONSTRAINT dist_process_pkey PRIMARY KEY (id));
CREATE INDEX dist_process_index on dist_process (object_to_index, serverid,journal_type);

CREATE TABLE dist_reindex_journal
(
  id bigserial NOT NULL,
  inode_to_index character varying(100) NOT NULL,
  ident_to_index character varying(100) NOT NULL,
  serverid character varying(64),
  priority integer NOT NULL,
  time_entered timestamp without time zone NOT NULL DEFAULT CURRENT_DATE,
  index_val varchar(325),
  dist_action integer NOT NULL DEFAULT 1,
  CONSTRAINT dist_reindex_journal_pkey PRIMARY KEY (id)
);

CREATE INDEX dist_reindex_index1 on dist_reindex_journal (inode_to_index);
CREATE INDEX dist_reindex_index2 on dist_reindex_journal (dist_action);
CREATE INDEX dist_reindex_index3 on dist_reindex_journal (serverid);
CREATE INDEX dist_reindex_index4 on dist_reindex_journal (ident_to_index,serverid);
CREATE INDEX dist_reindex_index on dist_reindex_journal (serverid,dist_action);
CREATE INDEX dist_reindex_index5 ON dist_reindex_journal (priority, time_entered);
CREATE INDEX dist_reindex_index6 ON dist_reindex_journal (priority);


CREATE TABLE quartz_log (id bigserial NOT NULL, JOB_NAME character varying(255) NOT NULL, serverid character varying(64), time_started timestamp without time zone NOT NULL, CONSTRAINT quartz_log_pkey PRIMARY KEY (id));

alter table cms_role add CONSTRAINT cms_role_name_role_key UNIQUE (role_key);
alter table cms_role add CONSTRAINT cms_role_name_db_fqn UNIQUE (db_fqn);
alter table cms_role add constraint fkcms_role_parent foreign key (parent) references cms_role;

alter table users_cms_roles add CONSTRAINT users_cms_roles_parent1 UNIQUE (role_id,user_id);
alter table users_cms_roles add constraint fkusers_cms_roles1 foreign key (role_id) references cms_role; 
alter table users_cms_roles add constraint fkusers_cms_roles2 foreign key (user_id) references user_;
		
ALTER TABLE cms_layout add CONSTRAINT cms_layout_name_parent UNIQUE (layout_name);

alter table portlet add CONSTRAINT portlet_role_key UNIQUE (portletid);
alter table cms_layouts_portlets add CONSTRAINT cms_layouts_portlets_parent1 UNIQUE (layout_id,portlet_id);
alter table cms_layouts_portlets add constraint fkcms_layouts_portlets foreign key (layout_id) references cms_layout;

alter table layouts_cms_roles add constraint fklayouts_cms_roles1 foreign key (role_id) references cms_role; 
alter table layouts_cms_roles add constraint fklayouts_cms_roles2 foreign key (layout_id) references cms_layout;
alter table layouts_cms_roles add CONSTRAINT layouts_cms_roles_parent1 UNIQUE (role_id,layout_id);

alter table contentlet add constraint fk_folder foreign key (folder) references folder(inode);


CREATE OR REPLACE FUNCTION "boolIntResult"("intParam" integer, "boolParam" boolean)
  RETURNS boolean AS
$BODY$select case 
		WHEN $2 AND $1 != 0 then true
		WHEN $2 != true AND $1 = 0 then true
		ELSE false
	END$BODY$
  LANGUAGE 'sql' VOLATILE
;
CREATE OR REPLACE FUNCTION "intBoolResult"("boolParam" boolean, "intParam" integer)
  RETURNS boolean AS
$BODY$select case 
		WHEN $1 AND $2 != 0 then true
		WHEN $1 != true AND $2 = 0 then true
		ELSE false
	END$BODY$
  LANGUAGE 'sql' VOLATILE
 ; 
CREATE OPERATOR =(
  PROCEDURE = "intBoolResult",
  LEFTARG = bool,
  RIGHTARG = int4);
  
CREATE OPERATOR =(
  PROCEDURE = "boolIntResult",
  LEFTARG = int4,
  RIGHTARG = bool);
  
CREATE OR REPLACE FUNCTION "boolBigIntResult"("intParam" bigint, "boolParam" boolean)
  RETURNS boolean AS
$BODY$select case 
		WHEN $2 AND $1 != 0 then true
		WHEN $2 != true AND $1 = 0 then true
		ELSE false
	END$BODY$
  LANGUAGE 'sql' VOLATILE
;
CREATE OR REPLACE FUNCTION "bigIntBoolResult"("boolParam" boolean, "intParam" bigint)
  RETURNS boolean AS
$BODY$select case 
		WHEN $1 AND $2 != 0 then true
		WHEN $1 != true AND $2 = 0 then true
		ELSE false
	END$BODY$
  LANGUAGE 'sql' VOLATILE
;
CREATE OPERATOR =(
   PROCEDURE="bigIntBoolResult",
   LEFTARG=boolean,
   RIGHTARG=bigint);


CREATE OPERATOR =(
  PROCEDURE = "boolBigIntResult",
  LEFTARG = bigint,
  RIGHTARG = bool);
  
CREATE OR REPLACE FUNCTION content_work_version_check() RETURNS trigger AS '
DECLARE
	currentworkinginode varchar(100);
BEGIN
  IF tg_op = ''DELETE'' THEN
     RETURN OLD;
  END IF;
  IF tg_op = ''INSERT'' OR tg_op = ''UPDATE'' THEN
     select inode.inode into currentworkinginode from contentlet, inode where working = true and contentlet.inode = inode.inode and 
     inode.identifier = (select inode.identifier from inode where inode.inode = NEW.inode) and contentlet.language_id = NEW.language_id;
     IF FOUND AND NEW.working = true AND NEW.inode <> currentworkinginode THEN
	RAISE EXCEPTION ''Cannot insert/update multiple working versions in the contentlet table, Working inode: %'', currentworkinginode;
	RETURN NULL;
     ELSE
	RETURN NEW;
     END IF;
  END IF;

  RETURN NULL;
END
' LANGUAGE plpgsql;

CREATE TRIGGER content_work_version_trigger BEFORE INSERT OR UPDATE OR DELETE
    ON contentlet FOR EACH ROW 
    EXECUTE PROCEDURE content_work_version_check ();
  
CREATE OR REPLACE FUNCTION identifier_host_inode_check() RETURNS trigger AS '
DECLARE
	inodeType varchar(100);
BEGIN
  IF (tg_op = ''INSERT'' OR tg_op = ''UPDATE'') AND substr(NEW.uri, 0, 8) <> ''content'' AND 
		(NEW.host_inode IS NULL OR NEW.host_inode = '''') THEN
		RAISE EXCEPTION ''Cannot insert/update a null or empty host inode for this kind of identifier'';
		RETURN NULL;
  ELSE
		RETURN NEW;
  END IF;

  RETURN NULL;
END
' LANGUAGE plpgsql;

CREATE TRIGGER required_identifier_host_inode_trigger BEFORE INSERT OR UPDATE 
    ON identifier FOR EACH ROW 
    EXECUTE PROCEDURE identifier_host_inode_check ();
      
CREATE OR REPLACE FUNCTION file_asset_live_version_check()
  RETURNS trigger AS '
DECLARE    
     currentliveinode varchar(100);
BEGIN 
IF tg_op = ''DELETE'' THEN    
     RETURN OLD; 
END IF; 
IF tg_op = ''INSERT'' OR tg_op = ''UPDATE'' THEN    
     select inode.inode into currentliveinode
          from file_asset, inode
          where live = true
               and file_asset.inode = inode.inode
               and inode.identifier = (select inode.identifier from inode where inode.inode = NEW.inode);
     IF FOUND AND NEW.live = true AND NEW.inode <> currentliveinode THEN         
          RAISE EXCEPTION ''Cannot insert/update multiple live versions in the file_asset table,  inode: %'', currentliveinode;         
          RETURN NULL;    
     ELSE    
          RETURN NEW;    
     END IF;
END IF; 
RETURN NULL;
END
'  LANGUAGE plpgsql;

CREATE TRIGGER file_asset_live_version_trigger
  BEFORE INSERT OR UPDATE OR DELETE
  ON file_asset
  FOR EACH ROW
  EXECUTE PROCEDURE file_asset_live_version_check();

CREATE OR REPLACE FUNCTION content_live_version_check() 
	RETURNS trigger AS '
DECLARE
	currentliveinode varchar(100);
BEGIN
IF tg_op = ''DELETE'' THEN
     RETURN OLD;
END IF;
IF tg_op = ''INSERT'' OR tg_op = ''UPDATE'' THEN
	select inode.inode into currentliveinode from contentlet, inode where live = true and contentlet.inode = inode.inode and
		inode.identifier = (select inode.identifier from inode where inode.inode = NEW.inode) and contentlet.language_id = NEW.language_id;
	IF FOUND AND NEW.live = true AND NEW.inode <> currentliveinode THEN
		RAISE EXCEPTION ''Cannot insert/update multiple live versions in the contentlet table,  inode: %'', currentliveinode;
		RETURN NULL;
	ELSE
		RETURN NEW;
	END IF;
END IF;
RETURN NULL;
END
' LANGUAGE plpgsql;
				
CREATE TRIGGER content_live_version_trigger 
	BEFORE INSERT OR UPDATE OR DELETE
	ON contentlet FOR EACH ROW
	EXECUTE PROCEDURE content_live_version_check();
	
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
	
alter table category alter column category_velocity_var_name set not null;

alter table import_audit add column warnings text,
	add column errors text,
	add column results text,
	add column messages text;

alter table structure alter host set default 'SYSTEM_HOST';
alter table structure alter folder set default 'SYSTEM_FOLDER';
alter table structure add constraint fk_structure_folder foreign key (folder) references folder(inode);

alter table structure alter column velocity_var_name set not null;
alter table structure add constraint unique_struct_vel_var_name unique (velocity_var_name);

CREATE OR REPLACE FUNCTION structure_host_folder_check() RETURNS trigger AS '
DECLARE
    folderInode varchar(100);
    hostInode varchar(100);
BEGIN
    IF ((tg_op = ''INSERT'' OR tg_op = ''UPDATE'') AND (NEW.host IS NOT NULL AND NEW.host <> '''' AND NEW.host <> ''SYSTEM_HOST''
          AND NEW.folder IS NOT NULL AND NEW.folder <> ''SYSTEM_FOLDER'' AND NEW.folder <> '''')) THEN
          select folder.host_inode, folder.inode INTO hostInode, folderInode from folder where folder.inode = NEW.folder;
          IF (FOUND AND NEW.host = hostInode) THEN
                 RETURN NEW;
          ELSE
                 RAISE EXCEPTION ''Cannot assign host/folder to structure, folder does not belong to given host'';
                 RETURN NULL;
          END IF;
    ELSE
        IF((tg_op = ''INSERT'' OR tg_op = ''UPDATE'') AND (NEW.host IS NULL OR NEW.host = '''' OR NEW.host= ''SYSTEM_HOST''
           OR NEW.folder IS NULL OR NEW.folder = '''' OR NEW.folder = ''SYSTEM_FOLDER'')) THEN
          IF(NEW.host = ''SYSTEM_HOST'' OR NEW.host IS NULL OR NEW.host = '''') THEN
             NEW.host = ''SYSTEM_HOST'';
             NEW.folder = ''SYSTEM_FOLDER'';
          END IF;
          IF(NEW.folder = ''SYSTEM_FOLDER'' OR NEW.folder IS NULL OR NEW.folder = '''') THEN
             NEW.folder = ''SYSTEM_FOLDER'';
          END IF;
        RETURN NEW;
        END IF;
    END IF;
  RETURN NULL;
END
' LANGUAGE plpgsql;

CREATE TRIGGER structure_host_folder_trigger BEFORE INSERT OR UPDATE
    ON structure FOR EACH ROW
    EXECUTE PROCEDURE structure_host_folder_check();
	
CREATE OR REPLACE FUNCTION load_records_to_index(server_id character varying, records_to_fetch int)
  RETURNS SETOF dist_reindex_journal AS'
DECLARE
   c1 bigint;
   result_data dist_reindex_journal;
   dj dist_reindex_journal;
   cid character varying;
   last_time timestamp;
   first boolean;
   found_recs boolean;
BEGIN
	IF server_id = NULL THEN
	RAISE EXCEPTION ''YOU MUST PASS IN A SERVERID'';
	END IF;  
	c1 := 0;
	SELECT INTO last_time now();
	first := true;
	WHILE c1 < 11 LOOP
		IF c1 > 0 THEN
			RETURN;
		END IF;
		IF first = false THEN
			found_recs := false;
			FOR dj IN SELECT * from dist_reindex_journal  where (serverid = server_id AND dist_action = 2 and time_entered > last_time) OR (serverid = server_id and dist_action <> 3 and time_entered > last_time) ORDER BY priority ASC ,time_entered ASC LIMIT 1000 LOOP            
				found_recs := true;
				IF c1 > (records_to_fetch - 1) THEN
					RETURN;
				END IF;
				IF dj.dist_action = 2 THEN
					c1 := c1 + 1;
					RETURN NEXT DJ;
				ELSE  
					SELECT INTO cid inode FROM inode WHERE inode = dj.inode_to_index;
					IF FOUND THEN
						c1 := c1 + 1;
						RETURN NEXT DJ;
					END IF;
				END IF;
				SELECT INTO last_time dj.time_entered;
			END LOOP;
			IF found_recs = false THEN
				RETURN;
			END IF;
		ELSE
			FOR dj IN SELECT * from dist_reindex_journal  where (serverid = server_id AND dist_action = 2) OR (serverid = server_id and dist_action <> 3) ORDER BY priority ASC ,time_entered ASC LIMIT 1000 LOOP            
				IF c1 > (records_to_fetch - 1) THEN
					RETURN;
				END IF;
				IF dj.dist_action = 2 THEN
					c1 := c1 + 1;
					RETURN NEXT DJ;
				ELSE  
					SELECT INTO cid inode FROM inode WHERE inode = dj.inode_to_index;
					IF FOUND THEN
						c1 := c1 + 1;
						RETURN NEXT DJ;
					END IF;
				END IF;
				SELECT INTO last_time dj.time_entered;
			END LOOP;
			first := false;		
		END IF;
	END LOOP;
END
' LANGUAGE 'plpgsql';

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

ALTER TABLE tag ALTER COLUMN host_id set default 'SYSTEM_HOST';
alter table tag add constraint tag_tagname_host unique (tagname, host_id);
alter table tag_inode add constraint fk_tag_inode_tagid foreign key (tag_id) references tag (tag_id);
