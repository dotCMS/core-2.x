
UPDATE relationship SET fixed=0 WHERE fixed IS NULL;

ALTER TABLE relationship ALTER COLUMN fixed tinyint NOT NULL 
alter table relationship add CONSTRAINT [DF_relationship _fixed]  DEFAULT ((0)) for fixed

