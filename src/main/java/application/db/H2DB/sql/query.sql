


-- User

-- createUser
INSERT INTO BE_User (userId, password, userName, email) VALUES ('user123', 'password123', 'test', 'test@naver.com');

-- findUserById
SELECT * FROM BE_User WHERE userId = 'user123';

-- findAll
SELECT usreId FROM BE_User



-- Session

-- addSession
INSERT INTO Session (sessionId, userId) VALUES ('abcd' , 'user123');

-- deleteSession
DELETE FROM Session WHERE userId = 'user123';



-- Article

-- createArticle
INSERT INTO Article (articleIndex, content , userName , filePath) VALUES ('1' , 'foo' ,'user123', '/img/post/fileName');

-- getArticle
SELECT * FROM Article WHERE articleIndex = '1';





