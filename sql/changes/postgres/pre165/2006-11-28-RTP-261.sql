-- postgres

CREATE TABLE content_rating
(
  id bigint NOT NULL,
  user_id character varying(255),
  session_id character varying(255) NOT NULL,
  rating_date timestamp without time zone,
  inode bigint NOT NULL,
  rating integer NOT NULL,
  CONSTRAINT pk_content_rating PRIMARY KEY(id)
) 
WITH OIDS;

CREATE SEQUENCE content_rating_sequence
  INCREMENT 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 156
  CACHE 1;