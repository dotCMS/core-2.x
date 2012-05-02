

UPDATE relationship SET fixed=0 WHERE fixed IS NULL;
ALTER TABLE relationship MODIFY fixed varchar(1) DEFAULT '0' NOT NULL;
