DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE moviedb;
USE moviedb;

CREATE TABLE movies
(	
	id varchar(10) PRIMARY KEY,
	title varchar(100) NOT NULL,
    year integer NOT NULL,
    director varchar(100) NOT NULL
);

CREATE TABLE stars
(	
	id varchar(10) PRIMARY KEY,
	name varchar(100) NOT NULL,
    birthYear integer
);

CREATE TABLE stars_in_movies
(	
	starId varchar(10) NOT NULL,
    movieId varchar(10) NOT NULL,
    FOREIGN KEY (starId) REFERENCES stars(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE genres
(
	id integer AUTO_INCREMENT PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE genres_in_movies
(
	genreId integer NOT NULL,
    movieId varchar(10) NOT NULL,
    FOREIGN KEY (genreId) REFERENCES genres(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE creditcards
(
	id varchar(20) PRIMARY KEY,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    expiration date NOT NULL
);

CREATE TABLE customers
(
	id integer AUTO_INCREMENT PRIMARY KEY,
	firstName varchar(50) NOT NULL,
	lastName varchar(50) NOT NULL,
	ccId varchar(20) NOT NULL,
	address varchar(200) NOT NULL,
	email varchar(50) NOT NULL,
	password varchar(20) NOT NULL,
    FOREIGN KEY (ccId) REFERENCES creditcards(id)
);

CREATE TABLE sales
(
	id integer AUTO_INCREMENT PRIMARY KEY,
    customerId integer NOT NULL,
    movieId varchar(10) NOT NULL,
    saleDate date NOT NULL,
    FOREIGN KEY (customerId) REFERENCES customers(id),
    FOREIGN KEY (movieId) REFERENCES movies(id)
);

CREATE TABLE ratings
(
	movieId varchar(10) NOT NULL,
    rating float NOT NULL,
    numVotes integer NOT NULL,
    FOREIGN KEY (movieId) REFERENCES movies(id)
);