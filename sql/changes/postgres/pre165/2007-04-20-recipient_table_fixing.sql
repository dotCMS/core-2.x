update communication set communication_type = 'external' where communication_type = 'offline';

alter table recipient alter column user_id type varchar(100);

CREATE INDEX idx_communication_user_id ON recipient USING btree (user_id);