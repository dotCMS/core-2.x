drop table indexation;

CREATE TABLE indexation
(
  indexation_id int8 NOT NULL,
  object_to_index int8 NOT NULL,
  time_entered timestamp NOT NULL,
  server_id varchar(25),
  CONSTRAINT indexation_pkey PRIMARY KEY (indexation_id)
) 
WITHOUT OIDS;

CREATE INDEX idx_indexation_1
  ON indexation
  USING btree
  (server_id);

create sequence indexation_seq start with 1;

DROP TABLE last_indexation;

---- MSSQL

drop table indexation;

CREATE TABLE indexation
(
  indexation_id bigint identity(1,1),
  object_to_index bigint NOT NULL,
  time_entered datetime NOT NULL,
  server_id varchar(25),
  CONSTRAINT indexation_pkey PRIMARY KEY (indexation_id)
);

CREATE INDEX idx_indexation_1
  ON indexation
  (server_id);

DROP TABLE last_indexation;

-- MySQL

drop table indexation;

CREATE TABLE indexation
(
  indexation_id bigint identity(1,1),
  object_to_index bigint NOT NULL,
  time_entered datetime NOT NULL,
  server_id varchar(25),
  CONSTRAINT indexation_pkey PRIMARY KEY (indexation_id)
);

CREATE INDEX idx_indexation_1
  ON indexation
  (server_id);

DROP TABLE last_indexation;

-- Oracle

drop table indexation;

CREATE TABLE indexation
(
  indexation_id number(8) NOT NULL,
  object_to_index number(8) NOT NULL,
  time_entered timestamp NOT NULL,
  server_id varchar(25),
  CONSTRAINT indexation_pkey PRIMARY KEY (indexation_id)
);

CREATE INDEX idx_indexation_1
  ON indexation (server_id);

create sequence indexation_seq start with 1;

DROP TABLE last_indexation;

