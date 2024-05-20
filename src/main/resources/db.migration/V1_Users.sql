create table if not exists users
(
    id       BIGSERIAL PRIMARY KEY,
    username VARCHAR(200) NOT NULL,
    password VARCHAR(200) NOT NULL,
    email    VARCHAR(200) NOT NULL UNIQUE
);

create table if not exists roles
(
    id   BIGSERIAL PRIMARY KEY,
    role VARCHAR(200) NOT NULL UNIQUE
);

create table if not exists users_roles
(
    user_id bigint not null,
    role_id int    not null,
    primary key (user_id, role_id),
    foreign key (user_id) references users (id),
    foreign key (role_id) references roles (id)
);

insert into roles (role)
values ('USER_ROLE'),
       ('ADMIN_ROLE'),
       ('CONSULTANT_ROLE');

insert into users (username, password, email)
values ('Ivan', '$2a$12$wzMr4e5/4axPkjCHtnakZOW8m2jgYKnG0.BQuUwwnIkjfxH5UvBaC', 'qwer@email.com'),
       ('Tolya', '$2a$12$wzMr4e5/4axPkjCHtnakZOW8m2jgYKnG0.BQuUwwnIkjfxH5UvBaC', 'asdf@email.com'),
       ('Alex', '$2a$12$wzMr4e5/4axPkjCHtnakZOW8m2jgYKnG0.BQuUwwnIkjfxH5UvBaC', 'zxcv@email.com');

insert into users_roles (user_id, role_id)
values (4, 1),
       (5, 2),
       (6, 3);

