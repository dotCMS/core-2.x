create table report_parameter (
   inode INT8 not null,
   report_inode INT8 not null,
   parameter_description VARCHAR(1000),
   parameter_name VARCHAR(100),
   class_type VARCHAR(250),
   default_value VARCHAR(10000),
   primary key (inode)
);

alter table report_parameter add constraint FK22DA125E5FB51EB foreign key (inode) references inode;