ALTER TABLE field ADD COLUMN listed bit(1);
update field set listed = '0';

#postgres

ALTER TABLE field ADD COLUMN listed boolean;
update field set listed = false;
