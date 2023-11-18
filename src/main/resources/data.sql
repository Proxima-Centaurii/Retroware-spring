INSERT INTO CATEGORIES (title, description)
VALUES 
    ('Thinking', 'PLACE HOLDER TEXT'),
    ('Driving', 'PLACE HOLDER TEXT'),
    ('Arcade', 'PLACE HOLDER TEXT'),
    ('Simulation', 'PLACE HOLDER TEXT'),
    ('Action', 'PLACE HOLDER TEXT'),
    ('Sports', 'PLACE HOLDER TEXT'),
    ('Shooting', 'PLACE HOLDER TEXT'),
    ('Fighting', 'PLACE HOLDER TEXT'),
    ('Sand-box', 'PLACE HOLDER TEXT'),
    ('Role-play', 'PLACE HOLDER TEXT'),
    ('Horror', 'PLACE HOLDER TEXT'),
    ('Idle', 'PLACE HOLDER TEXT'),
    ('Exploration', 'PLACE HOLDER TEXT'),
    ('Other', 'PLACE HOLDER TEXT');

/*TEST VALUES*/
INSERT INTO GAMES (title,description,play_count,rating,unlisted,resource_name, publish_date)
VALUES
    ('Centipede','Move the centipede using the arrow keys to collect as many points as possible. Don''t go off the screen or bite yourself!', 9999999, 100, 'False','centipede', '2022-04-07'),
    ('Defender','Shoot the incoming asteroid and do not let them touch or pass you by. The further you go the harder it gets. How long can you survive?', 9999, 75, 'False','defender','2022-04-03'),
    ('Rain of code','Mr Aaaanderson!!', 458, 50, 'False','rain_of_code', '2022-04-03'),
    ('Twenty one','Get a hand of total cards of closer to 21 than the bot without going over 21. Do you feel lucky?', 7840, 25, 'False','twenty_one', '2022-04-03'),
    ('Collision detection','A simple line intersection algorithm. Useful in collision detection! Grab one of the green handles and make the lines cross.', 346, 10, 'False','collision_detection', '2022-04-03'),
    ('TEST #6','Default description.', 50, 0, 'False','game_test', '2022-04-02'),
    ('TEST #7','Default description.', 149, 80, 'False','game_test', '2022-04-02'),
    ('TEST #8','Default description.', 5432, 45, 'False','game_test', '2022-04-02'),
    ('TEST #9','Default description.', 6532, 90, 'True','game_test', '2022-04-02'),
    ('TEST #10','Default description.', 2100, 1, 'True','game_test', '2022-04-02'),
    ('TEST #11','Default description.', 777, 90, 'False','game_test', '2022-04-02');

--Initialise the game ratings count table
INSERT INTO RATINGS_COUNT (GAME_ID)(
    SELECT GAME_ID FROM GAMES WHERE NOT EXISTS(
        SELECT GAME_ID FROM RATINGS_COUNT
    )
);
  
INSERT INTO GAME_CATEGORIES (game_id, category_id) VALUES
    (4,2),
    (4,4),
    (5,1),
    (5,2),
    (6,3),
    (6,4),
    (7,1),
    (7,4),
    (8,3),
    (8,1),
    (9,1),
    (9,4),
    (10,1),
    (10,14),
    (11,14),
    (1,3),
    (2,3),
    (3,4),
    (3,14); 

INSERT INTO FEATURED_GAMES_LIST (game_id) VALUES 1,2,3;
INSERT INTO POPULAR_GAMES_LIST (game_id) VALUES 4,5,6;


--TEST VALUES

--USER: Alex; Password: password
INSERT INTO USERS (USERNAME,PASSWORD,ENABLED) VALUES ('Alex', '$2a$10$mF4mFHe.2vEPiGr9jrczZuOLnXSplKSeaL6rJw2Gqi/dpKl5c5zhi','true');
INSERT INTO AUTHORITIES(USERNAME, AUTHORITY) VALUES ('Alex', 'ROLE_ADMIN');
INSERT INTO USER_PROFILE(USERNAME,JOIN_DATE,DESCRIPTION, PICTURE_NAME) VALUES
    ('Alex', '2023-11-01', 'Welcome to RETROWARE! I am the developer of this website.', '');


--USER: testificate; Password: password
INSERT INTO USERS (USERNAME,PASSWORD,ENABLED) VALUES ('testificate', '$2a$10$mF4mFHe.2vEPiGr9jrczZuOLnXSplKSeaL6rJw2Gqi/dpKl5c5zhi', 'true');
INSERT INTO AUTHORITIES(USERNAME, AUTHORITY) VALUES ('testificate', 'ROLE_USER');
INSERT INTO USER_PROFILE(USERNAME,JOIN_DATE,DESCRIPTION, PICTURE_NAME) VALUES
    ('testificate', '2023-11-01', 'I am a test user.', '');


INSERT INTO GAME_RATINGS(GAME_ID, USERNAME, RATING) VALUES
    (1,'Alex', 1),
    (2,'Alex', 1),
    (3,'Alex', 1),
    (4,'Alex', -1),
    (5,'Alex', -1),
    (6,'Alex', 0);

--This is just for testing. When featured games update the games with id 6,8, and 11 will be in the featured pane
UPDATE RATINGS_COUNT SET LIKES = 3 WHERE GAME_ID IN (6, 8, 11);

INSERT INTO COMMENTS (USERNAME, GAME_ID, COMMENT) VALUES
    ('Alex', 1, 'This was the first game I have programmed in JavaScript.'),
    ('Alex', 2, 'This game becomes more difficult as you progress. I did not intend to make aiming hard but I left it so just because it made the game more challenging'),
    ('Alex', 3, 'This was created just to replicate the "Rain of code" effect that I''ve seen in the Matrix movie and I liked.'),
    ('Alex', 4, 'This game was written to work with both mobile phones and PCs. Like the other games on this website, it was made for testing and practicing purposes.'),
    ('Alex', 5, 'This was small project i wrote a while ago when I was learning about collision detection in video games.');

