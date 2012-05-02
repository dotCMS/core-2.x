-- postgres

ALTER TABLE user_proxy ADD COLUMN prefix varchar(255);
ALTER TABLE user_proxy ALTER COLUMN prefix SET STORAGE EXTENDED;

ALTER TABLE user_proxy ADD COLUMN suffix varchar(255);
ALTER TABLE user_proxy ALTER COLUMN suffix SET STORAGE EXTENDED;

ALTER TABLE user_proxy ADD COLUMN title varchar(255);
ALTER TABLE user_proxy ALTER COLUMN title SET STORAGE EXTENDED;

DROP TABLE ext_user;