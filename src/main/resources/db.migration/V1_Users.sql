create table if not exists users
(
    id          BIGSERIAL PRIMARY KEY,
    username    VARCHAR(200) NOT NULL,
    password    VARCHAR(200) NOT NULL,
    email       VARCHAR(200) NOT NULL UNIQUE,
    created_at  TIMESTAMP DEFAULT now(),
    modified_at TIMESTAMP DEFAULT now()
);

create table if not exists roles
(
    id          BIGSERIAL PRIMARY KEY,
    role        VARCHAR(200) NOT NULL UNIQUE,
    created_at  TIMESTAMP DEFAULT now(),
    modified_at TIMESTAMP DEFAULT now()
);

create table if not exists specialization
(
    id          BIGSERIAL PRIMARY KEY,
    spec        VARCHAR(200) NOT NULL UNIQUE,
    created_at  TIMESTAMP DEFAULT now(),
    modified_at TIMESTAMP DEFAULT now()
);

create table if not exists users_role
(
    user_id     bigint not null,
    role_id     int    not null,
    created_at  TIMESTAMP DEFAULT now(),
    modified_at TIMESTAMP DEFAULT now(),
    primary key (user_id, role_id),
    foreign key (user_id) references users (id),
    foreign key (role_id) references roles (id)
);

create table if not exists users_spec
(
    user_id     bigint not null,
    spec_id     int    not null,
    created_at  TIMESTAMP DEFAULT now(),
    modified_at TIMESTAMP DEFAULT now(),
    primary key (user_id, spec_id),
    foreign key (user_id) references users (id),
    foreign key (spec_id) references specialization (id)
);

insert into roles (role)
values ('ROLE_USER'),
       ('ROLE_ADMIN'),
       ('ROLE_CONSULTANT');

insert into specialization (spec)
values ('SPORT'),
       ('TECHNICAL_SUPPORT'),
       ('TECHNICAL'),
       ('BOOK');

insert into specialization (spec)
values ('NO_SPEC');

insert into users (username, password, email)
values ('Ivan', '$2a$12$wzMr4e5/4axPkjCHtnakZOW8m2jgYKnG0.BQuUwwnIkjfxH5UvBaC', 'qwer@email.com'),
       ('Tolya', '$2a$12$wzMr4e5/4axPkjCHtnakZOW8m2jgYKnG0.BQuUwwnIkjfxH5UvBaC', 'asdf@email.com'),
       ('Alex', '$2a$12$wzMr4e5/4axPkjCHtnakZOW8m2jgYKnG0.BQuUwwnIkjfxH5UvBaC', 'zxcv@email.com'),
       ('Not', '$2a$12$wzMr4e5/4axPkjCHtnakZOW8m2jgYKnG0.BQuUwwnIkjfxH5UvBaC', 'q@aqw.com');

insert into users_role (user_id, role_id)
values (1, 1),
       (2, 2),
       (3, 3);

insert into users_spec (user_id, spec_id)
values (1, 1),
       (2, 2),
       (3, 3);

insert into users_role (user_id, role_id)
values (4, 3);

insert into users_spec (user_id, spec_id)
values (3, 4);

