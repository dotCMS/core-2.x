update qrtz_job_details set job_class_name='com.dotmarketing.quartz.job.DeliverCampaignThread' where job_class_name='com.dotmarketing.threads.DeliverCampaignThread';
update qrtz_job_details set job_class_name='com.dotmarketing.quartz.job.UpdateRatingThread' where job_class_name='com.dotmarketing.threads.UpdateRatingThread';
update qrtz_job_details set job_class_name='com.dotmarketing.quartz.job.ContentReviewThread' where job_class_name='com.dotmarketing.threads.ContentReviewThread';
update qrtz_job_details set job_class_name='com.dotmarketing.quartz.job.ContentIndexationThread' where job_class_name='com.dotmarketing.threads.ContentIndexationThread';
update qrtz_job_details set job_class_name='com.dotmarketing.quartz.job.ContentReindexerThread' where job_class_name='com.dotmarketing.threads.ContentReindexerThread';
update qrtz_job_details set job_class_name='com.dotmarketing.quartz.job.PopBouncedMailThread' where job_class_name='com.dotmarketing.threads.PopBouncedMailThread';