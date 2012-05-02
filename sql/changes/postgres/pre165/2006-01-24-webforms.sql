-- postgres

create table web_form (
	web_form_id int not null primary key,
	submit_date timestamp,
	form_type varchar(255),
	prefix varchar(255),
	first_name varchar(255),
	middle_initial varchar(255),
	middle_name varchar(255),
	last_name varchar(255),
	full_name varchar(255),
	organization varchar(255),
	title varchar(255),
	address varchar(255),
	address1 varchar(255),
	address2 varchar(255),
	city varchar(255),
	state varchar(255),
	zip varchar(255),
	country varchar(255),
	phone varchar(255),
	email varchar(255),
	custom_fields text

);

CREATE SEQUENCE web_form_seq INCREMENT BY 1 NO MAXVALUE MINVALUE 1 CACHE 1;


-- My SQL


create table web_form (
  web_form_id int not null primary key AUTO_INCREMENT,
  submit_date DATETIME,
  form_type varchar(255),
  prefix varchar(255),
  first_name varchar(255),
  middle_initial varchar(255),
  middle_name varchar(255),
  last_name varchar(255),
  full_name varchar(255),
  organization varchar(255),
  title varchar(255),
  address varchar(255),
  address1 varchar(255),
  address2 varchar(255),
  city varchar(255),
  state varchar(255),
  zip varchar(255),
  country varchar(255),
  phone varchar(255),
  email varchar(255),
  custom_fields text

);

-- Oracle

create table web_form (
	web_form_id int not null primary key,
	submit_date date,
	form_type varchar(255),
	prefix varchar(255),
	first_name varchar(255),
	middle_initial varchar(255),
	middle_name varchar(255),
	last_name varchar(255),
	full_name varchar(255),
	organization varchar(255),
	title varchar(255),
	address varchar(255),
	address1 varchar(255),
	address2 varchar(255),
	city varchar(255),
	state varchar(255),
	zip varchar(255),
	country varchar(255),
	phone varchar(255),
	email varchar(255),
	custom_fields clob

);

CREATE SEQUENCE web_form_seq INCREMENT BY 1;

-- MSSql

create table web_form (
  web_form_id int identity primary key ,
  submit_date DATETIME,
  form_type varchar(255),
  prefix varchar(255),
  first_name varchar(255),
  middle_initial varchar(255),
  middle_name varchar(255),
  last_name varchar(255),
  full_name varchar(255),
  organization varchar(255),
  title varchar(255),
  address varchar(255),
  address1 varchar(255),
  address2 varchar(255),
  city varchar(255),
  state varchar(255),
  zip varchar(255),
  country varchar(255),
  phone varchar(255),
  email varchar(255),
  custom_fields text

);