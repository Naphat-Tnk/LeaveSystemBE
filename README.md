## LeaveSystemBE
This project involves the development of the **back-end** system for the organization's leave management platform. The system is responsible for receiving data from the front-end, recording it, and performing updates to the database .

## Features
When a leave request is approved, the system calculates the used leave days and subtracts them from the total number of available leave days to determine the remaining amount. This is automatically recorded in the database.

## Install
### Backend
* PostgreSQL 15.13
* Java 17
* Spring boot 3.3
* Apache Maven 3.9.11

## Dependencies
* Spring Web
* Postgresql
* Spring Data JPA
* Spring Boot DevTools
* spring Security

## How to use
1. Install Project
2. Create database, table and set value in user, leave_type (databasename = leaveDB) Configure database connection in application.properties
```
CREATE TABLE users (
    id SERIAL PRIMARY KEY NOT NULL,
    username VARCHAR(250) NOT NULL,
    email VARCHAR(250) NOT NULL,
    role VARCHAR(100) NOT NULL,
    department VARCHAR(50)
);
```
```
CREATE TABLE leave_types (
    id SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(250) NOT NULL,
    description VARCHAR(500),
    max_days INTEGER
);

```
```
CREATE TABLE leave_requests (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    leave_type_id INTEGER NOT NULL REFERENCES leave_types(id),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    reason VARCHAR(255),
    explain VARCHAR(255)
);

```
```
CREATE TABLE leave_balances (
    id SERIAL PRIMARY KEY NOT NULL,
    user_id INTEGER NOT NULL REFERENCES users(id),
    leave_type_id INTEGER NOT NULL REFERENCES leave_types(id),
    year VARCHAR(255) NOT NULL,
    remaining_days INTEGER NOT NULL
);

```
3. open this project in **IntellJ** and run
4. this step i used postmen to test if you have postman you can use postman

**this project have frontend if you want to try you can go to this link to be download**

https://github.com/Naphat-Tnk/LeaveSystemFE.git

## SonarQube

<img width="1208" height="648" alt="Screenshot 2025-07-29 204814" src="https://github.com/user-attachments/assets/cb88378e-4c21-4a67-aad2-0d26b09de2aa" />

