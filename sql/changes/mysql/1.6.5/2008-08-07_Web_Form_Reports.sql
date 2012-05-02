ALTER TABLE report_asset ADD web_form_report TINYINT(1) default 0;
UPDATE report_asset SET web_form_report=0;