UPDATE structure SET content=true WHERE content IS NULL;
UPDATE structure SET fixed=true WHERE LOWER(Name)='event' OR LOWER(Name)='building' OR LOWER(Name)='facility';
UPDATE structure SET fixed=false WHERE LOWER(Name)<>'event' AND LOWER(Name)<>'building' AND LOWER(Name)<>'facility';

ALTER TABLE structure ALTER fixed SET NOT NULL;
ALTER TABLE structure ALTER fixed SET DEFAULT false;
ALTER TABLE structure ALTER content SET NOT NULL;
ALTER TABLE structure ALTER content SET DEFAULT true;
