delete from qrtz_simple_triggers where trigger_name like 'com.liferay.%';
delete from qrtz_triggers where job_name like 'com.liferay.%';
delete from qrtz_job_details where job_class_name like 'com.liferay.%';