create table if not exists chat
(
    id            serial primary key,
    creation_date timestamp default current_timestamp not null
);

create table if not exists message
(
    id           serial primary key,
    chat_id      integer references chat (id),
    sender       bigint,
    content      text                                not null,
    time_to_send timestamp default current_timestamp not null
);

create table if not exists user_chat
(
    user_id bigint,
    chat_id bigint references chat (id),
    primary key (user_id, chat_id)
);

