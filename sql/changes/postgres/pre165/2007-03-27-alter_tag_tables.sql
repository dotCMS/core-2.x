-- renaming tag_users table to tag_inode
ALTER TABLE tag_users RENAME TO tag_inode;

-- renaming column user_id to inode
ALTER TABLE tag_inode RENAME COLUMN user_id TO inode;

-- using as primary key the tag name
ALTER TABLE tag add primary key (tagname);
