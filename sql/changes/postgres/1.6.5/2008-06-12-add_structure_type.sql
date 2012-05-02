ALTER TABLE structure ADD COLUMN structuretype integer default 0;
update structure set structuretype = 1 where content = true;
update structure set structuretype = 3 where content = false;
ALTER TABLE structure DROP COLUMN content;