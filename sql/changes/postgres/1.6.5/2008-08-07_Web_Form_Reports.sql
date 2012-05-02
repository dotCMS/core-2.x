ALTER TABLE report_asset ADD web_form_report boolean default FALSE;
UPDATE report_asset SET web_form_report=FALSE;