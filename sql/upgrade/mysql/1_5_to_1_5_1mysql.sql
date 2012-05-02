alter table content_rating add identifier int;
update content_rating set identifier = (inode - 1);
alter table content_rating drop column inode;


alter table web_form add user_inode bigint default 0;
alter table web_form add categories varchar(255);


ALTER TABLE contentlet change `number1` `float1` float;
ALTER TABLE contentlet change `number2` `float2` float;
ALTER TABLE contentlet change `number3` `float3` float;
ALTER TABLE contentlet change `number4` `float4` float;
ALTER TABLE contentlet change `number5` `float5` float;
ALTER TABLE contentlet change `number6` `float6` float;
ALTER TABLE contentlet change `number7` `float7` float;
ALTER TABLE contentlet change `number8` `float8` float;
ALTER TABLE contentlet change `number9` `float9` float;
ALTER TABLE contentlet change `number10` `float10` float;
ALTER TABLE contentlet change `number11` `float11` float;
ALTER TABLE contentlet change `number12` `float12` float;
ALTER TABLE contentlet change `number13` `float13` float;
ALTER TABLE contentlet change `number14` `float14` float;
ALTER TABLE contentlet change `number15` `float15` float;
ALTER TABLE contentlet change `number16` `float16` float;
ALTER TABLE contentlet change `number17` `float17` float;
ALTER TABLE contentlet change `number18` `float18` float;
ALTER TABLE contentlet change `number19` `float19` float;
ALTER TABLE contentlet change `number20` `float20` float;
ALTER TABLE contentlet change `number21` `float21` float;
ALTER TABLE contentlet change `number22` `float22` float;
ALTER TABLE contentlet change `number23` `float23` float;
ALTER TABLE contentlet change `number24` `float24` float;
ALTER TABLE contentlet change `number25` `float25` float;


ALTER TABLE content_rating ADD user_ip varchar(255);
ALTER TABLE content_rating ADD long_live_cookie_id varchar(255);


ALTER TABLE inode MODIFY `owner` VARCHAR(100);