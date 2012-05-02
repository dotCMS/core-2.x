--postgres

drop table user_proxy;

create table user_proxy (
   inode INT8 not null,
   user_id VARCHAR(255),
   prefix VARCHAR(255),
   suffix VARCHAR(255),
   title VARCHAR(255),
   school VARCHAR(255),
   graduation_year INT4,
   organization VARCHAR(255),
   website VARCHAR(255),
   mail_subscription BOOL,
   primary key (inode),
   unique (user_id)
);

-- mssql

drop table user_proxy;

create table user_proxy (
   inode NUMERIC(19,0) not null,
   user_id VARCHAR(255) null,
   prefix VARCHAR(255) null,
   suffix VARCHAR(255) null,
   title VARCHAR(255) null,
   school VARCHAR(255) null,
   graduation_year INT null,
   organization VARCHAR(255) null,
   website VARCHAR(255) null,
   mail_subscription TINYINT null,
   primary key (inode),
   unique (user_id)
);

-- mysql

drop table user_proxy;

create table user_proxy (
   inode BIGINT not null,
   user_id VARCHAR(255),
   prefix VARCHAR(255),
   suffix VARCHAR(255),
   title VARCHAR(255),
   school VARCHAR(255),
   graduation_year INTEGER,
   organization VARCHAR(255),
   website VARCHAR(255),
   mail_subscription BIT,
   primary key (inode),
   unique (user_id)
);

-- oracle

drop table user_proxy;

create table user_proxy (
   inode NUMBER(19,0) not null,
   user_id VARCHAR2(255),
   prefix VARCHAR2(255),
   suffix VARCHAR2(255),
   title VARCHAR2(255),
   school VARCHAR2(255),
   graduation_year NUMBER(10,0),
   organization VARCHAR2(255),
   website VARCHAR2(255),
   mail_subscription NUMBER(1,0),
   primary key (inode),
   unique (user_id)
);
