#Postgres

CREATE TABLE user_comments
(
  inode int8 NOT NULL,
  cdate timestamp,
  comment_user_id varchar(100),
  type varchar(255),
  method varchar(255),
  subject varchar(255),
  ucomment text,
  user_id varchar(100),
  CONSTRAINT user_comments_pkey PRIMARY KEY (inode)
) 
WITHOUT OIDS;
ALTER TABLE user_comments OWNER TO root;


#Oracle

CREATE TABLE user_comments
(
  inode number(10) NOT NULL, 
  comment_user_id varchar2(100), 
  cdate date, 
  type varchar2(255), 
  method varchar2(255), 
  subject varchar2(255), 
  ucomment clob, 
  user_id varchar2(100), 
  CONSTRAINT pkey PRIMARY KEY (inode)
);

#mysql
CREATE TABLE user_comments
(
  inode int NOT NULL, 
  comment_user_id varchar(100), 
  cdate datetime,
  type varchar(255),
  method varchar(255), 
  subject varchar(255), 
  ucomment text, 
  user_id varchar(100),
  CONSTRAINT pkey PRIMARY KEY (inode)
);

#mssql
CREATE TABLE user_comments
(
  inode int NOT NULL, 
  comment_user_id varchar(100), 
  cdate datetime, 
  type varchar(255), 
  method varchar(255), 
  subject varchar(255), 
  ucomment text, 
  user_id varchar(100), 
  CONSTRAINT pkey PRIMARY KEY (inode)
);