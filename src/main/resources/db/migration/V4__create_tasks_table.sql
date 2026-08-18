CREATE TABLE IF NOT EXISTS tasks (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    deadline TIMESTAMP,
    assigned_to_id UUID,
    created_by_id UUID NOT NULL,
    team_id UUID NOT NULL,
    version BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    FOREIGN KEY (assigned_to_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE RESTRICT,
    FOREIGN KEY (team_id) REFERENCES teams(id) ON DELETE CASCADE
);

CREATE INDEX idx_task_status ON tasks(status);
CREATE INDEX idx_task_deadline ON tasks(deadline);
CREATE INDEX idx_task_assigned_to ON tasks(assigned_to_id);
CREATE INDEX idx_task_team ON tasks(team_id);
CREATE INDEX idx_task_created_by ON tasks(created_by_id);

CREATE TABLE IF NOT EXISTS task_labels (
    task_id UUID NOT NULL,
    label VARCHAR(255) NOT NULL,
    PRIMARY KEY (task_id, label),
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE
);

CREATE INDEX idx_task_labels ON task_labels(label);
