create table trackback (
  id BIGINT NOT NULL AUTO_INCREMENT,
  asset_identifier BIGINT not null,
  title VARCHAR(255),
  excerpt VARCHAR(255),
  url VARCHAR(255) not null,
  blog_name VARCHAR(255),
  track_date DATETIME not null, 
  primary key (id)
);

create index idx_trackback_2 on trackback (url);
create index idx_trackback_1 on trackback (asset_identifier);

ALTER TABLE structure ADD page_detail BIGINT not null;
update structure set page_detail=0;