ALTER TABLE links ADD linkcodetmp clob;
update links i1 set linkcodetmp = (select link_code from links i2 where i2.inode = i1.inode);
ALTER TABLE links DROP COLUMN link_code;
ALTER TABLE links RENAME COLUMN linkcodetmp TO link_code;