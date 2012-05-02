create table chain_state_parameter (
   id int not null auto_increment,
   chain_state_id int not null,
   name varchar(255) not null,
   value varchar(255) not null,
   primary key (id)
);
create table chain_link_code (
   id int not null auto_increment,
   class_name varchar(255) unique,
   code text not null,
   last_mod_date datetime not null,
   language varchar(255) not null,
   primary key (id)
);
create table chain (
   id int not null auto_increment,
   key_name varchar(255) unique,
   name varchar(255) not null,
   success_value varchar(255) not null,
   failure_value varchar(255) not null,
   primary key (id)
);
create table chain_state (
   id int not null auto_increment,
   chain_id int not null,
   link_code_id int not null,
   state_order int not null,
   primary key (id)
);
create index idx_chain_link_code_classname on chain_link_code (class_name);
create index idx_chain_key_name on chain (key_name);

alter table chain_state add constraint fk_state_chain foreign key (chain_id) references chain(id);

alter table chain_state add constraint fk_state_code foreign key (link_code_id) references chain_link_code(id);

alter table chain_state_parameter add constraint fk_parameter_state foreign key (chain_state_id) references chain_state(id);

