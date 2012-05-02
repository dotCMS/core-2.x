ALTER TABLE `field`  ADD COLUMN searchable TINYINT(1);
update `field` f set f.searchable = indexed;