
UPDATE structure SET content=1 WHERE content IS NULL;
UPDATE structure SET fixed=1 WHERE LOWER(Name)='event' OR LOWER(Name)='building' OR LOWER(Name)='facility';
UPDATE structure SET fixed=0 WHERE LOWER(Name)<>'event' AND LOWER(Name)<>'building' AND LOWER(Name)<>'facility';


ALTER TABLE structure ALTER COLUMN fixed tinyint NOT NULL 
ALTER TABLE structure ALTER COLUMN content tinyint  NOT NULL;
alter table structure add CONSTRAINT [DF_structure_fixed]  DEFAULT ((0)) for fixed
alter table structure add CONSTRAINT [DF_structure_content]  DEFAULT ((1)) for content
