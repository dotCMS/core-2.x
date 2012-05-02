

UPDATE structure SET content=1 WHERE content IS NULL;
UPDATE structure SET fixed=1 WHERE LOWER(Name)='event' OR LOWER(Name)='building' OR LOWER(Name)='facility';
UPDATE structure SET fixed=0 WHERE LOWER(Name)<>'event' AND LOWER(Name)<>'building' AND LOWER(Name)<>'facility';


ALTER TABLE structure MODIFY fixed varchar(1) DEFAULT '0' NOT NULL;
ALTER TABLE structure MODIFY content varchar(1) DEFAULT '1' NOT NULL;