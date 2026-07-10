-- Flyway가 앱 부팅 시 자동 실행. outputs/schema.sql과 내용 동일 (여기가 실제로 실행되는 원본).

CREATE TABLE users (
    user_id        VARCHAR(50)  PRIMARY KEY,
    toss_user_key  VARCHAR(100) NOT NULL UNIQUE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE recommendations (
    recommendation_id VARCHAR(50)   PRIMARY KEY,
    user_id            VARCHAR(50)   NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    region_name        VARCHAR(100)  NOT NULL,
    travel_date        DATE          NOT NULL,
    activity_type      VARCHAR(20)   NOT NULL CHECK (activity_type IN ('sea','camping','city','cafe')),
    weather_snapshot   JSONB         NOT NULL,
    recommended_items  JSONB         NOT NULL,
    created_at         TIMESTAMPTZ   NOT NULL DEFAULT now()
);
CREATE INDEX idx_recommendations_user_id ON recommendations(user_id);

CREATE TABLE checklists (
    checklist_id      VARCHAR(50) PRIMARY KEY,
    user_id           VARCHAR(50) NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
    recommendation_id VARCHAR(50) NOT NULL REFERENCES recommendations(recommendation_id) ON DELETE CASCADE,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_checklists_user_id ON checklists(user_id);
CREATE UNIQUE INDEX idx_checklists_recommendation_id ON checklists(recommendation_id);

CREATE TABLE checklist_items (
    checklist_item_id VARCHAR(50)  PRIMARY KEY,
    checklist_id       VARCHAR(50)  NOT NULL REFERENCES checklists(checklist_id) ON DELETE CASCADE,
    name                VARCHAR(100) NOT NULL,
    is_essential        BOOLEAN      NOT NULL DEFAULT false,
    checked             BOOLEAN      NOT NULL DEFAULT false,
    reason               VARCHAR(255),
    sort_order           SMALLINT     NOT NULL DEFAULT 0
);
CREATE INDEX idx_checklist_items_checklist_id ON checklist_items(checklist_id);
