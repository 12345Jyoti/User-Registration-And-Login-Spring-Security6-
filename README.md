# User-Registration-And-Login-Spring-Security6-
registration
# To create user role table
CREATE TABLE user_roles (
->     user_id BIGINT NOT NULL,
->     role VARCHAR(255) NOT NULL,
->     PRIMARY KEY (user_id, role),
->     FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
-> );
