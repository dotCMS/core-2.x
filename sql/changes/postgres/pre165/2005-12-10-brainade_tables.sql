create table brainade_rating(
	id bigint, 
	user_id varchar(255) NOT NULL,
  	rating_date timestamp,
	inode bigint not null,
	rating int not null,
	CONSTRAINT pk_brainade_rating PRIMARY KEY (id)
);



create sequence brainade_rating_sequence;
create index brainade_rating_idx2 on brainade_rating(inode);
create index brainade_rating_idx on brainade_rating(user_id);


create table brainade_clicks(
	id bigint, 
	inode bigint not null,
	user_id varchar(255) NOT NULL,
  	click_date timestamp,
	CONSTRAINT pk_brainade_clicks PRIMARY KEY (id)
);

create sequence brainade_clicks_sequence;
create index brainade_clicks_idx on brainade_rating(user_id);
create index brainade_clicks_idx2 on brainade_rating(inode);