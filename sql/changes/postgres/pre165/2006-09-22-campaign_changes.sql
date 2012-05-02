--begin communication table changes
alter table communication add ext_comm_identifier CHARACTER VARYING(255);
update communication set ext_comm_identifier = external_communication_identifier;
alter table communication drop column external_communication_identifier;
--end communication table changes

--begin campaign table changes
alter table campaign add sendEmail char(1) default '0';
update campaign set sendEmail = '0';
alter table campaign add communicationInode bigint;
update campaign set communicationInode = 0;
--end campaign table changes

--begin user_proxy table changes
alter table user_proxy add var1 varchar(255);
alter table user_proxy add var2 varchar(255);
alter table user_proxy add var3 varchar(255);
alter table user_proxy add var4 varchar(255);
alter table user_proxy add var5 varchar(255);
alter table user_proxy add var6 varchar(255);
alter table user_proxy add var7 varchar(255);
alter table user_proxy add var8 varchar(255);
alter table user_proxy add var9 varchar(255);
alter table user_proxy add var10 varchar(255);
alter table user_proxy add var11 varchar(255);
alter table user_proxy add var12 varchar(255);
alter table user_proxy add var13 varchar(255);
alter table user_proxy add var14 varchar(255);
alter table user_proxy add var15 varchar(255);
alter table user_proxy add var16 varchar(255);
alter table user_proxy add var17 varchar(255);
alter table user_proxy add var18 varchar(255);
alter table user_proxy add var19 varchar(255);
alter table user_proxy add var20 varchar(255);
alter table user_proxy add var21 varchar(255);
alter table user_proxy add var22 varchar(255);
alter table user_proxy add var23 varchar(255);
alter table user_proxy add var24 varchar(255);
alter table user_proxy add var25 varchar(255);
alter table user_proxy add last_result integer DEFAULT 0;
alter table user_proxy add last_message varchar(255);
update user_proxy set last_result = 0;
--end user_proxy table changes

--begin user_comments table changes
alter table user_comments add communication_id bigint;
--end user_comments table changes

--begin removing Mailing List Builder Portlet
update layout
set narrow1 = replace(narrow1, 'EXT_MAILINGLISTBUILDER,', '')
where narrow1 like '%EXT_MAILINGLISTBUILDER,%';

update layout
set narrow1 = replace(narrow1, ',EXT_MAILINGLISTBUILDER', '')
where narrow1 like '%,EXT_MAILINGLISTBUILDER%';

update layout
set narrow1 = replace(narrow1, 'EXT_MAILINGLISTBUILDER', '')
where narrow1 like '%EXT_MAILINGLISTBUILDER%';

update layout
set narrow2 = replace(narrow2, 'EXT_MAILINGLISTBUILDER,', '')
where narrow2 like '%EXT_MAILINGLISTBUILDER,%';

update layout
set narrow2 = replace(narrow2, ',EXT_MAILINGLISTBUILDER', '')
where narrow2 like '%,EXT_MAILINGLISTBUILDER%';

update layout
set narrow2 = replace(narrow2, 'EXT_MAILINGLISTBUILDER', '')
where narrow2 like '%EXT_MAILINGLISTBUILDER%';

update layout
set wide = replace(wide, 'EXT_MAILINGLISTBUILDER,', '')
where wide like '%EXT_MAILINGLISTBUILDER,%';

update layout
set wide = replace(wide, ',EXT_MAILINGLISTBUILDER', '')
where wide like '%,EXT_MAILINGLISTBUILDER%';

update layout
set wide = replace(wide, 'EXT_MAILINGLISTBUILDER', '')
where wide like '%EXT_MAILINGLISTBUILDER%';
--end removing Mailing List Builder Portlet