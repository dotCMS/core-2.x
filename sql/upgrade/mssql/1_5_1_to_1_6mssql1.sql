ALTER TABLE structure ADD "content" TINYINT default 1;
ALTER TABLE structure ADD fixed TINYINT   default 0;

ALTER TABLE relationship ADD fixed TINYINT  default 0;
ALTER TABLE field ADD read_only TINYINT  default 0;

ALTER TABLE field ADD fixed TINYINT default 0;
ALTER TABLE field ADD searchable TINYINT null;
alter table contentlet add disabled_wysiwyg VARCHAR(255) DEFAULT NULL;


