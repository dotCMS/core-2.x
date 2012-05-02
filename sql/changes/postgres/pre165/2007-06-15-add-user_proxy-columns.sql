--POSTGRES AND MYSQL 

ALTER TABLE user_proxy ADD COLUMN cquestionid varchar(255);
ALTER TABLE user_proxy ADD COLUMN cqanswer varchar(255);


--ORACLE AND SQL SERVER

ALTER TABLE user_proxy ADD cquestionid varchar(255);
ALTER TABLE user_proxy ADD cqanswer varchar(255);