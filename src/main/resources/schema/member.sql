create table member (
	user_id int not null auto_increment,
    username VARCHAR(30) not null unique,
    password VARCHAR(64) not null,
    email VARCHAR(255) not null,
    type VARCHAR(10),
    registered_time timestamp default now(),
	primary key (user_id)
);

select * from member;
truncate table member;