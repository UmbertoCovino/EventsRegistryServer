--drop database if exists events_registry;

create database if not exists events_registry;

use events_registry;

create table if not exists users (
	name varchar(80) not null,
	surname varchar(80) not null,
	email varchar(80) primary key,
	password varchar(20) not null,
	photo_path varchar(80) not null,
	chat_id bigint(20) unsigned 
);

create table if not exists events (
	id int(8) primary key auto_increment,
	title varchar(80) not null,
	start_date datetime not null,
	end_date datetime not null,
	description varchar(200) not null,
	photo_path varchar(80),
	user_owner_email varchar(80) not null,
	foreign key (user_owner_email) references users(email)
);

create table if not exists events_users_participations (
	event_id int(8) not null,
	user_email varchar(80) not null,
	primary key (event_id, user_email),
	foreign key (event_id) references events(id) on update cascade on delete cascade,
	foreign key (user_email) references users(email) on update cascade on delete cascade
);

