ALTER TABLE field ADD searchable TINYINT null;
update field set searchable = indexed;