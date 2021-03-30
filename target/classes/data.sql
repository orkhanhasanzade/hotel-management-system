
-- table for remember me part
create table if not exists persistent_logins (
   username   varchar(64) not null,
   series     varchar(64) not null,
   token      varchar(64) not null,
   last_used  timestamp not null default current_timestamp on update current_timestamp,
   primary key (series)
) engine=InnoDB default charset=utf8;

insert into setting (id, title) values (1, 'Title');

insert into room (id, floor, room_number, type, rent, status, readiness ) values
(1, 1, 1, 'Single', 30, null, 'Clean'),
(2, 1, 2, 'Single', 60, 'on', 'Clean'),
(3, 1, 3, 'Single', 150, 'on', 'Dirty'),
(4, 1, 4, 'Single', 180, 'on', 'Dirty'),
(5, 1, 5, 'Single', 30, 'on', 'Dirty'),
(6, 1, 6, 'Single', 60, 'on', 'Dirty'),
(7, 1, 7, 'Single', 150, 'on', 'Dirty'),
(8, 1, 8, 'Single', 180, 'on', 'Dirty'),
(9, 1, 9, 'Single', 30, 'on', 'Dirty'),
(10, 1, 10, 'Single', 60, 'on', 'Dirty'),
(11, 1, 11, 'Single', 150, 'on', 'Dirty'),
(12, 1, 12, 'Single', 180, 'on', 'Dirty'),
(13, 1, 13, 'Single', 30, 'on', 'Dirty');

insert into guest (id, title, name, surname, middle_name, email, address, image_path) values
(1, 'Mr', 'John', 'Doe', 'Jack', 'example@gmail.com', 'New-York', 'avatar.jpg'),
(2, 'Mr', 'Jack', 'Johnes', 'Bradley', 'example@gmail.com', 'New-York', 'avatar.jpg');

insert into department (id, name, description) values 
(1, 'Management', 'Management of hotel'),
(2, 'Front office', 'Deals with guest when they come'),
(3, 'Housekeeping', 'Deals with management of rooms and cleanliness'),
(4, 'Room service', 'For serving food and beverage for guest'),
(5, 'Kitchen', 'Food production and cooking');

insert into salary (id, currency, amount_per_hour) values 
(1, 'Dollar', 50),
(2, 'Dollar', 40),
(3, 'Dollar', 30),
(4, 'Dollar', 20);

insert into designation (id, name, description, salary_id) values 
(1, 'Hotel Manager', 'Hotel Manager designation', 1),
(2, 'Department Manager', 'Manager designation', 1),
(3, 'Project Manager', 'Project Manager designation', 1),
(4, 'HR-executive', 'HR-executive designation', 3),
(5, 'Cook', 'Cook designation', 4),
(6, 'Waiter', 'Waiter designation', 4),
(7, 'Cleaner', 'Cleaner designation', 4);

insert into employee (id, name, surname, middle_name, birthdate, gender, staff, shift, status, bonus_grade, mobile_number, 
					  address, image_path, department_id, designation_id, salary_id)  values 
(1, 'Tom', 'Ford', 'Jack', '1990-03-28' , 'Male', 'Full-time', 1, 'Active', 75, '055555555', 'New-york', 'avatar.jpg', 1, 1, 1),
(2, 'James', 'Depp', 'Charlie', '1990-03-28' , 'Male', 'Full-time', 1, 'Active', 100, '055555555', 'New-york', 'avatar.jpg', 2, 2, 2),
(3, 'James', 'Depp', 'Charlie', '1990-03-28' , 'Male', 'Full-time', 1, 'Active', 100, '055555555', 'New-york', 'avatar.jpg', 3, 2, 3),
(4, 'Antonio', 'Pitt', 'Charlie', '1990-03-28' , 'Male', 'Full-time', 1, 'Active', 100, '055555555', 'New-york', 'avatar.jpg', 4, 2, 4);

insert into leaves (id, category, from_date, to_date, description, employee_id) values
(1, 'Vacation', NOW(), NOW() + INTERVAL 1 DAY, 'Vacation leave', 1),
(2, 'Vacation', NOW(), NOW() + INTERVAL 1 DAY, 'Vacation leave', 2);

insert into leaves (id, category, from_date, from_time, to_date, to_time, hours, description, employee_id) values
(3, 'Sick', NOW(), '09:00', NOW(), '13:00', 4, 'Sick leave for flu reason', 3),
(4, 'Sick', NOW(), '09:00', NOW(), '13:00', 4, 'Sick leave for flu reason', 4);

insert into holiday (id, name, description, from_date, to_date, month, year) values
(1, 'New Year', 'New Year Holiday', '31/12/2020', '31/12/2020', 12, 2020),
(2, 'Last ring', 'Last ring Holiday', '31/05/2020', '31/05/2020', 5, 2020);

insert into overtime (id, date, hours, employee_id ) values 
(1, NOW(), 5, 1),
(2, NOW(), 6, 2);

insert into payroll (id, year, month, gross_salary, deductions, net_salary, status, employee_id) values
(1, 2020, 06, 1000, 200, 800, 'Unpaid', 1),
(2, 2020, 06, 1000, 200, 800, 'Unpaid', 2);
							
insert into account (id, date, number, name, description, initial_balance, balance, employee_id) values
(1, NOW(), 0001, 'Manager account', 'Dollar account', 100, 100, 1),
(2, NOW(), 0002, 'Employee account', 'Dollar account', 20, 20, 2);
							
insert into expense (id, date, category, currency, amount, description, emp_account_balance, status, department_id, employee_id) 
							values (1, NOW(), 'Office', 'Dollar', 10, 'Expense for office tools', 90, 'Submitted', 2, 1);						
update account set balance=90 where id=1;					
insert into expense (id, date, category, currency, amount, description, emp_account_balance, status, department_id, employee_id) 
							values (2, NOW(), 'Cleaning', 'Dollar', 10, 'Expense for cleaning stuff', 10, 'Submitted', 3, 2);
update account set balance=10 where id=2;																	
							
insert into transaction (id, date, amount, category, description, from_balance, to_balance, from_id, to_id, account_id, account_balance)	
					   values (1, NOW(), 10, 'transfer', 'Transfer money', 80, 20, 1, 2, 1, 0);	
update account set balance=80 where id=1;						   
update account set balance=20 where id=2;	

insert into reservation (id, creation_date, from_date, to_date, amount, payment_status, status) values
(1, NOW(), NOW(), NOW() + INTERVAL 1 DAY, 30, false, 'Pending');

insert into reservation_room (room_id, reservation_id) values
(1, 1);

insert into reservations_guest (guest_id, reservation_id) values
(1, 1);	

insert into roles (id, name) values 
(1, 'ADMIN'),
(2, 'USER');


insert into users (id, email, image_path, password, real_password ,name, active) values
(1, 'example@gmail.com' , 'avatar.jpg', '$2a$10$BnoxmpRmZZJJVTUgBmC0lOj/PZVKgLrcQiWpecwG8nLW46F.nKK8.', '123456', 'Tom Ford', 1),
(2, 'example@gmail.com' , 'avatar.jpg', '$2a$10$BnoxmpRmZZJJVTUgBmC0lOj/PZVKgLrcQiWpecwG8nLW46F.nKK8.', '123456', 'Tom Ford ', 1);

insert into user_role(user_id, role_id) values
(1,1),
(2,2);



