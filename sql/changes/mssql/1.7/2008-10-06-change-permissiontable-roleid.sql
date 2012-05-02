alter table permission add new_roleid varchar(100);
update permission set new_roleid = cast(roleid as varchar(100));
alter table permission drop constraint permission_inode_id_key;
alter table permission drop column roleid;
-- run as a seperate query
EXEC SP_RENAME 'dbo.permission.new_roleid','roleid','COLUMN';
ALTER TABLE permission ADD CONSTRAINT permission_inode_id_key UNIQUE(inode_id, roleid, permission);