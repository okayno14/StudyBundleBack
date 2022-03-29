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

--Пути
INSERT INTO "route" (method,urn) VALUES ('*', '*');
INSERT INTO role_route VALUES (9,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('POST', '/user');
INSERT INTO role_route VALUES (9,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('POST', '/user/:groupID');

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/:id/');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/:lastName/:firstName/:fatherName');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/:lastName/:firstName/:fatherName/:courseName');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('GET', '/user/:lastName/:firstName/:fatherName/:groupName');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/login');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/forgotPass/:email');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/confirm/:email');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/activate/:userID');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/fio/:userID');

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/pass/:userID');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('PUT', '/user/mail/:userID');
INSERT INTO role_route VALUES (10,(select max(id) from "route"));
INSERT INTO role_route VALUES (11,(select max(id) from "route"));

INSERT INTO "route" (method,urn) VALUES ('DELETE', '/user/:id');