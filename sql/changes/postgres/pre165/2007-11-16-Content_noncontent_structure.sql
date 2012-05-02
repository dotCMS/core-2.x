ALTER TABLE structure ADD COLUMN content boolean default TRUE;
ALTER TABLE structure ADD COLUMN fixed boolean   default FALSE;

ALTER TABLE relationship ADD COLUMN fixed boolean  default FALSE;

ALTER TABLE field ADD COLUMN fixed boolean default FALSE;
ALTER TABLE field ADD COLUMN read_only boolean  default FALSE;