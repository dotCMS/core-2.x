create table trackback (
  id INT8 not null,
  asset_identifier INT8 not null,
  title VARCHAR(255),
  excerpt VARCHAR(255),
  url VARCHAR(255) not null,
  blog_name VARCHAR(255),
  track_date TIMESTAMP not null, 
  primary key (id)
);

create sequence trackback_sequence;
create index idx_trackback_2 on trackback (url);
create index idx_trackback_1 on trackback (asset_identifier);

alter table user_proxy add constraint FK_USER_PROXY_CQUESTIONID foreign key(cquestionid) references challenge_question(cquestionid);

ALTER TABLE structure ADD page_detail INT8 not null;
update structure set page_detail=0;