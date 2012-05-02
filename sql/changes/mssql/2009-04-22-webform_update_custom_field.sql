ALTER TABLE web_form ADD custom_fields2 text; 
UPDATE web_form SET custom_fields2 = custom_fields;
ALTER TABLE web_form DROP COLUMN custom_fields;
EXEC sp_rename 'web_form.custom_fields2','custom_fields','COLUMN';

