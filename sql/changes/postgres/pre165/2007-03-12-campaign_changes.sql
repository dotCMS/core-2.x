--begin campaign table changes
alter table campaign add userFilterInode bigint;
update campaign set userFilterInode = 0;
alter table campaign add sendTo varchar(15);
update campaign set sendTo = 'mailingList';
alter table campaign add isRecurrent boolean default('f'); 
update campaign set isRecurrent = 'f';
alter table campaign add wasSent boolean default('f'); 
update campaign set wasSent = 't';
--end campaign table changes

--begin user_filter table changes
alter table user_filter add birthdaytypesearch varchar(10);
update user_filter set birthdaytypesearch = 'Since';
alter table user_filter add birthday timestamp;
alter table user_filter add lastLoginTypeSearch varchar(10);
update user_filter set lastLoginTypeSearch = 'DateRange';
alter table user_filter add lastLoginSince varchar(10);
alter table user_filter add createdTypeSearch varchar(10);
update user_filter set createdTypeSearch = 'DateRange';
alter table user_filter add createdSince varchar(10);
alter table user_filter add createdFrom timestamp;
alter table user_filter add createdTo timestamp;
alter table user_filter add lastVisitTypeSearch varchar(10);
update user_filter set lastVisitTypeSearch = 'DateRange';
alter table user_filter add lastVisitSince varchar(10);
alter table user_filter add lastVisitFrom timestamp;
alter table user_filter add lastVisitTo timestamp;
--end user_filter table changes
