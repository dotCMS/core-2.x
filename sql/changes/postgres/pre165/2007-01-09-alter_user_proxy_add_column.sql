-- if is SQL Server

alter table user_proxy add no_click_tracking bit default(0); 

update user_proxy set no_click_tracking = 0;

--if is in postgress

alter table user_proxy add no_click_tracking boolean default('f'); 

update user_proxy set no_click_tracking = 'f';