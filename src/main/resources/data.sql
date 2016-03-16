-- This file will be automatically run against the app's database on startup.
-- Spring Boot looks for a file named data.sql (and schema.sql) in the classpath
-- to run on startup.
-- See http://docs.spring.io/spring-boot/docs/1.2.2.RELEASE/reference/htmlsingle/#howto-intialize-a-database-using-spring-jdbc
-- for more information.
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (1, 'Chris Moon', 'Kraft Foods', {ts '2015-01-01 00:00:00.00'}, 2.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (2, 'Aakash Shah', 'Centrica', {ts '2015-01-01 00:00:00.00'}, 7.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (3, 'Anupama Pradhan', 'Haitong', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (4, 'Arul Venkatachalam', 'McLaren', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (5, 'Eliot Joslin', 'QET Tech', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (6, 'Leo Gustas', 'ACME', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (7, 'Mahavir Jain', 'Kraft Foods', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (8, 'Mark Ardito', 'Centrica', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (9, 'Daniel Soleymani', 'Pivotal', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (10, 'Mark Ediger', 'Pivotal', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (11, 'Sample feName 1', 'Sample account 1', {ts '2015-01-01 00:00:00.00'}, 1.5);
