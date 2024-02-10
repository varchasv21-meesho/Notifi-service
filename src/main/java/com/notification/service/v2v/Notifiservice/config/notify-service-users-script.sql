USE `notify-service`;

DROP TABLE IF EXISTS `authorities`;
DROP TABLE IF EXISTS `users`;

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `password` char(68) NOT NULL,
  `enabled` tinyint NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Inserting data for table `users`
--
-- NOTE: The passwords are encrypted using BCrypt
--
-- A generation tool is avail at: https://www.luv2code.com/generate-bcrypt-password
--
-- Default passwords here are: test123
--

INSERT INTO `users` 
VALUES 
('user','{bcrypt}$2a$10$mdZ7yl1Aiin8CqE8Dl0Dq.w8o9Sv4o.qlVzwzA38.zK97qfOh4s3O',1),
('editor','{bcrypt}$2a$10$Z4AzzJaLjSJCEgj4AqXoIuXxqBuhmYSy3P5iGr9nlpGVuN9C1wnNS',1),
('admin','{bcrypt}$2a$10$cPyLSjlStCwwpRTcu/sgVOD.qemwym3uJyft7QU2W8.Kn.o/GW0oO',1);


--
-- Table structure for table `authorities`
--

CREATE TABLE `authorities` (
  `username` varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL,
  UNIQUE KEY `authorities4_idx_1` (`username`,`authority`),
  CONSTRAINT `authorities4_ibfk_1` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Inserting data for table `authorities`
--

INSERT INTO `authorities` 
VALUES 
('user','ROLE_USER'),
('editor','ROLE_EDITOR'),
('admin','ROLE_ADMIN');