ALTER TABLE structure ADD COLUMN content  NUMBER(1,0)default 1;
ALTER TABLE structure ADD COLUMN fixed  NUMBER(1,0)  default 0;

ALTER TABLE relationship ADD COLUMN fixed  NUMBER(1,0) default 0;

ALTER TABLE field ADD COLUMN fixed  NUMBER(1,0) default 0;
ALTER TABLE field ADD COLUMN read_only  NUMBER(1,0)  default 0;