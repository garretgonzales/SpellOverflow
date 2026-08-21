-- seed after testing user auth --


create database IF NOT exists spelloverflow character set utf8mb4 collate utf8mb4_0900_ai_ci;

use spelloverflow;

create table if not exists users
(
    id            BIGINT unsigned NOT null AUTO_INCREMENT,
    username      VARCHAR(50)     NOT null,
    email         VARCHAR(320)    NOT null,
    password_hash VARCHAR(60)     NOT null,
    created_at    DATETIME(3)     NOT null DEFAULT CURRENT_TIMESTAMP(3),
    updated_at    DATETIME(3)     NOT null DEFAULT CURRENT_TIMESTAMP(3) on update CURRENT_TIMESTAMP(3),
    primary key (id),
    constraint uq_users_username unique (username),
    constraint uq_users_email unique (email),
    constraint chk_users_username_not_blank check (CHAR_LENGTH(TRIM(username)) between 3 and 50),
    constraint chk_users_email_not_blank check (CHAR_LENGTH(TRIM(email)) > 0)
) ENGINE = InnoDB;

create table if not exists questions
(
    id         BIGINT unsigned NOT null AUTO_INCREMENT,
    title      VARCHAR(255)    NOT null,
    body       LONGTEXT        NOT null,
    author_id  BIGINT unsigned NOT null,
    created_at DATETIME(3)     NOT null DEFAULT CURRENT_TIMESTAMP(3),
    updated_at DATETIME(3)     NOT null DEFAULT CURRENT_TIMESTAMP(3) on update CURRENT_TIMESTAMP(3),
    primary key (id),
    key idx_questions_created_at (created_at desc, id desc),
    key idx_questions_author_id (author_id),
    constraint fk_questions_author foreign key (author_id) references users (id) on delete restrict on update cascade,
    constraint chk_questions_title_not_blank check (CHAR_LENGTH(TRIM(title)) between 10 and 255),
    constraint chk_questions_body_not_blank check (CHAR_LENGTH(TRIM(body)) > 0)
) ENGINE = InnoDB;

create table if not exists answers
(
    id          BIGINT unsigned NOT null AUTO_INCREMENT,
    body        LONGTEXT        NOT null,
    question_id BIGINT unsigned NOT null,
    author_id   BIGINT unsigned NOT null,
    created_at  DATETIME(3)     NOT null DEFAULT CURRENT_TIMESTAMP(3),
    updated_at  DATETIME(3)     NOT null DEFAULT CURRENT_TIMESTAMP(3) on update CURRENT_TIMESTAMP(3),
    primary key (id),
    key idx_answers_question_created_at (question_id, created_at asc, id asc),
    key idx_answers_author_id (author_id),
    constraint fk_answers_question foreign key (question_id) references questions (id) on delete cascade on update cascade,
    constraint fk_answers_author foreign key (author_id) references users (id) on delete restrict on update cascade,
    constraint chk_answers_body_not_blank check (CHAR_LENGTH(TRIM(body)) > 0)
) ENGINE = InnoDB;

create table if not exists follows
(
    id          BIGINT unsigned NOT null AUTO_INCREMENT,
    user_id     BIGINT unsigned NOT null,
    question_id BIGINT unsigned NOT null,
    created_at  DATETIME(3)     NOT null DEFAULT CURRENT_TIMESTAMP(3),
    primary key (id),
    constraint uq_follows_user_question unique (user_id, question_id),
    key idx_follows_question_id (question_id),
    constraint fk_follows_user foreign key (user_id) references users (id) on delete cascade on update cascade,
    constraint fk_follows_question foreign key (question_id) references questions (id) on delete cascade on update cascade
) ENGINE = InnoDB;