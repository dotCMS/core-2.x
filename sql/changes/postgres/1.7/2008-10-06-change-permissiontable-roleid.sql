alter table permission add column new_roleid varchar(100);
update permission set new_roleid = cast(roleid as varchar(100));
alter table permission drop constraint permission_inode_id_key;
alter table permission drop column roleid;

alter table permission rename column new_roleid TO roleid;
ALTER TABLE permission ADD CONSTRAINT permission_inode_id_key UNIQUE(inode_id, roleid, permission);
