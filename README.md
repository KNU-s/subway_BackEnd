# 열차영차
실시간 지하철 위치 공유 웹 서비스
- 배포 URL : https://livesubway.site
- 개발 기간 : 24.06 ~ 24.08
# Member
- JShistory (BackEnd, Infra)
- gaeguul (FrontEnd)
# 프로젝트 소개
- '열차영차'는 실시간 지하철 위치를 공유하는 웹/앱 서비스입니다.
- 각 호선별로 나뉜 지하철에 위치를 확인할 수 있습니다.
- 각 열차 상태에 대한 상세한 위치를(출발, 도착, 진입)확인할 수 있습니다.
# ⚙️개발환경
### Frond-end
- React, Node.js
### Back-end
- Java 17, SpringBoot 3.x, WebSocket, Async, MongoDB 
### Infra
- AWS EC2, GitWebHook, Jenkins, Docker, Nginx
# 채택한 개발 기술
### SpringBoot
- Async
  - 실시간 데이터를 3초마다 가져와야 하는 요구사항을 만족시키기 위해 비동기 처리 방식을 채택했습니다. 비동기 처리를 통해 서버는 여러 작업을 동시에 처리할 수 있어, 각 작업이 블로킹되지 않고 빠르게 실행됩니다. 이로 인해 시스템의 응답성이 향상되었고, 자원을 효율적으로 활용하여 서버 성능을 극대화할 수 있었습니다
- WebSocket
  - 프론트엔드와의 실시간 데이터 통신을 위해 웹소켓을 활용했습니다. 웹소켓은 HTTP 기반의 전통적인 요청/응답 패턴보다 효율적인 양방향 통신을 가능하게 하며, 네트워크 비용을 줄이고 지연 시간을 최소화하는 장점이 있습니다. 이를 통해 사용자에게 실시간으로 데이터를 제공할 수 있었으며, 보다 즉각적인 반응성을 구현할 수 있었습니다.

# 내가 맡은 역할
- BackEnd : 실시간 데이터 보장, 대용량 데이터 처리, 비동기 처리 트러블 슈팅, 웹소켓 환경 구축, 메타데이터 생성 및 활용
- Infra : FrontEnd/BackEnd/DB 서버 구축, GitWebHook/Jenkins/Docker를 활용한 자동 배포 환경 구축
# 파일 구조
```bash
subway
    ├── SubwayApplication.java
    ├── aop (로그 AOP 패키지)
    │   ├── LoggingAop.java
    │   └── TimeExecuteAop.java
    ├── config
    │   ├── AsyncConfig.java
    │   └── WebConfig.java
    ├── controller
    │   ├── StationInfoApiController.java
    │   └── SubwayApiController.java
    ├── entity
    │   ├── StationInfo.java
    │   ├── Subway.java
    │   ├── UserVisitLog.java
    │   ├── dto
    │   │   └── SubwayDTO.java
    │   └── subwayEnum
    │       ├── SubwayLine.java
    │       └── TrainStatus.java
    ├── repository
    │   ├── StationInfoRepository.java
    │   ├── SubwayRepository.java
    │   └── UserVisitLogRepository.java
    ├── service
    │   ├── ApiService.java (API 요청 및 JSON 데이터 처리)
    │   ├── StationInfoService.java
    │   ├── SubwayAsyncService.java (비동기 처리 클래스)
    │   ├── SubwayDataCollector.java (Cron, Schedule을 활용한 데이터 수집 클래스)
    │   ├── SubwayService.java
    │   └── UserVisitService.java
    └── webSocket (웹소켓 관련 패키지)
        ├── CustomHandshakeInterceptor.java
        ├── WebSocketConfig.java
        └── WebSocketHandler.java
```
        


# 페이지 별 기능
### [초기화면]
<img width="608" alt="스크린샷 2024-08-15 오후 5 52 24" src="https://github.com/user-attachments/assets/d59492fb-33ad-4985-88e9-90cb23b48e79">

### [노선 선택 화면]
<img width="627" alt="image" src="https://github.com/user-attachments/assets/6a590d3b-617f-4544-bafc-594bdd20c805">

### [열차 상세 정보 화면]
<img width="559" alt="스크린샷 2024-08-15 오후 5 52 32" src="https://github.com/user-attachments/assets/cfa7482c-5e72-495b-9bcf-7fffc362c25f">

# 서버구성도
![server drawio (1)](https://github.com/user-attachments/assets/1941d6e9-d8e8-45db-a23a-ec6c05ce20b3)






