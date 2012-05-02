

UPDATE field SET fixed=0 WHERE fixed IS NULL;
UPDATE field SET read_only=0 WHERE read_only IS NULL;


ALTER TABLE field MODIFY fixed varchar(1) DEFAULT '0' NOT NULL;
ALTER TABLE field MODIFY read_only varchar(1) DEFAULT '1' NOT NULL;