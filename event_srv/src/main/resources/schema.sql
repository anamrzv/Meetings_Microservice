create table if not exists characteristic
(
    id   serial primary key,
    name varchar(10) not null
);

create table if not exists event
(
    id              serial primary key,
    name            varchar(40) not null,
    age_limit       integer     not null default 0,
    max_of_visitors integer              default 0,
    description     text        not null,
    image           text        not null,
    start_date      timestamp   not null
);

create table if not exists characteristic_event
(
    characteristic_id integer references characteristic (id),
    event_id          integer references event (id)
);
