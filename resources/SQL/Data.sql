--Типы работ
/*Лаба, Курсовая, РГЗ, Реферат, Контрольная работа, ВКР, Проверочная работа, Доклад*/
INSERT INTO bundle_type ("name") VALUES ('Лабораторная работа');
INSERT INTO bundle_type ("name")  VALUES ('Курсовая работа');
INSERT INTO bundle_type ("name")  VALUES ('РГЗ');
INSERT INTO bundle_type ("name")  VALUES ('Реферат');
INSERT INTO bundle_type ("name")  VALUES ('Контрольная работа');
INSERT INTO bundle_type ("name")  VALUES ('ВКР');
INSERT INTO bundle_type ("name")  VALUES ('Проверочная работа');
INSERT INTO bundle_type ("name")  VALUES ('Доклад');

--Роли
--id=9
INSERT INTO "role" ("name") VALUES ('Администратор');
--id=10
INSERT INTO "role" ("name") VALUES ('Преподаватель'); 
--id=11
INSERT INTO "role" ("name") VALUES ('Студент');
--id=12
INSERT INTO "role" ("name") VALUES ('Гость');

--Первый админ
INSERT INTO "user" ("email","email_state","pass","lastname","firstname","fathername","id_role") 
VALUES ('admin@host.ru',1,'password','Администратор','Администратор','Администратор',9);

--Пути
INSERT INTO "route" (method,urn) VALUES ('ANY', 'ANY');
INSERT INTO role_route VALUES (9,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('POST', '/user/');
--INSERT INTO role_route VALUES (9,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('POST', '/user/:groupID');

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/:id/');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/:email/');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/:lastName/:firstName/:fatherName/:roleID');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/:lastName/:firstName/:fatherName/:courseName');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/:lastName/:firstName/:fatherName/:groupName');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/login');
INSERT INTO role_route VALUES (12,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/logout');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));
INSERT INTO role_route VALUES (12,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/forgotPass/:email');
INSERT INTO role_route VALUES (12,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/resetPass/:passHash/:id');
INSERT INTO role_route VALUES (12,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/confirm/:email');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));
INSERT INTO role_route VALUES (12,(select max(id) from "route"));


INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/activate/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));
INSERT INTO role_route VALUES (12,(select max(id) from "route"));


INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/fio/:id');

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/pass/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/mail/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('DELETE', '/user/:id');

INSERT INTO "route" (method,urn) VALUES ('POST', '/user/group/:name');

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/group/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/group/:courseID');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/group/:name');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/group/:studentID');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/group/students/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/group/addStudents/:id');
INSERT INTO "route" (method,urn) VALUES ('DELETE', '/user/group/:id');

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/role');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('POST', '/course/');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/course/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/course/owner/name/:ownerID/:name');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/course/owner/:ownerID');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/course/group/:groupID');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/course/addCoAuthor/:id/:coAuthorID');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/course/delCoAuthor/:id/:coAuthorID');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/course/addGroup/:groupID/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/course/delGroup/:groupID/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/course/:id/:name');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/course/publish/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('DELETE', '/course/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('POST', '/course/requirement/:courseID/:bundleTypeID/:q');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/course/requirement/:courseID/:bundleTypeID/:q');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('DELETE', '/course/requirement/:courseID/:bundleTypeID/:q');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/bundle/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/bundle/:courseName/:ownerGroupName/:ownerLastName/:ownerFirstName/:ownerFatherName');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/bundle/:courseID/:ownerID');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/bundle/download/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('POST', '/bundle/upload/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/bundle/addCoAuthor/:id/:coAuthorID');
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/bundle/delCoAuthor/:id/:coAuthorID');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES('PUT', '/bundle/cancel/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('DELETE', '/bundle/:id');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('POST', '/bundle/bundleType/:name');

INSERT INTO "route" (method,urn) VALUES ('GET', '/bundle/bundleType');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/bundle/bundleType/:id/:name');
INSERT INTO "route" (method,urn) VALUES ('DELETE', '/bundle/bundleType/:id');