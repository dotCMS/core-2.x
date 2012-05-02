--POSTGRES AND MYSQL 

ALTER TABLE user_proxy ADD COLUMN how_heard varchar(255);
ALTER TABLE user_proxy ADD COLUMN long_lived_cookie varchar(255);
ALTER TABLE user_proxy ADD COLUMN company varchar(255);
ALTER TABLE user_proxy ADD COLUMN website varchar(255);



--ORACLE AND SQL SERVER

ALTER TABLE user_proxy ADD how_heard varchar(255);
ALTER TABLE user_proxy ADD long_lived_cookie varchar(255);
ALTER TABLE user_proxy ADD company varchar(255);
ALTER TABLE user_proxy ADD website varchar(255);