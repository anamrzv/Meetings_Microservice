create table if not exists user_chat
(
    id      serial primary key,
    user_id bigint,
    chat_id bigint
);

