create table event_recurrence (
  inode numeric(19,0) NOT NULL,
  occurs varchar(10),
  rec_interval numeric(19,0),
  rec_starting date,
  ending date,
  days_of_week varchar(20),
  day_of_month numeric(19,0)
);

alter table event_recurrence
add foreign key (inode) references inode(inode);