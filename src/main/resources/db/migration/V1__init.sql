CREATE TABLE users
(
    id    bigserial primary key,
    first_name varchar(255),
    last_name  varchar(255),
    email      varchar(255),
    password varchar(255)

);

CREATE TABLE address
(
    address_id bigserial primary key,
    address1   text,
    address2   text,
    city       text,
    state      text,
    country    text,
    zip        text

);

CREATE TABLE user_address
(
    id  bigserial primary key,
    user_id integer not null,
    address_id integer not null,
    constraint fk_user_user_id foreign KEY (user_id) references users (id),
    constraint fk_address_address_id foreign KEY (address_id) references address (address_id)
);