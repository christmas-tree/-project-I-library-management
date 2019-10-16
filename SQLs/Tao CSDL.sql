USE ThuVienDB;

CREATE TABLE [category] (
	catId varchar(2) PRIMARY KEY,
	catName nvarchar(30) NOT NULL);

CREATE TABLE [publisher] (
	pubId varchar(3) PRIMARY KEY,
	pubName nvarchar(50) NOT NULL);

CREATE TABLE [language] (
	languageId varchar(2) PRIMARY KEY,
	languageName nvarchar(30) NOT NULL);

CREATE TABLE [book] (
	bid int IDENTITY(1,1) PRIMARY KEY,
	created datetime DEFAULT CURRENT_TIMESTAMP,
	bookName nvarchar(100) NOT NULL,
	price int NOT NULL,
	catId varchar(2) NOT NULL,
	author nvarchar(30) NOT NULL,
	pubId varchar(3) NOT NULL,
	pubYear int NOT NULL,
	languageId varchar(2) NOT NULL,
	[location] nvarchar(50),
	quantity int NOT NULL,
	availQuantity int NOT NULL,
	FOREIGN KEY (catId) REFERENCES category,
	FOREIGN KEY (pubId) REFERENCES publisher,
	FOREIGN KEY (languageId) REFERENCES [language]);

CREATE TABLE [reader] (
	id int IDENTITY(1,1) PRIMARY KEY,
	created datetime DEFAULT CURRENT_TIMESTAMP,
	[name] nvarchar(50) NOT NULL,
	dob date NOT NULL,
	gender bit NOT NULL,
	idCardNum int,
	[address] nvarchar(100),
	canBorrow bit DEFAULT 1 NOT NULL);

CREATE TABLE [staff] (
	id int IDENTITY(1,1) PRIMARY KEY,
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
	FOREIGN KEY (rid) REFERENCES reader(id),
	FOREIGN KEY (borrowStaffId) REFERENCES staff(id));

CREATE TABLE [transactionDetail] (
	detailId int IDENTITY(1,1) PRIMARY KEY,
	transactId int NOT NULL,
	returnStaffId int,
	dueDate datetime NOT NULL,
	returnDate datetime,
	deposit int NOT NULL,
	isExtended bit DEFAULT 0 NOT NULL,
	FOREIGN KEY (transactId) REFERENCES [transaction],
	FOREIGN KEY (returnStaffId) REFERENCES staff(id));
