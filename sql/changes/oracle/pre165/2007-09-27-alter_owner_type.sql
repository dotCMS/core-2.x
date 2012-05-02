ALTER TABLE inode ADD ownertmp varchar2(100);
update inode i1 set ownertmp = (select owner from inode i2 where i2.inode = i1.inode);
ALTER TABLE inode DROP COLUMN owner;
ALTER TABLE inode RENAME COLUMN ownertmp TO owner;