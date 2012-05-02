create table event_recurrence (
  inode numeric(19,0) NOT NULL,
  occurs varchar(10),
  interval int,
  rec_starting datetime,
  rec_ending datetime,
  days_of_week varchar(20),
  day_of_month int
);

alter table event_recurrence 
add foreign key (inode) references inode(inode);