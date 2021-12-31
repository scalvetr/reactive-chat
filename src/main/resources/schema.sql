CREATE TABLE IF NOT EXISTS messages (
    id SERIAL NOT NULL PRIMARY KEY,
    content VARCHAR(2000) NOT NULL,
    content_type VARCHAR(20) NOT NULL,
    sent TIMESTAMP(3) NOT NULL,
    username VARCHAR(60) NOT NULL,
    user_avatar_image_link VARCHAR(256) NOT NULL
);