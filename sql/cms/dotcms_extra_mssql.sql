--mssql
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

ALTER TABLE structure ALTER COLUMN fixed tinyint NOT NULL;
alter table structure add CONSTRAINT [DF_structure_fixed]  DEFAULT ((0)) for fixed;

ALTER TABLE field ALTER COLUMN fixed tinyint NOT NULL; 
ALTER TABLE field ALTER COLUMN read_only tinyint  NOT NULL;
ALTER TABLE campaign ALTER COLUMN active tinyint NOT NULL; 
alter table field add CONSTRAINT [DF_field_fixed]  DEFAULT ((0)) for fixed;
alter table field add CONSTRAINT [DF_field_read_only]  DEFAULT ((0)) for read_only;

insert into User_ (userId, companyId, createDate, password_, passwordEncrypted, passwordReset, firstName, middleName, lastName, male, birthday, emailAddress, skinId, dottedSkins, roundedSkins, greeting, layoutIds, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values ('dotcms.org.default', 'default', GetDate(), 'password', '0', '0', '', '', '', '1', '19700101', 'default@dotcms.org', '01', '0', '0', 'Welcome!', '', GetDate(), 0, '0', '1');

create index addres_userid_index on address(userid);
create index tag_user_id_index on tag(user_id);
create index tag_inode_tagid on tag_inode(tag_id);
create index tag_inode_inode on tag_inode(inode);
CREATE TABLE dist_journal
	   (
       id bigint NOT NULL IDENTITY (1, 1),
       object_to_index VARCHAR(1024) NOT NULL,
       serverid varchar(64) NOT NULL,
       journal_type int NOT NULL,
       time_entered datetime NOT NULL
       ) ;

ALTER TABLE dist_journal ADD CONSTRAINT
       PK_dist_journal PRIMARY KEY CLUSTERED
       (
       id
       );


ALTER TABLE dist_journal ADD CONSTRAINT
       IX_dist_journal UNIQUE NONCLUSTERED
       (
       object_to_index,
       serverid,
       journal_type
       );
CREATE TABLE dist_process ( id bigint NOT NULL IDENTITY (1, 1), object_to_index varchar(1024) NOT NULL, serverid varchar(64) NOT NULL, journal_type int NOT NULL, time_entered datetime NOT NULL ) ;
ALTER TABLE dist_process ADD CONSTRAINT PK_dist_process PRIMARY KEY CLUSTERED ( id);
	
create table plugin_property (
   plugin_id varchar(255) not null,
   propkey varchar(255) not null,
   original_value varchar(255) not null,
   current_value varchar(255) not null,
   primary key (plugin_id, propkey)
);

alter table plugin_property add constraint fk_plugin_plugin_property foreign key (plugin_id) references plugin(id);

CREATE TABLE dist_reindex_journal ( id bigint NOT NULL IDENTITY (1, 1), inode_to_index varchar(100) NOT NULL,ident_to_index varchar(100) NOT NULL, serverid varchar(64) NOT NULL, priority int NOT NULL, time_entered datetime DEFAULT getDate(), index_val varchar(325),dist_action integer NOT NULL DEFAULT 1);
	
CREATE INDEX dist_reindex_index1 on dist_reindex_journal (inode_to_index);
CREATE INDEX dist_reindex_index2 on dist_reindex_journal (dist_action);
CREATE INDEX dist_reindex_index3 on dist_reindex_journal (serverid);
CREATE INDEX dist_reindex_index4 on dist_reindex_journal (ident_to_index,serverid);
CREATE INDEX dist_reindex_index on dist_reindex_journal (serverid,dist_action);
CREATE INDEX dist_reindex_index5 ON dist_reindex_journal (priority, time_entered);
CREATE INDEX dist_reindex_index6 ON dist_reindex_journal (priority);


ALTER TABLE dist_reindex_journal ADD CONSTRAINT PK_dist_reindex_journal PRIMARY KEY CLUSTERED ( id);

CREATE TABLE quartz_log ( id bigint NOT NULL IDENTITY (1, 1), JOB_NAME varchar(255) NOT NULL, serverid varchar(64) , time_started datetime NOT NULL, primary key (id));

CREATE TRIGGER check_role_key_uniqueness
ON cms_role
FOR INSERT, UPDATE
AS
DECLARE @c varchar(100)
SELECT @c = count(*)
FROM cms_role e INNER JOIN inserted i ON i.role_key = e.role_key WHERE i.role_key IS NOT NULL AND i.id <> e.id
IF (@c > 0)  
BEGIN
   RAISERROR ('Duplicated role key.', 16, 1)
   ROLLBACK TRANSACTION
END;

CREATE TRIGGER check_identifier_host_inode
ON identifier
FOR INSERT, UPDATE AS
DECLARE @uri varchar(10) 
DECLARE @hostInode varchar(50)
DECLARE cur_Inserted cursor for
 Select uri, host_inode
 from inserted 
 for Read Only
open cur_Inserted
fetch next from cur_Inserted into @uri,@hostInode 
while @@FETCH_STATUS <> -1
BEGIN
 IF(@uri <> 'content' AND (@hostInode is null OR @hostInode = ''))
 BEGIN
	RAISERROR (N'Cannot insert/update a null or empty host inode for this kind of identifier', 10, 1)
	ROLLBACK WORK
 END
fetch next from cur_Inserted into @uri,@hostInode
END;

ALTER TABLE cms_role ADD CONSTRAINT IX_cms_role2 UNIQUE NONCLUSTERED (db_fqn);
alter table cms_role add constraint fkcms_role_parent foreign key (parent) references cms_role;

ALTER TABLE users_cms_roles ADD CONSTRAINT IX_cms_role UNIQUE NONCLUSTERED (role_id, user_id);
alter table users_cms_roles add constraint fkusers_cms_roles1 foreign key (role_id) references cms_role;
alter table users_cms_roles add constraint fkusers_cms_roles2 foreign key (user_id) references user_;

ALTER TABLE cms_layout ADD CONSTRAINT IX_cms_layout UNIQUE NONCLUSTERED (layout_name); 

ALTER TABLE portlet ADD CONSTRAINT IX_portletid UNIQUE NONCLUSTERED (portletid);

ALTER TABLE cms_layouts_portlets ADD CONSTRAINT IX_cms_layouts_portlets UNIQUE NONCLUSTERED (portlet_id, layout_id); 
alter table cms_layouts_portlets add constraint fklcms_layouts_portlets foreign key (layout_id) references cms_layout;

ALTER TABLE layouts_cms_roles ADD CONSTRAINT IX_layouts_cms_roles UNIQUE NONCLUSTERED (role_id, layout_id); 
alter table layouts_cms_roles add constraint fklayouts_cms_roles1 foreign key (role_id) references cms_role;
alter table layouts_cms_roles add constraint fklayouts_cms_roles2 foreign key (layout_id) references cms_layout;

alter table contentlet add constraint fk_folder foreign key (folder) references folder(inode);


create table dist_reindex_lock (dummy int);
create table dist_lock (dummy int);
insert into dist_reindex_lock (dummy) values (1);
insert into dist_lock (dummy) values (1);

create table import_audit (
	id bigint not null,
	start_date datetime,
	userid varchar(255), 
	filename varchar(512),
	status int,
	last_inode varchar(100),
	records_to_import bigint,
	serverid varchar(255),
	primary key (id)
	);
	
alter table category alter column category_velocity_var_name varchar(255) not null;

alter table import_audit add warnings text,
	errors text,
	results text,
	messages text;

alter table structure add CONSTRAINT [DF_structure_host] DEFAULT 'SYSTEM_HOST' for host;
alter table structure add CONSTRAINT [DF_structure_folder] DEFAULT 'SYSTEM_FOLDER' for folder;
alter table structure add CONSTRAINT [CK_structure_host] CHECK(host <> '' AND host IS NOT NULL)
alter table structure add constraint fk_structure_folder foreign key (folder) references folder(inode);

alter table structure alter column velocity_var_name varchar(255) not null;
alter table structure add constraint unique_struct_vel_var_name unique (velocity_var_name);

CREATE TRIGGER structure_host_folder_trigger
ON structure
FOR INSERT, UPDATE AS
DECLARE @newFolder varchar(100)
DECLARE @newHost varchar(100)
DECLARE @folderInode varchar(100)
DECLARE @hostInode varchar(100)
DECLARE cur_Inserted cursor for
 Select folder, host
 from inserted
 for Read Only
open cur_Inserted
fetch next from cur_Inserted into @newFolder,@newHost
while @@FETCH_STATUS <> -1
BEGIN
   IF (@newHost <> 'SYSTEM_HOST' AND @newFolder <> 'SYSTEM_FOLDER')
   BEGIN
          SELECT @hostInode = folder.host_inode, @folderInode = folder.inode from folder where folder.inode = @newFolder
      IF (@folderInode IS NULL OR @folderInode = '' OR @newHost <> @hostInode)
      BEGIN
            RAISERROR (N'Cannot assign host/folder to structure, folder does not belong to given host', 10, 1)
            ROLLBACK WORK
          END
  END
fetch next from cur_Inserted into @newFolder,@newHost
END;

	
CREATE FUNCTION load_records_to_index(@server_id VARCHAR, @records_to_fetch INT)
RETURNS  @dj TABLE (id bigint, inode_to_index varchar(100), ident_to_index varchar(100), serverid varchar(64), priority int, time_entered datetime, index_val varchar(325), dist_action int)
AS 
BEGIN
DECLARE @id bigint
DECLARE @inode_to_index varchar(100)
DECLARE @ident_to_index varchar(100)
DECLARE @serverid varchar(64)
DECLARE @priority int
DECLARE @time_entered datetime
DECLARE @index_val varchar(325)
DECLARE @dist_action int
DECLARE @c1 bigint
DECLARE @cid varchar(100)
DECLARE @last_time datetime
DECLARE @first tinyint
DECLARE @found_recs tinyint
DECLARE @inode_count int
    SET @c1 = 0
    SET @last_time = GETDATE()
	SET @first = 1
    WHILE (@c1 < 11)
    BEGIN
		IF (@c1 > 0) 
        BEGIN
			RETURN
		END
        IF (@first = 1)
        BEGIN
	      DECLARE dj_cursor CURSOR LOCAL FOR
            SELECT id, inode_to_index, ident_to_index, serverid, priority, time_entered, index_val, dist_action FROM ( 
              SELECT *, ROW_NUMBER() OVER (ORDER BY priority ASC ,time_entered ASC) as row FROM dist_reindex_journal
               WHERE (serverid = @server_id AND dist_action = 2) OR (serverid = @server_id and dist_action <> 3) 
             ) a WHERE row <= 1000
            OPEN dj_cursor
            FETCH NEXT FROM dj_cursor INTO @id, @inode_to_index, @ident_to_index, @serverid, @priority, @time_entered, @index_val, @dist_action 
	        WHILE @@FETCH_STATUS = 0 
		    BEGIN
				IF (@c1 > (@records_to_fetch - 1))
                BEGIN
					RETURN
				END
				IF(@dist_action = 2)
				BEGIN
				  SET @c1 = @c1 + 1
                  INSERT INTO @dj VALUES(@id, @inode_to_index, @ident_to_index, @serverid, @priority, @time_entered, @index_val, @dist_action)
				END
				ELSE
				BEGIN
				  SELECT @inode_count = COUNT(inode.inode) FROM inode WHERE inode.inode = @inode_to_index
				  IF (@inode_count >0) 
                  BEGIN
					SET @c1 = @c1 + 1
                    INSERT INTO @dj VALUES(@id, @inode_to_index, @ident_to_index, @serverid, @priority, @time_entered, @index_val, @dist_action)
				  END
				END	
				SET @last_time = @time_entered
			FETCH NEXT FROM dj_cursor INTO @id, @inode_to_index, @ident_to_index, @serverid, @priority, @time_entered, @index_val, @dist_action 
		    END
	        CLOSE dj_cursor
	        DEALLOCATE dj_cursor
			SET @first = 0
		END
        ELSE
			SET @found_recs = 0
            DECLARE dj_cursor CURSOR LOCAL FOR
            SELECT id, inode_to_index, ident_to_index, serverid, priority, time_entered, index_val, dist_action FROM ( 
              SELECT *, ROW_NUMBER() OVER (ORDER BY priority ASC ,time_entered ASC) as row FROM dist_reindex_journal
               WHERE (serverid = @server_id AND dist_action = 2 and time_entered > @last_time) OR (serverid = @server_id and dist_action <> 3 and time_entered > @last_time) 
             ) a WHERE row <= 1000
            OPEN dj_cursor 
            FETCH NEXT FROM dj_cursor INTO @id, @inode_to_index, @ident_to_index, @serverid, @priority, @time_entered, @index_val, @dist_action 
	        WHILE @@FETCH_STATUS = 0 
		    BEGIN
			SET @found_recs = 1
				IF (@c1 > (@records_to_fetch - 1))
                BEGIN
					RETURN
				END
				IF(@dist_action = 2)
				BEGIN
				  SET @c1 = @c1 + 1
                  INSERT INTO @dj VALUES(@id, @inode_to_index, @ident_to_index, @serverid, @priority, @time_entered, @index_val, @dist_action)
				END
				ELSE
				BEGIN
				  SELECT @inode_count = COUNT(inode.inode) FROM inode WHERE inode.inode = @inode_to_index
				  IF (@inode_count >0) 
                  BEGIN
					SET @c1 = @c1 + 1
                    INSERT INTO @dj VALUES(@id, @inode_to_index, @ident_to_index, @serverid, @priority, @time_entered, @index_val, @dist_action)
				  END
				END	
				SET @last_time = @time_entered
			FETCH NEXT FROM dj_cursor INTO @id, @inode_to_index, @ident_to_index, @serverid, @priority, @time_entered, @index_val, @dist_action 
		    END
	        CLOSE dj_cursor 
	        DEALLOCATE dj_cursor
            IF (@found_recs = 0)
            BEGIN
				RETURN
			END
	   END
  RETURN
END;

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

ALTER TABLE tag add CONSTRAINT [DF_tag_host] DEFAULT 'SYSTEM_HOST' for host_id;
alter table tag add constraint tag_tagname_host unique (tagname, host_id);
alter table tag_inode add constraint fk_tag_inode_tagid foreign key (tag_id) references tag (tag_id);
