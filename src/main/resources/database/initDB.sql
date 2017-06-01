DROP DATABASE IF EXISTS startupdb;
CREATE DATABASE IF NOT EXISTS startupdb
  DEFAULT CHARACTER SET utf8;
USE startupdb;


CREATE TABLE images
(
  id INT(10) unsigned PRIMARY KEY NOT NULL AUTO_INCREMENT,
  data BLOB
);

CREATE TABLE users
(
  id INT(10) unsigned PRIMARY KEY NOT NULL AUTO_INCREMENT,
  username VARCHAR(50) DEFAULT '' NOT NULL,
  password VARCHAR(60) DEFAULT '' NOT NULL,
  contacts VARCHAR(300) DEFAULT '' NOT NULL,
  role ENUM('ADMIN', 'USER') DEFAULT 'USER' NOT NULL,
  locked TINYINT(1) DEFAULT '0' NOT NULL,
  image_id INT(10) unsigned DEFAULT '1',
  CONSTRAINT users_ibfk_1 FOREIGN KEY (image_id) REFERENCES images (id)
);
CREATE INDEX image_id ON users (image_id);

CREATE TABLE startups
(
  id INT(10) unsigned PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(100) DEFAULT '' NOT NULL,
  description VARCHAR(1800) DEFAULT '' NOT NULL,
  min_investment INT(11) DEFAULT '0' NOT NULL,
  need_investment INT(11) DEFAULT '0' NOT NULL,
  author_id INT(10) unsigned NOT NULL,
  image_id INT(10) unsigned DEFAULT '1',
  CONSTRAINT startups_ibfk_2 FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);
CREATE INDEX image_id ON startups (image_id);

CREATE TABLE investments
(
  id INT(10) unsigned PRIMARY KEY NOT NULL AUTO_INCREMENT,
  amount INT(11) DEFAULT '0' NOT NULL,
  startup_id INT(10) unsigned DEFAULT '0',
  investor_id INT(10) unsigned NOT NULL,
  CONSTRAINT investments_ibfk_2 FOREIGN KEY (startup_id) REFERENCES startups (id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT investments_ibfk_1 FOREIGN KEY (investor_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);
