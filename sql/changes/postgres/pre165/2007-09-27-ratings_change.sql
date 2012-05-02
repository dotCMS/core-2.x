alter table content_rating add column identifier bigint;

update content_rating set identifier = (inode - 1);

alter table content_rating drop column inode;