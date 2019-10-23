CREATE DATABASE ThuVienDB;
go
USE ThuVienDB;

CREATE TABLE [category] (
	catId varchar(2) PRIMARY KEY,
	catName nvarchar(30) NOT NULL);

CREATE TABLE [publisher] (
	pubId varchar(3) PRIMARY KEY,
	pubName nvarchar(50) NOT NULL);

CREATE TABLE [language] (
	[langId] varchar(2) PRIMARY KEY,
	[language] nvarchar(30) NOT NULL);

CREATE TABLE [book] (
	bid varchar(6) PRIMARY KEY,
	created datetime DEFAULT CURRENT_TIMESTAMP,
	bookName nvarchar(100) NOT NULL,
	price int NOT NULL,
	catId varchar(2) NOT NULL,
	author nvarchar(30) NOT NULL,
	pubId varchar(3) NOT NULL,
	pubYear int NOT NULL,
	[langId] varchar(2) NOT NULL,
	[location] nvarchar(50),
	quantity int NOT NULL,
	availQuantity int NOT NULL,
	FOREIGN KEY (catId) REFERENCES category,
	FOREIGN KEY (pubId) REFERENCES publisher,
	FOREIGN KEY ([langId]) REFERENCES [language]);

CREATE TABLE [reader] (
	rid int IDENTITY(1,1) PRIMARY KEY,
	created datetime DEFAULT CURRENT_TIMESTAMP,
	[name] nvarchar(50) NOT NULL,
	dob date NOT NULL,
	gender bit NOT NULL,
	idCardNum int,
	[address] nvarchar(100),
	canBorrow bit DEFAULT 1 NOT NULL);

CREATE TABLE [staff] (
	[sid] int IDENTITY(1,1) PRIMARY KEY,
	created datetime DEFAULT CURRENT_TIMESTAMP,
	isAdmin bit NOT NULL,
	username varchar(20) NOT NULL,
	[password] binary(256) NOT NULL,
	[name] nvarchar(50) NOT NULL,
	dob date NOT NULL,
	gender bit NOT NULL,
	idCardNum int,
	[address] nvarchar(100));

CREATE TABLE [transaction] (
	transactId int IDENTITY(1,1) PRIMARY KEY,
	borrowingDate datetime NOT NULL,
	rid int NOT NULL,
	borrowStaffId int NOT NULL,
	FOREIGN KEY (rid) REFERENCES reader(rid),
	FOREIGN KEY (borrowStaffId) REFERENCES staff([sid]));

CREATE TABLE [transactionDetail] (
	transactId int NOT NULL,
	bid varchar(6) NOT NULL,
	returnStaffId int,
	dueDate datetime NOT NULL,
	returnDate datetime,
	deposit int NOT NULL,
	isExtended bit DEFAULT 0 NOT NULL,
	FOREIGN KEY (transactId) REFERENCES [transaction],
	FOREIGN KEY (returnStaffId) REFERENCES staff([sid]),
	FOREIGN KEY (bid) REFERENCES book(bid),
	PRIMARY KEY (transactId, bid));
