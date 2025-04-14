📦 custhumb-server
AI 기반 자동 썸네일 생성 서비스의 백엔드 서버입니다.
Spring Boot 기반 REST API이며, Google Cloud Platform(GCP)의 Cloud Run과 Cloud Build를 이용해 서버리스 배포됩니다.

🌐 주요 기술 스택
Backend: Spring Boot, Gradle

Infra: Google Cloud Run (Serverless), Artifact Registry, Cloud Build

CI/CD: cloudbuild.yaml을 활용한 자동화 빌드 & 배포

Secrets: Secret Manager를 통해 환경설정 분리 (local / remote)
