-- =============================================
-- AnyOtherDay MVP Schema (Integrated)
-- Tables: Guardian, Ward, AudioRecord, AIReport
-- =============================================

DROP TABLE IF EXISTS AIReport;
DROP TABLE IF EXISTS AudioRecord;
DROP TABLE IF EXISTS Ward;
DROP TABLE IF EXISTS Guardian;

-- ---------------------------------------------
-- 1. Guardian (보호자)
-- ---------------------------------------------
CREATE TABLE Guardian (
    guardian_id   INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,   -- MVP: plain text
    phone         VARCHAR(20)  NOT NULL,
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status        ENUM('active', 'deleted') NOT NULL DEFAULT 'active'
);

-- ---------------------------------------------
-- 2. Ward (피보호자)
-- ---------------------------------------------
CREATE TABLE Ward (
    ward_id        INT AUTO_INCREMENT PRIMARY KEY,
    guardian_id    INT NOT NULL,

    name           VARCHAR(255) NOT NULL,
    age            INT NOT NULL,
    gender         ENUM('MALE', 'FEMALE') NOT NULL,
    phone          VARCHAR(15) NOT NULL,
    relationship   VARCHAR(50) NOT NULL,

    diagnosis      JSON NOT NULL,

    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    status         ENUM('active', 'deleted') NOT NULL DEFAULT 'active',

    CONSTRAINT FK_Guardian_TO_Ward
        FOREIGN KEY (guardian_id)
        REFERENCES Guardian(guardian_id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_ward_guardian ON Ward(guardian_id);

-- ---------------------------------------------
-- 3. AudioRecord (음성 기록)
-- ---------------------------------------------
CREATE TABLE AudioRecord (
    record_id       INT AUTO_INCREMENT PRIMARY KEY,
    ward_id         INT NOT NULL,

    uploaded_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, -- 서버 업로드 시각
    recorded_at     DATETIME NULL,                              -- 실제 녹음 시각

    file_url        VARCHAR(500) NOT NULL,
    file_format     VARCHAR(10)  NOT NULL,  -- wav/mp3/m4a 등
    status          ENUM('pending', 'processed', 'failed') DEFAULT 'pending',

    CONSTRAINT FK_Ward_TO_AudioRecord
        FOREIGN KEY (ward_id)
        REFERENCES Ward(ward_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_record_ward ON AudioRecord(ward_id);
CREATE INDEX idx_record_recent ON AudioRecord(ward_id, uploaded_at);

-- ---------------------------------------------
-- 4. AIReport (AI 레포트)
-- ---------------------------------------------
CREATE TABLE AIReport (
    report_id        INT AUTO_INCREMENT PRIMARY KEY,
    record_id        INT NOT NULL, 

    analysis_result  JSON NOT NULL, -- 모델 응답 원본

    created_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT FK_Record_TO_AIReport
        FOREIGN KEY (record_id)
        REFERENCES AudioRecord(record_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_report_record ON AIReport(record_id);
CREATE INDEX idx_report_recent ON AIReport(created_at);