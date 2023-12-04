create table if not exists dialogs
(
    id      serial primary key,
    user_id bigint,
    chat_id bigint
);

