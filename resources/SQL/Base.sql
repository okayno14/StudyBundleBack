CREATE DATABASE study_bundle;
\c study_bundle

CREATE SEQUENCE hibernate_sequence;

CREATE TYPE method_HTTP AS ENUM ('POST', 'GET', 'PUT', 'DELETE','*');

CREATE TABLE "route"(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    method method_HTTP NOT NULL,
    urn VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE "role"(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    "name" VARCHAR(20) NOT NULL
);

CREATE TABLE role_route(
    id_role BIGINT,
    id_route BIGINT,
    FOREIGN KEY (id_route) REFERENCES "route"(id),
    FOREIGN KEY (id_role) REFERENCES "role"(id),
    PRIMARY KEY (id_role, id_route)
);

CREATE TABLE "group"(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    "name" VARCHAR(255) NOT NULL
);

CREATE TABLE "user"(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    email VARCHAR(255) NOT NULL UNIQUE,
    pass VARCHAR(255) NOT NULL,
    lastname VARCHAR(20) NOT NULL,
    firstname VARCHAR(20) NOT NULL,
    fathername VARCHAR(20) NOT NULL,
    id_role BIGINT NOT NULL,
    id_group BIGINT,
    FOREIGN KEY (id_role) REFERENCES "role"(id),
    FOREIGN KEY (id_group) REFERENCES "group"(id)
);

CREATE TABLE course(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    "name" VARCHAR(255) NOT NULL
);

CREATE TABLE group_course(
    id_group BIGINT NOT NULL,
    id_course BIGINT NOT NULL,
    FOREIGN KEY (id_group) REFERENCES "group"(id),
    FOREIGN KEY (id_course) REFERENCES course(id),
    PRIMARY KEY (id_group, id_course)
);

CREATE TYPE author as ENUM ('author', 'coauthor');

CREATE TABLE course_acl(
    id_user BIGINT NOT NULL,
    id_course BIGINT NOT NULL,
    rights author NOT NULL,
    FOREIGN KEY (id_user) REFERENCES "user"(id),
    FOREIGN KEY (id_course) REFERENCES course(id),
    PRIMARY KEY (id_user,id_course)
);

CREATE TABLE bundle_type(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    "name" VARCHAR(255) NOT NULL
);

CREATE TABLE requirement(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    quantity INTEGER NOT NULL,
    id_bundle_type BIGINT NOT NULL,
    FOREIGN KEY (id_bundle_type) REFERENCES bundle_type(id)
);

CREATE TABLE requirement_course(
    id_course BIGINT NOT NULL,
    id_requirement BIGINT NOT NULL,
    FOREIGN KEY (id_course) REFERENCES course(id),
    FOREIGN KEY (id_requirement) REFERENCES requirement(id),
    PRIMARY KEY (id_course, id_requirement)
);

CREATE TABLE bundle(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    file_name VARCHAR(255),
    num INTEGER NOT NULL,
    id_course BIGINT NOT NULL,
    id_bundle_type BIGINT NOT NULL,
    FOREIGN KEY (id_course) REFERENCES course(id),
    FOREIGN KEY (id_bundle_type) REFERENCES bundle_type(id)
);

CREATE TABLE report(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    sym_count BIGINT,
    unique_words BIGINT,
    word_count BIGINT,
    sym_count_no_space BIGINT,
    id_bundle BIGINT UNIQUE NOT NULL,
    FOREIGN KEY (id_bundle) REFERENCES bundle(id)
);

CREATE TABLE bundle_acl(
    id_user BIGINT NOT NULL,
    id_bundle BIGINT NOT NULL,
    rights author NOT NULL,
    FOREIGN KEY (id_user) REFERENCES "user"(id),
    FOREIGN KEY (id_bundle) REFERENCES bundle(id),
    PRIMARY KEY (id_user,id_bundle)
);