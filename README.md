## 관리자 계정 생성

애플리케이션 실행 전 아래 SQL을 실행하여 관리자 계정을 생성합니다.

```sql
USE `cafe-db`;

CREATE TABLE admin
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO admin (username, password)
VALUES (
    'admin',
    '$2a$10$lFtaadAkChkbthlv5C8DYeecEOyj9lKv.ZVu9M9ztM3g/jgSvZ9wO'
);
```

### 관리자 로그인 정보

| 항목 | 값 |
|------|-----|
| 아이디 | `admin` |
| 비밀번호 | `1234` |

> 비밀번호는 BCrypt로 암호화되어 저장되어 있습니다.
