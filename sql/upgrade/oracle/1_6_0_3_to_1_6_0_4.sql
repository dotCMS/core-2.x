

UPDATE relationship SET fixed =0 WHERE fixed IS NULL;


ALTER TABLE relationship MODIFY fixed NOT NULL;
ALTER TABLE relationship MODIFY fixed DEFAULT 0;
