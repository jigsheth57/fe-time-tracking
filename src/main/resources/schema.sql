drop table if exists time_entry
create table time_entry (time_entry_id bigint not null auto_increment, account_name varchar(255) not null, date date not null, fe_name varchar(255) not null, hours double precision not null, primary key (time_entry_id))
	