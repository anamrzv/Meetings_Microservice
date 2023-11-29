create table if not exists chat
(
    id            serial primary key,
    creation_date timestamp default current_timestamp not null
);

create table if not exists message
(
    id           serial primary key,
    chat_id      integer references chat (id),
    sender       integer references user_settings (id),
    content      text                                not null,
    time_to_send timestamp default current_timestamp not null
);

