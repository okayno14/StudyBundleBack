CREATE SEQUENCE hibernate_sequence;

CREATE TYPE method_HTTP AS ENUM ('POST', 'GET', 'PUT', 'DELETE','ANY');

CREATE TABLE "route"(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    method method_HTTP NOT NULL,
    urn VARCHAR(255) NOT NULL,
    UNIQUE (method,urn)
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
    "name" VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE "user"(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    email VARCHAR(255) NOT NULL UNIQUE,
    email_state INTEGER NOT NULL,
    pass VARCHAR(255) NOT NULL,
    lastname VARCHAR(20) NOT NULL,
    firstname VARCHAR(20) NOT NULL,
    fathername VARCHAR(20) NOT NULL,
    id_role BIGINT NOT NULL,
    id_group BIGINT,
    FOREIGN KEY (id_role) REFERENCES "role"(id),
    FOREIGN KEY (id_group) REFERENCES "group"(id)
);

CREATE TYPE course_state as ENUM ('EMPTY', 'IN_PROGRESS', 'PUBLISHED');

CREATE TABLE course(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    "name" VARCHAR(255) NOT NULL,
    state course_state NOT NULL
);

CREATE TABLE group_course(
    id_group BIGINT NOT NULL,
    id_course BIGINT NOT NULL,
    FOREIGN KEY (id_group) REFERENCES "group"(id),
    FOREIGN KEY (id_course) REFERENCES course(id),
    PRIMARY KEY (id_group, id_course)
);

CREATE TYPE author as ENUM ('AUTHOR', 'COAUTHOR');

CREATE TABLE course_acl(
    user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    rights author NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"(id),
    FOREIGN KEY (course_id) REFERENCES course(id),
    PRIMARY KEY (user_id,course_id)
);

CREATE TABLE bundle_type(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    "name" VARCHAR(255) CONSTRAINT bundle_type_unique NOT NULL UNIQUE
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

CREATE TYPE bundle_state AS ENUM ('EMPTY', 'CANCELED', 'ACCEPTED');

CREATE TABLE report(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    file_name VARCHAR(255),
    sym_count BIGINT,
    unique_words BIGINT,
    word_count BIGINT,
    sym_count_no_space BIGINT
);

CREATE TABLE bundle(
    id BIGINT PRIMARY KEY DEFAULT nextval('hibernate_sequence'),
    folder VARCHAR(255),
    num INTEGER NOT NULL,
    state bundle_state NOT NULL,
    id_course BIGINT NOT NULL,
    id_bundle_type BIGINT NOT NULL,
    id_report BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (id_course) REFERENCES course(id),
    FOREIGN KEY (id_bundle_type) REFERENCES bundle_type(id),
    FOREIGN KEY (id_report) REFERENCES report(id)
);

CREATE TABLE bundle_acl(
    user_id BIGINT NOT NULL,
    bundle_id BIGINT NOT NULL,
    rights author NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"(id),
    FOREIGN KEY (bundle_id) REFERENCES bundle(id),
    PRIMARY KEY (user_id,bundle_id)
);