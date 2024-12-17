# MSA-LogisticsHub
MSA 기반 물류 관리 및 배송 시스템 플랫폼
<br><br>

## 👨‍👩‍👧‍👦 Our Team

|양혜지|문병민|최영근|오겸비|
|:---:|:---:|:---:|:---:|
|[@laira2](https://github.com/laira2)|[@qudalsrnt3x](https://github.com/qudalsrnt3x)|[@ykchoi1203](https://github.com/ykchoi1203)|[@afoisl](https://github.com/afoisl)|
|업체,상품|주문,배송|허브,슬랙,AI|유저,배송담당자|


<br><br>

## 📝 Technologies & Tools
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/> <img src="https://img.shields.io/badge/SpringSecurity-6DB33F?style=for-the-badge&logo=SpringSecurity&logoColor=white"/>

<img src="https://img.shields.io/badge/postgres-%234169E1.svg?style=for-the-badge&logo=postgresql&logoColor=white"> <img src="https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white"/> <img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white"/>

<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/> <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"/> <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"/> 

<img src="https://img.shields.io/badge/IntelliJIDEA-000000?style=for-the-badge&logo=IntelliJIDEA&logoColor=white"/> <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=Notion&logoColor=white"/> <img src="https://img.shields.io/badge/Slack-4A154B?style=for-the-badge&logo=slack&logoColor=white"/> <img src="https://img.shields.io/badge/google%20gemini-8E75B2?style=for-the-badge&logo=google%20gemini&logoColor=white"/>

<br><br>

## 📑 서비스 구성
### 🙋🏻‍ 회원 (CRUD)
- Auth APIs

> * POST /api/auth/signup - 회원 가입
> * POST /api/auth/login - 로그인

- User APIs
> * GET /api/users/user/{userId} - 내 정보 조회
> * GET /api/users - 유저 전체 조회
> * GET /api/users/{userId} - 유저 상세 조회 
> * PATCH /api/users/{userId} - 유저 권한 수정
> * DELETE /api/user/{userId} - 유저 탈퇴

- Hub Manager APIs
> * POST /api/hub-managers - 허브 매니저 생성
- Delivery Manager APIs
> * POST /api/delivery-managers - 배송 담당자 생성
> * GET /api/delivery-managers - 배송 담당자 전체 조회
> * GET /api/delivery-managers/{deliveryManagerId} - 배송 담당자 상세 조회
> * PATCH /api/delivery-managers/{deliveryManagerId} - 배송 담당자 수정
> * DELETE /api/delivery-managers/{deliveryManagerId} - 배송 담당자 삭제
> * GET /api/delivery-managers/available-manager - 배정 가능한 허브 배송 담당자 조회

### 🏭 허브
- Hub APIs
> * POST /api/hubs - 허브 생성
> * PUT /api/hubs/{id} - 허브 수정
> * DELETE /api/hubs/{id} - 허브 삭제
> * GET /api/hubs/{id} - 허브 상세 조회
> * GET /api/hubs - 허브 검색
> * GET /api/hubs/company-address - 업체 주소 기반 Hub 조회
> * GET /api/hubs/list - 허브 전체 조회

- Hub Transfer APIs
> * POST /api/hub-transfers - Hub Transfer 생성
> * PUT /api/hub-transfers/{id} - Hub Transfer 수정
> * DELETE /api/hub-transfers/{id} - Hub Transfer 삭제
> * GET /api/hub-transfers/{id} - Hub Transfer 상세 조회
> * GET /api/hub-transfers - Hub Transfer 검색
> * GET /api/hub-transfers/hub-to-hub - 허브 간 Hub Transfer 조회

- Area APIs
> * POST /api/areas - Area 생성
> * PUT /api/areas/{id} - Area 수정
> * DELETE /api/areas/{id} - Area 삭제
> * GET /api/areas/{id} - Area 상세 조회
> * GET /api/areas - Area 검색

### 🏢 업체
- Company APIs

> * POST /api/companies - 업체 생성
> * PUT /api/companies/{id} - 업체 수정
> * DELETE /api/companies/{id} - 업체 삭제
> * GET /api/companies - 업체 전체 조회
> * GET /api/companies/{id} - 업체 상세 조회
> * GET /api/companies/search-by-name - 업체 이름으로 업체 조회

### 🧾 주문
- Order APIs
> * POST /api/orders - 주문 생성
> * GET /api/orders - 주문 전체 조회
> * GET /api/orders/{orderId} - 주문 상세 조회
> * PUT /api/orders/{orderId} - 주문 수정
> * PATCH /api/orders/{orderId}/status - 주문 상태 변경
> * DELETE /api/orders/{orderId} - 주문 삭제

### 🚛 배달
- Delivery APIs

> * GET /api/deliveries/{deliveryId}/order-status - 주문 상태 변경 가능 여부 확인
> * GET /api/deliveries - 배송 전체 조회
> * GET /api/deliveries/{deliveryId} - 배송 상세 조회
> * PATCH /api/deliveries/{deliveryId}/status - 배송 상태 수정
> * DELETE /api/deliveries/{deliveryId} - 배송 삭제
- Delivery Route APIs

> * PATCH /api/delivery-routes/{routeId}/status - 배송 이동 경로 상태 수정







<br>


## 📜 실행방법
1. 먼저 각 모듈마다 gradle 에서 Tasks -> build -> bootJar 순서대로 한번씩 실행한다. <br>
2. Prod 환경으로 설정한다. <br>
3. docker-compose 를 실행한다.
```shell
docker-compose up -d
```


<br><br>

## 🚗 프로젝트 기능


<br><br>

## 🚨 Trouble Shooting
#### 허브 배송 담당자 배정 최적화 [WIKI보기](https://github.com/i-four-people/MSA-LogisticsHub/wiki/%5BTrouble-Shooting%5D-%ED%97%88%EB%B8%8C-%EB%B0%B0%EC%86%A1-%EB%8B%B4%EB%8B%B9%EC%9E%90-%EB%B0%B0%EC%A0%95-%EC%B5%9C%EC%A0%81%ED%99%94)
#### Gateway 포트로 접근 시 403 에러 [WIKI보기](https://github.com/i-four-people/MSA-LogisticsHub/wiki/%5BTrouble-Shooting%5D-Gateway-%ED%8F%AC%ED%8A%B8%EB%A1%9C-%EC%A0%91%EA%B7%BC-%EC%8B%9C-403-%EC%97%90%EB%9F%AC)

<br><br>

## 🌐 Architecture
<img width="373" alt="스크린샷 2024-12-18 00 09 04" src="https://github.com/user-attachments/assets/f0582b2a-9942-4374-be75-e9372a420bab" />


<br><br>

## 📋 ERD Diagram
![물류 관리 및 배송 시스템](https://github.com/user-attachments/assets/dfa69175-c486-44f4-9b21-267ab083872d)
