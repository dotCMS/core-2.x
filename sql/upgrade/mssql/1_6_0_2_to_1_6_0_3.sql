
UPDATE field SET fixed=0 WHERE fixed IS NULL;
UPDATE field SET read_only=0 WHERE read_only IS NULL;



ALTER TABLE field ALTER COLUMN fixed tinyint NOT NULL 
ALTER TABLE field ALTER COLUMN read_only tinyint  NOT NULL;
alter table field add CONSTRAINT [DF_field_fixed]  DEFAULT ((0)) for fixed
alter table field add CONSTRAINT [DF_field_read_only]  DEFAULT ((0)) for read_only
