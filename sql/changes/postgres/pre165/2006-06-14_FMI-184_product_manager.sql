CREATE TABLE ecom_product (
	inode bigint NOT NULL PRIMARY KEY REFERENCES inode(inode),
	title varchar (255) NOT NULL ,
	short_description text NULL ,
	long_description text NULL ,
	req_shipping bool NULL ,
	featured bool NULL ,
	sort_order int NULL ,
	comments text NULL ,
	showOnWeb bool NULL 
);


CREATE TABLE ecom_discount_code (
	inode bigint NOT NULL PRIMARY KEY REFERENCES inode(inode) ,
	discount_type int NOT NULL ,
	start_date timestamp NULL ,
	end_date timestamp NULL ,
	code_id varchar (50) NULL CONSTRAINT uk_discount_code_id UNIQUE ,
	code_description varchar (100) NULL ,
	free_shipping bool NULL ,
	no_bulk_disc bool NULL ,
	discount_amount decimal(18, 3) NOT NULL ,
	min_order int NULL 
);

CREATE TABLE ecom_order (
	inode bigint NOT NULL PRIMARY KEY REFERENCES inode(inode) ,
	user_inode bigint NULL ,
	order_status int NULL ,
	payment_status int NULL ,
	date_posted timestamp NULL ,
	last_mod_date timestamp NULL ,
	billing_address1 varchar (255) NULL ,
	billing_address2 varchar (255) NULL ,
	billing_city varchar (100) NULL ,
	billing_state varchar (50) NULL ,
	billing_zip varchar (50) NULL ,
	billing_country varchar (50) NULL ,
	billing_phone varchar (50) NULL ,
	billing_fax varchar (50) NULL ,
	billing_contact_name varchar (100) NULL ,
	billing_contact_phone varchar (50) NULL ,
	billing_contact_email varchar (100) NULL ,
	shipping_address1 varchar (255) NULL ,
	shipping_address2 varchar (255) NULL ,
	shipping_city varchar (50) NULL ,
	shipping_state varchar (50) NULL ,
	shipping_zip varchar (50) NULL ,
	shipping_country varchar (50) NULL ,
	shipping_phone varchar (50) NULL ,
	shipping_fax varchar (50) NULL ,
	payment_type char (10) NULL ,
	name_on_card varchar (100) NULL ,
	card_type varchar (50) NULL ,
	card_number varchar (50) NULL ,
	card_exp_month int NULL ,
	card_exp_year int NULL ,
	card_verification_value varchar (50) NULL ,
	order_sub_total decimal(18, 2) NULL ,
	order_shipping decimal(18, 2) NULL ,
	order_ship_type int NULL ,
	order_tax decimal(18, 2) NULL ,
	tax_exempt_number varchar (50) NULL ,
	discount_codes varchar (50) NULL ,
	order_total decimal(18, 2) NULL ,
	order_total_paid decimal(18, 2) NULL ,
	order_total_due decimal(18, 2) NULL ,
	invoice_number varchar (50) NULL ,
	invoice_date timestamp NULL ,
	check_number varchar (50) NULL ,
	check_bank_name varchar (100) NULL ,
	po_number varchar (50) NULL ,
	order_discount decimal(18, 2) NULL ,
	tracking_number varchar (256) NULL ,
	modified_QB bool NULL ,
	modified_FH bool NULL ,
	backend_user varchar (100) NULL ,
	shipping_label varchar (50) NULL 
) ;


CREATE TABLE ecom_product_format (
	inode bigint NOT NULL PRIMARY KEY REFERENCES inode(inode) ,
	product_inode bigint NOT NULL REFERENCES ecom_product(inode) ,
	format_name varchar (255) NOT NULL ,
	item_num varchar (50) NULL ,
	format varchar (100) NOT NULL ,
	inventory_quantity int NULL ,
	reorder_trigger int NULL ,
	weight decimal(18, 3) NULL ,
	width int NULL ,
	height int NULL ,
	depth int NULL 
);


CREATE TABLE ecom_order_item (
	inode bigint NOT NULL PRIMARY KEY REFERENCES inode(inode) ,
	order_inode bigint NULL REFERENCES ecom_order(inode) ,
	product_inode bigint NULL REFERENCES ecom_product_format(inode) ,
	item_qty int NULL ,
	item_price decimal(18, 2) NULL 
);


CREATE TABLE ecom_product_price (
	inode bigint NOT NULL PRIMARY KEY ,
	product_format_inode bigint NOT NULL ,
	min_qty int NULL ,
	max_qty int NULL ,
	retail_price decimal(18, 2) NOT NULL ,
	partner_price decimal(18, 2) NOT NULL 
);


CREATE TABLE web_event (
	inode bigint NOT NULL PRIMARY KEY ,
	title varchar (255) NOT NULL ,
	subtitle varchar (255) NULL ,
	summary varchar (1000) NULL ,
	description text NULL ,
	terms_conditions text NULL ,
	comments text NULL ,
	partners_only bool NULL ,
	show_on_web bool NULL ,
	sort_order int NULL ,
	event_image_1 bigint NULL ,
	event_image_2 bigint NULL ,
	event_image_3 bigint NULL ,
	event_image_4 bigint NULL ,
	is_institute bool NULL 
);


CREATE TABLE web_event_location (
	inode bigint NOT NULL PRIMARY KEY ,
	web_event_inode bigint NOT NULL REFERENCES web_event(inode) ,
	city varchar (255) NULL ,
	state varchar (50) NULL ,
	start_date timestamp NULL ,
	end_date timestamp NULL ,
	show_on_web bool NULL ,
	web_reg_active bool NULL ,
	hotel_name varchar (255) NULL ,
	hotel_link bigint NULL ,
	past_event_link bigint NULL ,
	partner_price decimal(18, 2) NULL ,
	non_partner_price decimal(18, 2) NULL ,
	short_description text NULL ,
	text_email varchar (1000) NULL ,
	almost_at_capacity bool NULL ,
	full_capacity bool NULL ,
	default_contract_partner_price bool NULL 
);


CREATE TABLE web_event_registration (
	inode bigint NOT NULL PRIMARY KEY REFERENCES inode(inode) ,
	event_inode bigint NOT NULL ,
	event_location_inode bigint NOT NULL ,
	user_inode bigint NOT NULL ,
	registration_status int NULL ,
	date_posted timestamp NULL ,
	last_mod_date timestamp NULL ,
	total_paid decimal(18, 2) NULL ,
	total_due decimal(18, 2) NULL ,
	total_registration decimal(18, 2) NULL ,
	payment_type int NULL ,
	billing_address_1 varchar (255) NULL ,
	billing_address_2 varchar (255) NULL ,
	billing_city varchar (255) NULL ,
	billing_state varchar (50) NULL ,
	billing_zip varchar (50) NULL ,
	billing_contact_name varchar (255) NULL ,
	billing_contact_phone varchar (50) NULL ,
	billing_contact_email varchar (255) NULL ,
	card_name varchar (255) NULL ,
	card_type varchar (50) NULL ,
	card_number varchar (50) NULL ,
	card_exp_month varchar (50) NULL ,
	card_exp_year varchar (50) NULL ,
	card_verification_value varchar (4) NULL ,
	check_number varchar (50) NULL ,
	check_bank_name varchar (255) NULL ,
	po_number varchar (50) NULL ,
	invoice_number varchar (50) NULL ,
	badge_printed bool NULL ,
	how_did_you_hear varchar (255) NULL ,
	ceo_name varchar (255) NULL ,
	modified_QB bool NULL ,
	reminder_email_sent bool NULL ,
	post_email_sent bool NULL ,
	billing_country varchar (255) NULL 
);


CREATE TABLE web_event_attendee (
	inode bigint NOT NULL PRIMARY KEY ,
	event_registration_inode bigint NULL REFERENCES web_event_registration(inode) ,
	first_name varchar (255) NULL ,
	last_name varchar (255) NULL ,
	badge_name varchar (255) NULL ,
	email varchar (255) NULL ,
	title varchar (255) NULL ,
	registration_price decimal(18, 2) NULL 
);
