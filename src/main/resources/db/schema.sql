DROP TABLE IF EXISTS ai_report;
DROP TABLE IF EXISTS audio_record;
DROP TABLE IF EXISTS ward;
DROP TABLE IF EXISTS guardian;

-- ---------------------------------------------
-- guardian
-- ---------------------------------------------
CREATE TABLE guardian (
    guardian_id   INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(60) NOT NULL,
    phone         VARCHAR(20)  NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status        ENUM('active', 'deleted') NOT NULL DEFAULT 'active'
);

-- ---------------------------------------------
-- ward
-- ---------------------------------------------
CREATE TABLE ward (
    ward_id        INT AUTO_INCREMENT PRIMARY KEY,
    guardian_id    INT NOT NULL,

    name           VARCHAR(255) NOT NULL,
    age            INT NOT NULL,
    gender         ENUM('male', 'female') NOT NULL,
    phone          VARCHAR(15) NOT NULL,
    relationship   VARCHAR(50) NOT NULL,

    diagnosis      JSON NULL,

    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status         ENUM('active', 'deleted') NOT NULL DEFAULT 'active',

    CONSTRAINT fk_guardian_to_ward
        FOREIGN KEY (guardian_id)
        REFERENCES guardian(guardian_id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_ward_guardian ON ward(guardian_id);

-- ---------------------------------------------
-- audio_record
-- ---------------------------------------------
CREATE TABLE audio_record (
    record_id       INT AUTO_INCREMENT PRIMARY KEY,
    ward_id         INT NOT NULL,

    uploaded_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    recorded_at     DATETIME NULL,

    file_url        VARCHAR(500) NOT NULL,
    file_format     VARCHAR(20)  NOT NULL,
    
    status          ENUM('uploaded', 'processing', 'completed', 'failed') DEFAULT 'uploaded',
    error_message   TEXT NULL,

    CONSTRAINT fk_ward_to_audio_record
        FOREIGN KEY (ward_id)
        REFERENCES ward(ward_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_record_ward ON audio_record(ward_id);
CREATE INDEX idx_record_recent ON audio_record(ward_id, uploaded_at);

-- ---------------------------------------------
-- ai_report
-- ---------------------------------------------
CREATE TABLE ai_report (
    report_id        INT AUTO_INCREMENT PRIMARY KEY,
    record_id        INT NOT NULL,

    analysis_result  JSON NOT NULL,

    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_record_to_ai_report
        FOREIGN KEY (record_id)
        REFERENCES audio_record(record_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_report_record ON ai_report(record_id);
CREATE INDEX idx_report_recent ON ai_report(created_at);