create table communication( 
   inode INT8 not null, 
   title varchar(255), 
   trackback_link_inode INT8, 
   communication_type varchar(255),
   from_name varchar(255), 
   from_email varchar(255), 
   email_subject varchar(255), 
   html_page_inode INT8, 
   text_message text, 
   mod_date TIMESTAMP, 
   modified_by varchar(255),
   external_communication_identifier varchar(255),
   primary key (inode) 

) 