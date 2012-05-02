ALTER TABLE structure ADD structuretype integer default 0;
exec ('update structure set structuretype = 1 where content = 1;');
exec ('update structure set structuretype = 3 where content = 0;');
ALTER TABLE structure DROP CONSTRAINT DF_structure_content;
ALTER TABLE structure DROP COLUMN content;