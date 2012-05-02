CREATE TABLE calendar_reminder
(
   user_id character varying(100), 
   event_id bigint, 
   send_date timestamp without time zone, 
   CONSTRAINT "PK_REMINDER_CALENDAR" PRIMARY KEY (user_id, event_id, send_date) USING INDEX TABLESPACE pg_default
) WITH (OIDS=FALSE)
;