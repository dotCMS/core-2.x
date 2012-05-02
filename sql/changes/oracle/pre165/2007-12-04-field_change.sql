ALTER TABLE field ADD COLUMN searchable NUMBER(1,0);
update field set searchable = indexed;
