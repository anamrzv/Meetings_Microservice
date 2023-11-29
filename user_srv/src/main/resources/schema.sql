create table if not exists profile
(
    id            serial primary key,
    sign_up_date  timestamp default current_timestamp not null,
    first_name    varchar(30)                         not null,
    last_name     varchar(30)                         not null,
    date_of_birth date                                not null,
    phone         varchar(20)                         not null,
    mail          varchar(320)                        not null,
    icon          text                                not null
);

create table if not exists roles
(
    role_id   integer primary key,
    name varchar(10) not null
);

create table if not exists user_settings
(
    id              serial primary key,
    login           varchar(20) not null unique,
    hashed_password varchar(60) not null,
    role            integer references roles (role_id) not null,
    profile_id      integer references profile (id)
);
