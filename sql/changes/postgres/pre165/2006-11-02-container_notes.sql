--POSTGRES

ALTER TABLE containers ADD COLUMN notes text;
ALTER TABLE containers ALTER COLUMN notes SET STORAGE EXTENDED;
UPDATE containers SET notes = '' WHERE notes IS null;

-- Oracle
-- ALTER TABLE containers ADD notes clob;
-- UPDATE containers SET notes = '' WHERE notes IS null;