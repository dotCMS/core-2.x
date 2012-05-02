-- Table: last_indexation

-- DROP TABLE last_indexation;

CREATE TABLE last_indexation
(
  inode int8 NOT NULL,
  server_id varchar(64) NOT NULL,
  time_entered timestamp NOT NULL,
  CONSTRAINT last_indexation_pkey PRIMARY KEY (inode),
  CONSTRAINT last_indexation_server_id_key UNIQUE (server_id)
) 
WITHOUT OIDS;
ALTER TABLE last_indexation OWNER TO postgres;

-- Table: indexation

-- DROP TABLE indexation;

CREATE TABLE indexation
(
  inode int8 NOT NULL,
  object_to_index int8 NOT NULL,
  server_id varchar(64) NOT NULL,
  time_entered timestamp NOT NULL,
  CONSTRAINT indexation_pkey PRIMARY KEY (inode)
) 
WITHOUT OIDS;
ALTER TABLE indexation OWNER TO postgres;