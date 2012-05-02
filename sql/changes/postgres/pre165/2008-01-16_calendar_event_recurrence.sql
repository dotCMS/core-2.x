create table event_recurrence (
  inode bigint NOT NULL,
  occurs varchar(10),
  rec_interval int,
  rec_starting date,
  ending date,
  days_of_week varchar(20),
  day_of_month int
);

alter table event_recurrence 
add foreign key (inode) references inode(inode);
