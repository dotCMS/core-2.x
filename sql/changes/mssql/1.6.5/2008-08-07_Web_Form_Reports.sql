ALTER TABLE report_asset ADD web_form_report TINYINT default 0;
exec ('UPDATE report_asset SET web_form_report=0;');