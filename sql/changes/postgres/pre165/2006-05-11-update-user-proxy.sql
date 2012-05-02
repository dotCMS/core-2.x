--POSTGRES AND MYSQL 

ALTER TABLE user_proxy ADD COLUMN school varchar(255);
ALTER TABLE user_proxy ADD COLUMN graduation_year integer;

--ORACLE AND SQL SERVER

ALTER TABLE user_proxy ADD school varchar(255);
ALTER TABLE user_proxy ADD graduation_year integer;