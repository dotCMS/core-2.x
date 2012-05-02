-- STATUS SCRIPTS

delete from status;
insert into status values (1,'New',1);
insert into status values (2,'Open',2);
insert into status values (3,'FeedBack Pending',1);
insert into status values (4,'Publish Pending',1);
insert into status values (5,'Published',4);
insert into status values (6,'Closed',1);
insert into status values (7,'Cancelled',1);

-- ACTION SCRIPTS

delete from action;
insert into action values (1,'Request a Change',1,2,1,'t');
insert into action values (2,'Request Feedback',2,3,2,'f');
insert into action values (3,'Send Feedback',3,2,1,'f');
insert into action values (4,'Request Publish',2,4,2,'f');
insert into action values (5,'Publish',4,5,4,'f');
insert into action values (6,'Publish',2,5,4,'f');
insert into action values (7,'Close',5,6,4,'f');
insert into action values (8,'Reopen',5,2,4,'f');
insert into action values (9,'Reopen',6,2,4,'f');
insert into action values (10,'Reopen',4,2,4,'f');
insert into action values (11,'Cancel Workflow',3,7,1,'f');
insert into action values (12,'Cancel Workflow',4,7,4,'f');
insert into action values (13,'Cancel Workflow',5,7,4,'f');
insert into action values (14,'Cancel Workflow',6,7,4,'f');
insert into action values (15,'Cancel Workflow',2,7,2,'f');
