# Devport Backend API

devport (devport.kr) - 해외 개발자 콘텐츠를 한국어 요약으로 제공하는 서비스

## 기술 스택

- **Spring Boot 4.0.0** + Java 25
- **Spring Data JPA** + PostgreSQL (prod) / H2 (dev)
- **Spring Security** + OAuth2 (GitHub, Google)
- **JWT** 인증 (Access + Refresh Token)
- **Redis** 캐싱
- **Cloudflare Turnstile** 봇 방지

## 프로젝트 구조

```
src/main/java/kr/devport/api/
├── config/          # CORS, Security 설정
├── domain/          # Entity, Enum
├── repository/      # JPA Repository
├── security/        # JWT, OAuth2 인증
├── service/         # 비즈니스 로직
├── controller/      # REST API
└── dto/             # Request/Response DTO
```

## API 엔드포인트

### 게시글
- `GET /api/articles` - 메인 피드 (페이지네이션, 카테고리 필터)
- `GET /api/articles/github-trending` - GitHub 트렌딩 저장소
- `GET /api/articles/trending-ticker` - 티커용 트렌딩 게시글

### 인증
- `GET /api/auth/me` - 현재 사용자 정보 (인증 필요)
- `POST /api/auth/refresh` - Access Token 갱신
- `POST /api/auth/logout` - 로그아웃 (Refresh Token 폐기)

### LLM 순위
- `GET /api/llm-rankings` - 벤치마크별 LLM 순위
- `GET /api/benchmarks` - 전체 벤치마크 목록

## OAuth2 로그인 흐름

1. **프론트엔드**: Turnstile 캡차 완료 → `turnstile_token` 쿠키 저장
2. **프론트엔드**: `/oauth2/authorization/{provider}` 리다이렉트 (GitHub/Google)
3. **백엔드**: OAuth2 인증 후 Turnstile 토큰 검증
4. **백엔드**: 검증 성공 시 JWT 토큰 발급 (Access 1시간, Refresh 30일)
5. **프론트엔드**: 토큰 저장 및 API 요청 시 `Authorization: Bearer {token}` 헤더 사용

## 환경 변수 설정

`.env.example`을 복사하여 `.env` 파일 생성:

```bash
cp .env.example .env
```

필수 환경 변수:
- `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET` - Google OAuth
- `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET` - GitHub OAuth
- `JWT_SECRET` - JWT 서명 키 (최소 256비트)
- `CLOUDFLARE_TURNSTILE_SECRET_KEY` - Turnstile 봇 방지

### OAuth2 설정

**GitHub OAuth App:**
1. GitHub Settings → Developer settings → OAuth Apps
2. Homepage URL: `http://localhost:8080`
3. Callback URL: `http://localhost:8080/login/oauth2/code/github`

**Google OAuth Client:**
1. Google Cloud Console → APIs & Services → Credentials
2. Redirect URI: `http://localhost:8080/login/oauth2/code/google`

**Cloudflare Turnstile:**
1. https://dash.cloudflare.com/ 가입
2. Turnstile 섹션에서 새 사이트 생성
3. Site Key (프론트엔드용), Secret Key (백엔드용) 복사

### JWT Secret 생성
```bash
openssl rand -base64 64
```

## 실행 방법

```bash
# 개발 모드
./gradlew bootRun

# 프로덕션 모드
./gradlew bootRun --args='--spring.profiles.active=prod'

# 빌드
./gradlew build

# 테스트
./gradlew test
```

## 데이터베이스 스키마

- **users** - OAuth2 사용자 계정
- **refresh_tokens** - Refresh Token 관리
- **articles** - 메인 게시글 테이블
- **article_tags** - 게시글 태그 (Many-to-Many)
- **llm_models** - LLM 모델 정의
- **llm_benchmark_scores** - 모델별 벤치마크 점수
- **benchmarks** - 벤치마크 참조 데이터

## 주요 기능

- ✅ OAuth2 소셜 로그인 (GitHub, Google)
- ✅ JWT 이중 토큰 시스템 (Access 1시간, Refresh 30일)
- ✅ Cloudflare Turnstile 봇 방지
- ✅ Redis 캐싱 (트렌딩 티커, GitHub 리더보드)
- ✅ 페이지네이션 및 카테고리 필터링
- ✅ LLM 벤치마크 순위 시스템
- ✅ 자동 데이터 초기화 (개발 모드)

## 보안

- JWT 기반 무상태 인증
- Refresh Token 데이터베이스 저장 및 폐기 가능
- Turnstile 캡차로 자동화 로그인 차단
- OAuth2 인증 시 Turnstile 토큰 검증 필수
- CSRF 비활성화 (Stateless API)
- CORS 프론트엔드 도메인만 허용


---

**Version:** 1.0.0
**Spring Boot:** 4.0.0
**Java:** 25
