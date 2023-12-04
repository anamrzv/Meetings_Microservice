create table if not exists meetings
(
    id      serial primary key,
    user_id bigint,
    event_id bigint
);

