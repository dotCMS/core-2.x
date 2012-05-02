ALTER TABLE web_form ADD custom_fieldstmp varchar2(2000);
update web_form set custom_fieldstmp = SUBSTR(custom_fields,1,2000);
ALTER TABLE web_form DROP COLUMN custom_fields;
ALTER TABLE web_form RENAME COLUMN custom_fieldstmp TO custom_fields;
