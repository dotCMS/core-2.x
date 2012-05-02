update identifier 
set uri = (inode.type || '.' || inode.inode)
from inode
where identifier.uri is null and identifier.inode = inode.inode;

alter table identifier alter column uri set not null;