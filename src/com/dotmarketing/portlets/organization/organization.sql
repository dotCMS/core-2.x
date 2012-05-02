
drop table organization;
create table organization (
	inode bigint not null, 
	title varchar(255),
	ceo_name varchar(255),
	partner_url varchar(255),
	partner_key varchar(255),
	partner_logo bigint,
	street1 varchar(255),
	street2 varchar(255),
	city varchar(255),
	state varchar(255),
	zip varchar(100),
	phone varchar(100),
	fax varchar(100),
	country varchar(255),
	is_system boolean default false, 
	parent_organization bigint default 0,
	primary key (inode)
);

alter table organization add constraint organization_inode foreign key (inode) references inode (inode);

