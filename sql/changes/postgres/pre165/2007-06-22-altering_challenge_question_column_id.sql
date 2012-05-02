-- altering user_proxy
alter table user_proxy add column new_cquestionid integer;
update user_proxy set new_cquestionid = cast(cquestionid as integer);
alter table user_proxy drop constraint FK_USER_PROXY_CQUESTIONID;
alter table user_proxy drop column cquestionid;
alter table user_proxy rename column new_cquestionid TO cquestionid;

-- altering challenge_question
alter table challenge_question add column new_cquestionid integer;
update challenge_question set new_cquestionid = cast(cquestionid as integer);
alter table challenge_question drop column cquestionid;
alter table challenge_question rename column new_cquestionid TO cquestionid;
alter table challenge_question add primary key (cquestionid);

-- restoring constraint
alter table user_proxy add constraint FK_USER_PROXY_CQUESTIONID foreign key(cquestionid) references challenge_question(cquestionid);
