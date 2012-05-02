ALTER TABLE web_form ADD COLUMN custom_fields2 longtext; 
UPDATE web_form SET custom_fields2 = custom_fields;
ALTER TABLE web_form DROP COLUMN custom_fields;
ALTER TABLE web_form CHANGE custom_fields2 custom_fields longtext;