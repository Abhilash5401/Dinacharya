CREATE TABLE IF NOT EXISTS user_skills (
    user_id UUID NOT NULL,
    skill VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_id, skill),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
