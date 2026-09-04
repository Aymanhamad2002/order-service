CREATE TABLE orders(
    id bigserial primary key not null,
    book_isbn varchar(255) not null,
    book_name varchar(255) null,
    book_price numeric(12,2) null,
    quantity integer not null,
    status varchar(255) not null,
    created_date timestamp not null,
    last_modified_date timestamp not null,
    version integer not null
)