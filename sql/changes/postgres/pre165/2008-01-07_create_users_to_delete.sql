CREATE TABLE users_to_delete (
	id bigint NOT NULL,
	user_id character varying(100) NOT NULL,
	primary key (id)
);

CREATE SEQUENCE user_to_delete_seq;