create table trackback (
  id NUMERIC(19,0) not null,
  asset_identifier NUMERIC(19,0) not null,
  title VARCHAR(255),
  excerpt VARCHAR(255),
  url VARCHAR(255) not null,
  blog_name VARCHAR(255),
  track_date DATE not null, 
  primary key (id)
);

create sequence trackback_sequence;
create index idx_trackback_2 on trackback (url);
create index idx_trackback_1 on trackback (asset_identifier);

ALTER TABLE structure ADD page_detail NUMERIC(19,0) not null;
update structure set page_detail=0;