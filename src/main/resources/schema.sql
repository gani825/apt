-- =============================================
-- 아파트 관리 시스템 DB 초기화 스크립트
-- =============================================

CREATE DATABASE IF NOT EXISTS apt_management
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE apt_management;

-- =============================================
-- 공통/인증 관련 테이블
-- =============================================

-- 세대 테이블 (user보다 먼저 생성 필요)
CREATE TABLE IF NOT EXISTS household
(
    household_id BIGINT      NOT NULL AUTO_INCREMENT COMMENT '세대 고유 ID',
    dong         VARCHAR(10) NOT NULL COMMENT '동 번호 (예: 101동)',
    ho           VARCHAR(10) NOT NULL COMMENT '호수 (예: 502호)',
    created_at   DATETIME    NOT NULL DEFAULT NOW() COMMENT '세대 등록 일시',
    PRIMARY KEY (household_id),
    UNIQUE KEY uk_dong_ho (dong, ho)   -- 동+호 중복 방지
) COMMENT '세대 정보';

-- 사용자 테이블
CREATE TABLE IF NOT EXISTS user
(
    user_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '사용자 고유 ID',
    household_id BIGINT                COMMENT '소속 세대 ID (FK → household, 소셜 로그인은 null)',
    email        VARCHAR(100) NOT NULL COMMENT '로그인 이메일 (UNIQUE)',
    password     VARCHAR(255) NOT NULL COMMENT 'BCrypt 암호화 비밀번호 (소셜 로그인은 랜덤 생성)',
    name         VARCHAR(50)  NOT NULL COMMENT '사용자 이름',
    phone        VARCHAR(20)           COMMENT '휴대폰 번호',
    role         ENUM ('RESIDENT', 'ADMIN') NOT NULL DEFAULT 'RESIDENT' COMMENT '권한',
    -- ★2차 승인 시스템 준비 컬럼 (1차에서는 DEFAULT PENDING, 실제 체크 로직은 2차)
    status       ENUM ('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '승인 상태',
    approved_by  BIGINT                COMMENT '승인한 관리자 ID (★2차)',
    approved_at  DATETIME              COMMENT '승인/거부 일시 (★2차)',
    -- 소셜 로그인 정보 (일반 로그인은 LOCAL)
    provider     VARCHAR(20)  NOT NULL DEFAULT 'LOCAL' COMMENT '로그인 제공자 (LOCAL/GOOGLE/KAKAO/NAVER)',
    provider_id  VARCHAR(100)          COMMENT '소셜 로그인 제공자 고유 사용자 ID',
    created_at   DATETIME     NOT NULL DEFAULT NOW() COMMENT '가입 일시',
    updated_at   DATETIME              COMMENT '수정 일시',
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_provider (provider, provider_id), -- 소셜 계정 중복 방지
    FOREIGN KEY fk_user_household (household_id) REFERENCES household (household_id)
) COMMENT '사용자 정보';

-- Refresh Token 저장 테이블
CREATE TABLE IF NOT EXISTS refresh_token
(
    token_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '토큰 고유 ID',
    user_id       BIGINT       NOT NULL COMMENT '토큰 소유자 ID (FK → user)',
    refresh_token VARCHAR(500) NOT NULL COMMENT 'Refresh Token 문자열',
    expired_at    DATETIME     NOT NULL COMMENT '토큰 만료 시간',
    created_at    DATETIME     NOT NULL DEFAULT NOW() COMMENT '토큰 발급 일시',
    PRIMARY KEY (token_id),
    FOREIGN KEY fk_token_user (user_id) REFERENCES user (user_id) ON DELETE CASCADE
) COMMENT 'Refresh Token 저장';

-- =============================================
-- 테스트용 관리자 계정 (BCrypt: admin1234)
-- =============================================
INSERT IGNORE INTO household (dong, ho)
VALUES ('101동', '관리자');

INSERT IGNORE INTO user (household_id, email, password, name, role, status)
VALUES (
    (SELECT household_id FROM household WHERE dong='101동' AND ho='관리자'),
    'admin@apt.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh72', -- admin1234
    '관리자',
    'ADMIN',
    'APPROVED'
);
