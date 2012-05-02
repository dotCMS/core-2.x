CREATE TABLE favorites
(
  name varchar(255) NOT NULL,
  description varchar(255),
  starting timestamptz,
  ending timestamptz,
  path varchar(255),
  deleted bool DEFAULT false,
  sort_order int4 DEFAULT 0,
  favorites_type int4 DEFAULT 0,
  user_id varchar(255) NOT NULL,
  group_id varchar(255) NOT NULL,
  inode int8 NOT NULL,
  CONSTRAINT pk_favorites PRIMARY KEY (inode)
) 
WITHOUT OIDS;
ALTER TABLE favorites OWNER TO root;
