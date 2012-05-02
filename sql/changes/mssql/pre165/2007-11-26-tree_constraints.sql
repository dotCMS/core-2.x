alter table tree add constraint FK36739EC4AB08AA foreign key (parent) references inode;
alter table tree add constraint FK36739E5A3F51C foreign key (child) references inode;