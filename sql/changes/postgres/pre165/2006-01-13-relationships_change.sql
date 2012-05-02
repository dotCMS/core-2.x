'Postgres
alter table relationship rename column required to child_required;

alter table relationship add column parent_required boolean default false;

update relationship set parent_required = false; 

'Mysql
alter table relationship change required child_required char (1) not null;

alter table relationship add column parent_required boolean default false;

update relationship set parent_required = false; 

'oracle
alter table relationship rename column required  to child_required;

alter table relationship add parent_required char(1) default '0';

update relationship set parent_required = '0'; 

