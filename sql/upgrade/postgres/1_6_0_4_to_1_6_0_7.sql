ALTER TABLE field ADD COLUMN unique_ boolean default FALSE;
update field set unique_ = false where unique_ is null;