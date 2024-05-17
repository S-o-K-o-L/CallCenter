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
values ('Ivan', '1', 'qwer@email.com'),
       ('Tolya', '1', 'asdf@email.com'),
       ('Alex', '1', 'zxcv@email.com');

insert into users_roles (user_id, role_id)
values (1, 1),
       (2, 2),
       (3, 3);

