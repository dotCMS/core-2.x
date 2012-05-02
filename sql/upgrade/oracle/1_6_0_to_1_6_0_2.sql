

UPDATE structure SET content=1 WHERE content IS NULL;
UPDATE structure SET fixed=1 WHERE LOWER(Name)='event' OR LOWER(Name)='building' OR LOWER(Name)='facility';
UPDATE structure SET fixed=0 WHERE LOWER(Name)<>'event' AND LOWER(Name)<>'building' AND LOWER(Name)<>'facility';


ALTER TABLE structure MODIFY fixed NOT NULL;
ALTER TABLE structure MODIFY fixed DEFAULT 0;
ALTER TABLE structure MODIFY content NOT NULL;
ALTER TABLE structure MODIFY content DEFAULT 1;
