ALTER TABLE web_form ADD  (custom_fields2 clob);
UPDATE web_form SET custom_fields2 = custom_fields;
ALTER TABLE web_form DROP COLUMN custom_fields;
ALTER TABLE web_form RENAME COLUMN custom_fields2 TO custom_fields;