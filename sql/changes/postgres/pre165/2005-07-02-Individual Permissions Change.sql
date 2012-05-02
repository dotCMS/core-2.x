insert into permission 
(select distinct on (parent_id.parent, permission.roleid, permission.permission) nextval('permission_seq'), parent_id.parent, permission.roleid, permission.permission 
from inode, tree parent_folder, tree parent_id, permission
where 
(inode in (select inode from htmlpage) or inode in (select inode from containers) 
	or inode in (select inode from contentlet) or inode in (select inode from template) 
	or inode in (select inode from file_asset) or inode in (select inode from links)) 
and parent_folder.parent in (select inode from folder) and parent_folder.child = inode.inode
and parent_id.parent in (select inode from identifier) and parent_id.child = inode.inode
and permission.inode_id = parent_folder.parent
and not exists (select * from permission perm2 
	where perm2.inode_id = parent_id.parent and perm2.roleid = permission.roleid and perm2.permission = permission.permission));
