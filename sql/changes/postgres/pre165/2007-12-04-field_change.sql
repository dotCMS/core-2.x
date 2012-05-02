ALTER TABLE field ADD COLUMN searchable boolean;
update field set searchable = indexed;