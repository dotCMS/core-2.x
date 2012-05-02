insert into permission (id,inode_id, roleid, permission)
(
select PERMISSION_SEQ.NEXTVAL ,inode.identifier,
(select roleid from role_ r where r.name like 'CMS Anonymous' and
companyid = 'dotcms.org'), 1
from contentlet, inode
where contentlet.inode = inode.inode and inode.identifier not in (
select inode_id from permission where permission = 1 and roleid =
(select roleid from role_ r where r.name like 'CMS Anonymous' and
companyid = 'dotcms.org'))
)
