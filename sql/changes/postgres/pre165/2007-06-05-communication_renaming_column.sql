--begin communication table changes
alter table communication add ext_comm_id CHARACTER VARYING(255);
update communication set ext_comm_id = ext_comm_identifier;
alter table communication drop column ext_comm_identifier;
--end communication table changes
