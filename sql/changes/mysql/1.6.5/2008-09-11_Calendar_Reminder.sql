CREATE TABLE `calendar_reminder` (
  `user_id` VARCHAR(100) NOT NULL,
  `event_id` BIGINT(20) UNSIGNED NOT NULL,
  `send_date` DATETIME NOT NULL,
  PRIMARY KEY (`user_id`, `event_id`, `send_date`)
);
