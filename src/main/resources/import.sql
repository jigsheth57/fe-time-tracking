-- This file will be automatically run against the app's database on startup.
-- Spring Boot looks for a file named data.sql (and schema.sql) in the classpath
-- to run on startup.
-- See http://docs.spring.io/spring-boot/docs/1.2.2.RELEASE/reference/htmlsingle/#howto-intialize-a-database-using-spring-jdbc
-- for more information.
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (1, 'Dennis Meinecke', 'United', {ts '2015-01-01 00:00:00.00'}, 8.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (2, 'Vijay Karthik', 'Rhino Security Labs', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (3, 'Vijay Karthik', 'Rhino Security Labs', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (4, 'Dennis Meinecke', 'United', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (5, 'Reto Lichtensteiger', 'Rhino Security Labs', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (6, 'Fred Kern', 'ACME', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (7, 'Reto Lichtensteiger', 'Rhino Security Labs', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (8, 'John Lee', 'Pivotal', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (9, 'Adam Johnson', 'Pivotal', {ts '2015-01-01 00:00:00.00'}, 1.5);
insert into time_entry(time_entry_id, fe_name, account_name, `date`, hours) values (10, 'Vijay Karthik', 'Pivotal', {ts '2015-01-01 00:00:00.00'}, 1.5);
