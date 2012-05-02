-- begin postgres

drop table user_filter;

create table user_filter (
  inode int8 not null, 
  title varchar(255), 
  firstname varchar(100),
  middlename varchar(100),
  lastname varchar(100),
  emailaddress varchar(100),
  birthdayTo timestamp,
  birthdayFrom timestamp,
  loginTo timestamp,
  loginFrom timestamp,
  city varchar(100),
  state varchar(100),
  country varchar(100),
  zip varchar(100),
  active_ boolean,
  tagname varchar(255),
  cell varchar(100),
  phone varchar(100),
  fax varchar(100),

  var1 varchar(255),
  var2 varchar(255),
  var3 varchar(255),
  var4 varchar(255),
  var5 varchar(255),
  var6 varchar(255),
  var7 varchar(255),
  var8 varchar(255),
  var9 varchar(255),
  var10 varchar(255),
  var11 varchar(255),
  var12 varchar(255),
  var13 varchar(255),
  var14 varchar(255),
  var15 varchar(255),
  var16 varchar(255),
  var17 varchar(255),
  var18 varchar(255),
  var19 varchar(255),
  var20 varchar(255),
  var21 varchar(255),
  var22 varchar(255),
  var23 varchar(255),
  var24 varchar(255),
  var25 varchar(255),

  categories varchar(255),

  primary key (inode)
)
-- end postgres