alter table links add link_code text;
alter table links add link_type varchar(20);
update links set link_type = 'INTERNAL' where internal = true;
update links set link_type = 'EXTERNAL' where internal = false;
alter table links drop internal;
