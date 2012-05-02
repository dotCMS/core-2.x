ALTER TABLE field ADD COLUMN unique_ NUMBER(1,0) default 0;
update field set unique_ = 0 where unique_ is null;