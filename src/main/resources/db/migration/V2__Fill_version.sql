INSERT INTO PUBLIC."ROLE" (ID,DISPLAY_NAME,SYSTEM_NAME) VALUES
	 (1,'Пользователь','USER'),
	 (2,'Администратор','ADMIN');

INSERT INTO PUBLIC.USER_ENTITY (ID,ACTIVE,LOGIN,NAME,PASSWORD,PASSWORD_ACCEPT) VALUES
	 (1,true,'user','user','$2a$10$Pk48ZGt1sb5reh9W2o00OuSaraLCAKGetQznXFYfnLz9m6IgzGBwC',NULL),
	 (2,true,'admin','admin','$2a$10$t3ECfnynBCCZU4d7fofcsuVg4OszPntbj29ai/x/ZumfhphwMnHFi',NULL);

INSERT INTO PUBLIC.USER_ENTITY_ROLES (USER_ENTITY_ID,ROLES_ID) VALUES
    (1,1),
    (2,2);

INSERT INTO PUBLIC.THEME (ID,ESTIMATED_TIME,LEARNED,TEXT,TITLE) VALUES
    (1,10,false,'с1','Тема 1'),
    (2,20,false,'с2','Тема 2'),
    (3,30,false,'с3','Тема 3'),
    (4,10,false,'c4','Тема 4'),
    (5,20,false,'c5','Тема 5'),
    (6,30,false,'c6','Тема 6'),
    (7,30,false,'c7','Тема 7'),
    (8,40,false,'c8','Тема 8');

INSERT INTO PUBLIC.ANSWER (ID,CORRECT,TEXT) VALUES
    (1,false,'неверный1'),
    (2,true,'верный2'),
    (3,false,'неверный3'),
    (4,false,'неверный4');

INSERT INTO PUBLIC.QUESTION (ID,TEXT,STATUS,FAVORITE) VALUES
	 (1,'тествопрос','NOTANSWERED',false),
	 (2,'2тест вопрос','NOTANSWERED',false),
	 (3,'3тест вопрос','NOTANSWERED',false),
	 (4,'4тест вопрос','NOTANSWERED',false),
	 (5,'5тест вопрос','NOTANSWERED',false),
	 (6,'6тест вопрос','NOTANSWERED',false),
	 (7,'7тест вопрос','NOTANSWERED',false),
	 (8,'8тест вопрос','NOTANSWERED',false),
	 (9,'9тест вопрос','NOTANSWERED',false),
	 (10,'10тест вопрос','NOTANSWERED',false);

INSERT INTO PUBLIC.QUESTION_ANSWERS (QUESTION_ID,ANSWERS_ID) VALUES
	 (1,1),
	 (1,2),
	 (1,3),
	 (1,4),
	 (2,1),
	 (2,3),
	 (2,2),
	 (2,4),
	 (3,1),
	 (3,4),
	 (3,3),
	 (3,2),
	 (4,3),
	 (4,2),
	 (4,4),
	 (4,1),
	 (5,3),
	 (5,1),
	 (5,4),
	 (5,2),
	 (6,1),
	 (6,2),
	 (6,3),
	 (6,4),
	 (7,1),
	 (7,3),
	 (7,2),
	 (7,4),
	 (8,1),
	 (8,4),
	 (8,3),
	 (8,2),
	 (9,3),
	 (9,2),
	 (9,4),
	 (9,1),
	 (10,3),
	 (10,1),
	 (10,4),
	 (10,2);

INSERT INTO PUBLIC.TICKET (ID,ERROR_COUNT,LAST_PASS,STATUS) VALUES
	 (1,0,NULL,'NOTANSWERED'),
	 (2,0,NULL,'NOTANSWERED');

INSERT INTO PUBLIC.TICKET_QUESTIONS (TICKET_ID,QUESTIONS_ID) VALUES
	 (1,1),
	 (1,2),
	 (1,3),
	 (1,4),
	 (1,5),
	 (2,6),
	 (2,7),
	 (2,8),
	 (2,9),
	 (2,10);

INSERT INTO PUBLIC.THEME_QUESTIONS (THEME_ID,QUESTIONS_ID) VALUES
    (1,1),
    (1,2),
    (1,3),
    (1,4),
    (1,5),
    (2,6),
    (2,7),
    (2,8),
    (2,9),
    (2,10);
