-- User Table Create SQL
CREATE TABLE BE_User
(
    `userId`    VARCHAR(50)    NOT NULL,
    `password`  VARCHAR(50)    NULL,
    `userName`  VARCHAR(50)    NULL,
    `email`     VARCHAR(50)    NULL,
     PRIMARY KEY (userId)
);

-- Article FK 위해 UNIQUE 설정
ALTER TABLE BE_User
ADD CONSTRAINT uk_userName UNIQUE (userName);

-- Article Table Create SQL
CREATE TABLE Article
(
    `articleIndex`  INT             NOT NULL    AUTO_INCREMENT,
    `content`       VARCHAR(200)    NULL,
    `userName`      VARCHAR(50)     NULL,
    `filePath`      VARCHAR(50)     NULL,
     PRIMARY KEY (articleIndex)
);

-- Foreign Key 설정 SQL - Article(userName) -> User(userName)
ALTER TABLE Article
    ADD CONSTRAINT FK_Article_userName_BE_User_userName FOREIGN KEY (userName)
        REFERENCES BE_User (userName) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- Session Table Create SQL
CREATE TABLE Session
(
    `sessionId`  VARCHAR(50)    NOT NULL,
    `userId`     VARCHAR(50)    NULL,
     PRIMARY KEY (sessionId)
);

-- Foreign Key 설정 SQL - Session(userId) -> User(userId)
ALTER TABLE Session
    ADD CONSTRAINT FK_Session_userId_BE_User_userId FOREIGN KEY (userId)
        REFERENCES BE_User (userId) ON DELETE RESTRICT ON UPDATE RESTRICT;

-- Comment Table Create SQL
CREATE TABLE Comment
(
    `articleIndex`  INT             NOT NULL,
    `content`       VARCHAR(200)    NULL,
     PRIMARY KEY (articleIndex)
);

-- Foreign Key 설정 SQL - Comment(articleIndex) -> Article(articleIndex)
ALTER TABLE Comment
    ADD CONSTRAINT FK_Comment_articleIndex_Article_articleIndex FOREIGN KEY (articleIndex)
        REFERENCES Article (articleIndex) ON DELETE RESTRICT ON UPDATE RESTRICT;