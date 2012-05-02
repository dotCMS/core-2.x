UPDATE relationship SET fixed=false WHERE fixed IS NULL;

ALTER TABLE relationship ALTER fixed SET NOT NULL;
ALTER TABLE relationship ALTER fixed SET DEFAULT false;

