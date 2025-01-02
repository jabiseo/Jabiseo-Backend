/*
certificate / subject / exam
member
plan
plan item 3개

*/


insert into `certificate` (`certificate_id`, `name`)
values (1, '정보처리기사');

insert into `member` (`member_id`, `email`, `nickname`, `profile_image`, `oauth_id`, `oauth_server`, `deleted`,
                      `current_certificate_id`, `last_access_at`, `created_at`, `modified_at`)
values (1, 'email@email.com', 'hyeok', 'profile', '1', 'KAKAO', 0, 1, null, null, null);

insert into `plan` (`plan_id`, `end_at`,`created_at`, `certificate_id`, `member_id`)
values (1, '2024-12-03','2024-12-01', 1, 1);

insert into `plan_item` (`plan_item_id`, `plan_id`, `activity_type`, `goal_type`, `target_value`, `created_at`)
values
     (1, 1, 'EXAM', 'DAILY', 100, '2024-12-01'),
     (2, 1, 'STUDY', 'DAILY', 100, '2024-12-01'),
     (3, 1, 'TIME', 'DAILY', 100, '2024-12-01');
