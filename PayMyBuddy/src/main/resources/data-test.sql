DELETE FROM transactions;
DELETE FROM user_user;
DELETE FROM users;
ALTER TABLE users AUTO_INCREMENT = 1;

INSERT INTO users (id, username, email, password) 
VALUES
(1,"Harry", "hpotter@gryffondor.com", "$2a$10$xMy7iuC9JgW7pTtinDLzZeSCGsWjqhEj/AyiixHSphxeYcMks5roC"),
(2,"Hermione", "hermione@gryffondor.com", "$2a$10$yang3q.Y4NtxJjW6Gv5SVeRMPrg8qgUKR8u7/fwJwAJAC15696MBq"),
(3,"Ron", "ron@gryffondor.com", "$2a$10$bxKdVt5y5UP85ub4tIlyf.5xShX8TJUTfxkJLC.M3IbSfiMi6LJLq"),
(4,"Drago", "dmalefoy@serpentar.com", "$2a$10$TCy0QXDIl2ZCCyF.vQ9ajuR6K0Pp1qEUkRMAUGK4FKqoTO4NtRiT6"),
(5,"McGonagall", "mcgonagall@poudlard.com", "$2a$10$minchYaH6Pi.HXvs7WmdbulZ5eJw1U32R.37UWsdII.pxZDmodnd2"),
(6,"Hagrid", "hagrid@poudlard.com", "$2a$10$YJ4F8G7ImmwiqXX4FknhNe/OVhs97zYYQyYE3mvIAjtoFYKHLRMQK"), 
(7,"Neuville", "nlondubat@gryffondor.com", "$2a$10$yb5iIU8Fx2aZhyLe.5uggOo5sv3gMNvBNYwtjIKHf8MEFtUmvDDZS");

INSERT INTO user_user (user_id, connected_user_id) 
VALUES 
(1,2), (2,1);


INSERT INTO transactions (description, amount, sender_id, receiver_id)
VALUES 
("nouvelle baguette magique", 75, 1,2),
("pour tes lunettes", 40, 4, 1),
("vive les mariés", 150, 5, 1),
("HP & Ginny", 100, 7, 1),
("bon mariage", 125, 6, 1),
("bieraubeurre de samedi", 12.55, 3, 7),
("joyeux anniversaire", 75, 2, 3),
("HB", 50, 7, 3), 
("pour ton nimbus 3000 !", 175, 1, 3);