# 아파트엔 - 백엔드 (인증 + 소셜 로그인)

## 실행 전 준비

### 1. Java 17 설치 확인
```bash
java -version   # 17 이상 필요
```

### 2. MySQL 세팅
```sql
-- MySQL 접속 후 실행
CREATE DATABASE IF NOT EXISTS apt_management
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;
```
또는 `src/main/resources/schema.sql` 파일을 MySQL에서 직접 실행

### 3. MySQL 계정 확인
`src/main/resources/application.yml`에서 DB 접속 정보 확인:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/apt_management
    username: root      # 본인 MySQL 계정
    password: 1234      # 본인 MySQL 비밀번호
```

### 4. 소셜 로그인 (선택사항 - 1차에서 안 해도 됨)
환경변수 설정 필요:
```bash
# Windows (시스템 환경변수)
GOOGLE_CLIENT_ID=구글콘솔에서발급
GOOGLE_CLIENT_SECRET=구글콘솔에서발급
NAVER_CLIENT_ID=네이버개발자센터에서발급
NAVER_CLIENT_SECRET=네이버개발자센터에서발급
KAKAO_CLIENT_ID=카카오개발자콘솔에서발급
KAKAO_CLIENT_SECRET=카카오개발자콘솔에서발급
```
소셜 로그인 없이 일반 로그인만 테스트하려면 환경변수 없어도 서버 실행 가능

## 실행 방법

### IntelliJ IDEA (추천)
1. IntelliJ에서 이 폴더를 Open
2. Gradle 자동 빌드 대기 (우측 하단 진행바)
3. `AptManagementApplication.java` 열고 ▶ Run 클릭

### 터미널
```bash
# Windows
gradlew.bat bootRun

# Mac/Linux
chmod +x gradlew
./gradlew bootRun
```

### 실행 확인
```
http://localhost:8080
```

## API 목록

| Method | URL | 설명 | 인증 |
|--------|-----|------|------|
| POST | /api/auth/register | 회원가입 | 불필요 |
| POST | /api/auth/login | 로그인 | 불필요 |
| POST | /api/auth/logout | 로그아웃 | 필요 |
| POST | /api/auth/refresh | AT 재발급 | 불필요 |
| GET | /api/auth/me | 내 정보 조회 | 필요 |
