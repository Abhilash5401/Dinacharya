CREATE TABLE IF NOT EXISTS comments (
    id UUID PRIMARY KEY,
    content TEXT NOT NULL,
    author_id UUID NOT NULL,
    task_id UUID NOT NULL,
    flagged BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

CREATE INDEX idx_comment_task ON comments(task_id);
CREATE INDEX idx_comment_author ON comments(author_id);
CREATE INDEX idx_comment_flagged ON comments(flagged);

CREATE TABLE IF NOT EXISTS attachments (
    id UUID PRIMARY KEY,
    file_url VARCHAR(2000) NOT NULL,
    file_name VARCHAR(500) NOT NULL,
    file_type VARCHAR(100),
    task_id UUID NOT NULL,
    uploaded_by_id UUID NOT NULL,
    uploaded_at TIMESTAMP NOT NULL,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_attachment_task ON attachments(task_id);
CREATE INDEX idx_attachment_uploaded_by ON attachments(uploaded_by_id);
