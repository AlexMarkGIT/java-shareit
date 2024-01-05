drop all objects;

create table if not exists users (
    id bigint generated by default as identity primary key,
    name varchar(50) not null,
    email varchar(255) unique not null,
    constraint uq_user_email unique (email)
);

create table if not exists items (
    id bigint generated by default  as identity primary key,
    name varchar(50),
    description varchar(255),
    available boolean not null,
    owner_id bigint not null references users(id) on delete cascade
);

create table if not exists bookings (
    id bigint generated by default  as identity primary key,
    start_date timestamp without time zone not null,
    end_date timestamp without time zone not null,
    item_id  bigint not null references items(id),
    booker_id  bigint not null references users(id),
    status varchar(15) not null
);
create table if not exists comments (
    id bigint generated by default  as identity primary key,
    text varchar(255),
    item_id bigint references items(id),
    author_id bigint references users(id),
    created_time timestamp without time zone
);
