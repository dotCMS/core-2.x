alter table permission add role_id_dummy varchar2(100);
alter table permission drop unique (inode_id, roleid, permission);
update permission set role_id_dummy = cast(roleid as varchar2(100));
alter table permission drop column roleid;
alter table permission rename column role_id_dummy to roleid;
alter table permission add constraint permission_inode_id_key UNIQUE(inode_id, permission, roleid);