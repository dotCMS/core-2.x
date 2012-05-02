UPDATE field SET fixed=false WHERE fixed IS NULL;
UPDATE field SET read_only=false WHERE read_only IS NULL;

ALTER TABLE field ALTER fixed SET NOT NULL;
ALTER TABLE field ALTER fixed SET DEFAULT false;
ALTER TABLE field ALTER read_only SET NOT NULL;
ALTER TABLE field ALTER read_only SET DEFAULT false;
