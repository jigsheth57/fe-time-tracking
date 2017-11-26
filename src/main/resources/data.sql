-- This file will be automatically run against the app's database on startup.
-- Spring Boot looks for a file named data.sql (and schema.sql) in the classpath
-- to run on startup.
-- See http://docs.spring.io/spring-boot/docs/1.2.2.RELEASE/reference/htmlsingle/#howto-intialize-a-database-using-spring-jdbc
-- for more information.
insert into time_entry(fe_name, account_name, `date`, hours) values ('Chris Moon', 'Kraft Foods', {ts '2015-01-01 00:00:00.00'}, 2.5);
insert into time_entry(fe_name, account_name, `date`, hours) values ('Aakash Shah', 'Centrica', {ts '2015-01-01 00:00:00.00'}, 7.5);
insert into time_entry(fe_name, account_name, `date`, hours) values ('Anupama Pradhan', 'Haitong', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(fe_name, account_name, `date`, hours) values ('Arul Venkatachalam', 'McLaren', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(fe_name, account_name, `date`, hours) values ('Eliot Joslin', 'QET Tech', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(fe_name, account_name, `date`, hours) values ('Leo Gustas', 'ACME', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(fe_name, account_name, `date`, hours) values ('Mahavir Jain', 'Kraft Foods', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(fe_name, account_name, `date`, hours) values ('Mark Ardito', 'Centrica', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(fe_name, account_name, `date`, hours) values ('Daniel Soleymani', 'Pivotal', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(fe_name, account_name, `date`, hours) values ('Mark Ediger', 'Pivotal', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(fe_name, account_name, `date`, hours) values ('Sample feName 1', 'Sample account 1', {ts '2015-01-01 00:00:00.00'}, 1.5);
