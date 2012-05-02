--begin campaign table changes
alter table campaign add column parent_campaign bigint;

update campaign set parent_campaign = 0;
--end campaign table changes
