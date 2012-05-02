ALTER TABLE structure ADD content TINYINT(1) default 1;
ALTER TABLE structure ADD fixed TINYINT(1)   default 0;

ALTER TABLE relationship ADD fixed TINYINT(1)  default 0;

ALTER TABLE field ADD fixed TINYINT(1) default 0;
ALTER TABLE field ADD read_only TINYINT(1)  default 0;