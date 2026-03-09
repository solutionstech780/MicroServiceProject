CREATE TABLE user_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER',
    CONSTRAINT uk_user_username UNIQUE (username),
    INDEX idx_user_username (username)
);
