alter table web_event alter column description type varchar(1000);
alter table web_event alter column terms_conditions type varchar(1000);
alter table web_event alter column comments type varchar(1000);
alter table ecom_order drop column order_id;
