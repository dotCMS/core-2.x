ALTER TABLE field ADD unique_ TINYINT default 0;
update field set unique_ = 0 where unique_ is null;