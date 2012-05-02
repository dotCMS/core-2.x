

UPDATE field SET fixed =0 WHERE fixed IS NULL;
UPDATE field SET read_only =0 WHERE fixed IS NULL;


ALTER TABLE field MODIFY fixed NOT NULL;
ALTER TABLE field MODIFY fixed DEFAULT 0;
ALTER TABLE field MODIFY read_only NOT NULL;
ALTER TABLE field MODIFY read_only DEFAULT 0;
