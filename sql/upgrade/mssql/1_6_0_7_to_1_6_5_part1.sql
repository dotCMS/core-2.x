
ALTER TABLE structure ADD structuretype integer default 0;
exec ('update structure set structuretype = 1 where content = 1;');
exec ('update structure set structuretype = 3 where content = 0;');

set nocount on

declare @constname	sysname,
	@cmd		varchar(1024), 
    @tablename varchar(1024)
set @tablename = 'structure'

declare curs_constraints cursor for
	select 	name
	from 	sysobjects 
	where 	xtype in ('C', 'F', 'PK', 'UQ', 'D')
	and	(status & 64) = 0
	and     parent_obj = object_id(@tablename)

open curs_constraints

fetch next from curs_constraints into @constname
while (@@fetch_status = 0)
begin
	select @cmd = 'ALTER TABLE ' + @tablename + ' DROP CONSTRAINT ' + @constname
	exec(@cmd)
	fetch next from curs_constraints into @constname
end

close curs_constraints
deallocate curs_constraints

GO
SET QUOTED_IDENTIFIER  OFF    SET ANSI_NULLS  ON 
GO


ALTER TABLE structure DROP COLUMN content;

alter table structure add CONSTRAINT [DF_structure_fixed]  DEFAULT ((0)) for fixed

ALTER TABLE user_proxy ADD chapter_officer VARCHAR(255) DEFAULT NULL;
update inode set type = 'communication' where inode in (select inode from communication);
ALTER TABLE report_asset ADD web_form_report TINYINT default 0;
