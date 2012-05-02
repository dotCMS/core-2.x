CREATE TABLE users_to_delete (
	id NUMBER(19,0) not null,
	user_id varchar2(100) not null,
	primary key (id)
);

CREATE SEQUENCE user_to_delete_seq;