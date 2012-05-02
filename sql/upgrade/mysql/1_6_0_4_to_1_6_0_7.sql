ALTER TABLE field ADD unique_ TINYINT(1) default 0;
update field set unique_ = 0 where unique_ is null; 