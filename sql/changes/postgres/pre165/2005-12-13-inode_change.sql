ALTER TABLE inode ADD COLUMN identifier bigint;
ALTER TABLE inode alter column identifier SET DEFAULT 0;
update inode set identifier =0;



-- Oracle
-- ALTER TABLE inode ADD identifier number(20,0) default 0;
-- update inode set identifier =0