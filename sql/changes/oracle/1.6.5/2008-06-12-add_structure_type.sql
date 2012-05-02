ALTER TABLE structure ADD structuretype int;
update structure set structuretype = 1 where content = 1;
update structure set structuretype = 3 where content = 0;
ALTER TABLE structure DROP COLUMN content;