create table calendar_reminder
(
   user_id VARCHAR2(100) not null,
   event_id NUMBER(20) not null,
   send_date DATE not null
);

alter table calendar_reminder add constraint calendar_reminder_pk primary key(user_id,event_id,send_date);