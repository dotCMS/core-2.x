-- The new table host to host multiple web roots
CREATE TABLE host (
    inode bigint not null,
    hostname varchar(255) not null,
    is_default boolean,
    aliases text
);

CREATE INDEX host_idx1 ON host USING btree (hostname);

ALTER TABLE ONLY host
    ADD CONSTRAINT host_pkey PRIMARY KEY (inode);

CREATE FUNCTION create_default_host() RETURNS VARCHAR(100) AS $$
DECLARE
	next_inode bigint;
	admin_id bigint;
	count_hosts int;
BEGIN
	SELECT INTO count_hosts count(*) FROM host WHERE is_default = true;
	IF count_hosts > 0
	THEN
		RETURN 'Default host already exists';
	END IF;
	SELECT INTO next_inode nextval('inode_seq');
	INSERT INTO host (inode, hostname, is_default, aliases) VALUES (next_inode, 'default', true, '');
	INSERT INTO inode (inode, type, owner, idate) values (next_inode, 'host', 0, current_date);
	
	--Adding permission to the cms administrator over the default host
	SELECT INTO admin_id roleid from role_ where name = 'CMS Administrator';
	insert into permission values (nextval('permission_seq'), next_inode, admin_id, 1);
	insert into permission values (nextval('permission_seq'), next_inode, admin_id, 2);
	insert into permission values (nextval('permission_seq'), next_inode, admin_id, 4);	

	RETURN 'Created default host with inode ' || next_inode;
END;
$$ LANGUAGE plpgsql;

SELECT 'Creating default host, msg: ' || create_default_host () as result;

DROP FUNCTION create_default_host ();

-- Altering table identifier to host multiple roots

ALTER TABLE identifier ADD COLUMN host_inode bigint;

UPDATE identifier SET host_inode = (SELECT inode FROM host WHERE hostname = 'default');

ALTER TABLE identifier ALTER COLUMN host_inode SET NOT NULL;

DROP INDEX identifier_idx1;

CREATE UNIQUE INDEX identifier_idx1
  ON identifier
  USING btree
  (host_inode, uri);


-- Removing the current root folder and updating all the current folders to point to the created default host

drop function update_current_folders_parent();

CREATE FUNCTION update_current_folders_parent() RETURNS VARCHAR(100) AS $$
DECLARE
	default_host_inode bigint;
	root_inode bigint;
	count_roots int;
BEGIN
	SELECT INTO count_roots count(*) FROM folder WHERE name = 'Home';
	IF count_roots = 0
	THEN
		RETURN 'Current home folder not found';
	END IF;
	SELECT INTO root_inode inode FROM folder WHERE name = 'Home';
	SELECT INTO default_host_inode inode FROM host WHERE is_default = true;
	UPDATE tree SET parent = default_host_inode where parent = root_inode;
	RETURN 'Updated all the folders Successfully';
END;
$$ LANGUAGE plpgsql;

SELECT 'Updating all folders, msg: ' || update_current_folders_parent () as result;
