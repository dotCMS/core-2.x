alter table permission add role_id_dummy varchar(100);
alter table permission drop index inode_id;
update permission set role_id_dummy = cast(roleid as String);
alter table permission drop column roleid;
alter table permission change role_id_dummy roleId varchar(100);
alter table permission add constraint permission_inode_id_key UNIQUE(inode_id, permission, roleid);