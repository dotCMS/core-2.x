alter table content_rating add "identifier" numeric(19,0);

update content_rating set identifier = (inode - 1);

alter table content_rating drop column inode;