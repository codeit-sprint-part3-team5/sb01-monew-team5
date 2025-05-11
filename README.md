# **monew_5team**

팀 노션 링크(<a href="https://codeit.notion.site/1ca6fd228e8d8130a23deec0dae79641#1ca6fd228e8d811fbe96f955fe9ee6cb">https://codeit.notion.site/1ca6fd228e8d8130a23deec0dae79641#1ca6fd228e8d811fbe96f955fe9ee6cb</a>)<br>

## **팀원 구성**

박지현 (<a href="https://github.com/jjhparkk">https://github.com/jjhparkk</a>)<br>
강병훈 (<a href="https://github.com/kbh-n">https://github.com/kbh-n</a>)<br>
김승찬 (<a href="https://github.com/tmdcks801">https://github.com/tmdcks801</a>)<br>
양병운 (<a href="https://github.com/Yang-ByeongUn">https://github.com/Yang-ByeongUn</a>)<br>
정연경 (<a href="https://github.com/yeonkyung-jeong">https://github.com/yeonkyung-jeong</a>)

---

## **프로젝트 소개**

- 여러 뉴스 API를 통합하여 사용자에게 맞춤형 뉴스를 제공하고, 의견을 나눌 수 있는 소셜 기능을 갖춘 서비스
- 프로젝트 기간: 2025.04.16 ~ 2025.05.12

---

## **기술 스택**

- Backend: Spring Boot, Spring Security, Spring Data JPA, AWS
- Database: PostgreSQL, MongoDB
- 공통 Tool: Git & Github, Discord, Code with me

---

## **팀원별 구현 기능 상세**

### **박지현**

#### 댓글 도메인
<img src="img/댓글 1.png" alt="댓글 1" width="500"/>

- **댓글**
  - 댓글에 관한 API 엔드포인트 제작
  - 좋아요에 관한 API 엔드포인트 제작

### **강병훈**

#### 관심사 도메인
<img src="img/관심사 1.png" alt="관심사 1" width="300"/>
<img src="img/관심사 2.png" alt="관심사 2" width="300"/>

- **관심사**
  - 관심사를 등록하고 그에 맞는 키워드도 등록하는 API 엔드포인트 제작
  - 관심사에 대한 정보 관리하는 API 엔드포인트 제작

### **김승찬**

#### 유저 활동 및 알림 도메인 및 로깅
<img src="img/유저 활동 1.png" alt="유저 활동 1" width="300"/>
<img src="img/알림 1.png" alt="알림 1" width="300"/>

- **유저 활동**
  - 사용자의 정보 및 최근 활동을 기록하고 있는 정보를 제공하는 API 제작
- **알림**
  - 알림을 조회 및 삭제하는 API 엔드포인트 제작
  - 이벤트를 통해 알림 생성
- **로깅**
  - 로그를 MongoDB에 적재

### **양병운**
<img src="img/기사 1.png" alt="유저 1" width="500"/>

#### 기사 도메인

- **기사**
  - 외부 API를 통해 외부의 기사 중 관심사로 등록된 기사들을 가져옴
  - 기사를 조회하는 API 엔드포인트 제작

### **정연경**

#### 유저 도메인 및 배포 CI/CD
<img src="img/유저1.png" alt="유저 1" width="300"/>


![image](https://github.com/user-attachments/assets/cae09b0c-c60f-4816-aa5c-e81d5eac6327)


![image](https://github.com/user-attachments/assets/c9be5e7f-ed65-490c-be39-c7581458a51c)


![image.png](attachment:f5ec66b0-99dc-43fe-872a-0dfac00b6a88:2e91b849-6d6d-474d-83e2-75dd1f60312c.png)


![image.png](attachment:ffcef3a0-2fd9-4d0b-a0ec-0a023812c00a:70557000-846e-4c20-abb9-e6a07bd3b69d.png)

- **유저**
  - 유저 회원가입 로그인 기능 구현 - 로그인 성공 시, 사용자 ID를 헤더에 포함하여 API 요청하면 서버는 이로 사용자 식별 후, 쿠키 기반 세션을 통해 로그인 상태를 유지하도록 구성
  - 닉네임 수정, 논리/물리 삭제 기능 구현
- **배포**
  - Docker 이미지 빌드 후 AWS에 수동 배포 (RDS, EC2, ECR, ECS Fargate 사용)
  - 이후 Github Actions CI/CD 파이프라인 구축 후 자동 배포 성공 (AWS IAM OIDC 인증 사용)
---

## **파일 구조**

```

src:.
+---main
|   +---java
|   |   \---com
|   |       \---example
|   |           \---part35teammonew
|   |               |   Part35teamMonewApplication.java
|   |               |
|   |               +---config
|   |               |       CorsConfig.java
|   |               |       SecurityConfig.java
|   |               |       SwaggerConfig.java
|   |               |
|   |               +---domain
|   |               |   |   ArticleInterest.java
|   |               |   |
|   |               |   +---article
|   |               |   |   +---api
|   |               |   |   |       NewsSearch.java
|   |               |   |   |
|   |               |   |   +---batch
|   |               |   |   |       ArticleSchedule.java
|   |               |   |   |       BackupBatchConfig.java
|   |               |   |   |       BatchConfig.java
|   |               |   |   |       S3BatchConfig.java
|   |               |   |   |       S3Config.java
|   |               |   |   |       S3UploadArticle.java
|   |               |   |   |       SharedArticleReader.java
|   |               |   |   |
|   |               |   |   +---controller
|   |               |   |   |   |   ArticleController.java
|   |               |   |   |   |
|   |               |   |   |   \---docs
|   |               |   |   |           ArticleApi.java
|   |               |   |   |
|   |               |   |   +---dto
|   |               |   |   |       ArticleBaseDto.java
|   |               |   |   |       ArticleCursorRequest.java
|   |               |   |   |       ArticleEnrollmentResponse.java
|   |               |   |   |       ArticleRestoreRequestDto.java
|   |               |   |   |       ArticleSourceAndDateAndInterestsRequest.java
|   |               |   |   |       ArticlesRequestDto.java
|   |               |   |   |       ArticlesResponse.java
|   |               |   |   |       findByCursorPagingResponse.java
|   |               |   |   |
|   |               |   |   +---entity
|   |               |   |   |       Article.java
|   |               |   |   |       Direction.java
|   |               |   |   |       SortField.java
|   |               |   |   |
|   |               |   |   +---repository
|   |               |   |   |       ArticleRepository.java
|   |               |   |   |
|   |               |   |   \---service
|   |               |   |           ArticleService.java
|   |               |   |           ArticleServiceImpl.java
|   |               |   |
|   |               |   +---articleView
|   |               |   |   +---Dto
|   |               |   |   |       ArticleViewDto.java
|   |               |   |   |
|   |               |   |   +---entity
|   |               |   |   |       ArticleView.java
|   |               |   |   |
|   |               |   |   +---mapper
|   |               |   |   |       ArticleViewMapper.java
|   |               |   |   |
|   |               |   |   +---repository
|   |               |   |   |       ArticleViewRepository.java
|   |               |   |   |
|   |               |   |   \---service
|   |               |   |           ArticleViewServiceImp.java
|   |               |   |           ArticleViewServiceInterface.java
|   |               |   |
|   |               |   +---comment
|   |               |   |   +---controller
|   |               |   |   |   |   CommentController.java
|   |               |   |   |   |   CommentLikeController.java
|   |               |   |   |   |
|   |               |   |   |   \---docs
|   |               |   |   |           CommentApi.java
|   |               |   |   |           CommentLikeApi.java
|   |               |   |   |
|   |               |   |   +---dto
|   |               |   |   |       CommentCreateRequest.java
|   |               |   |   |       CommentDto.java
|   |               |   |   |       CommentLikeResponse.java
|   |               |   |   |       CommentPageResponse.java
|   |               |   |   |       CommentUpdateRequest.java
|   |               |   |   |
|   |               |   |   +---entity
|   |               |   |   |       Comment.java
|   |               |   |   |       CommentLike.java
|   |               |   |   |
|   |               |   |   +---mapper
|   |               |   |   |       CommentMapper.java
|   |               |   |   |
|   |               |   |   +---repository
|   |               |   |   |       CommentLikeRepository.java
|   |               |   |   |       CommentRepository.java
|   |               |   |   |
|   |               |   |   \---service
|   |               |   |           CommentLikeService.java
|   |               |   |           CommentLikeServiceImpl.java
|   |               |   |           CommentService.java
|   |               |   |           CommentServiceImpl.java
|   |               |   |
|   |               |   +---interest
|   |               |   |   |   InterestRepository.java
|   |               |   |   |
|   |               |   |   +---controller
|   |               |   |   |   |   InterestController.java
|   |               |   |   |   |
|   |               |   |   |   \---docs
|   |               |   |   |           InterestApi.java
|   |               |   |   |
|   |               |   |   +---dto
|   |               |   |   |   +---request
|   |               |   |   |   |       InterestCreateRequest.java
|   |               |   |   |   |       InterestPageRequest.java
|   |               |   |   |   |       InterestUpdateRequest.java
|   |               |   |   |   |
|   |               |   |   |   \---response
|   |               |   |   |           InterestDto.java
|   |               |   |   |           PageResponse.java
|   |               |   |   |
|   |               |   |   +---entity
|   |               |   |   |       Interest.java
|   |               |   |   |
|   |               |   |   +---Enum
|   |               |   |   |       SortBy.java
|   |               |   |   |
|   |               |   |   \---service
|   |               |   |           InterestService.java
|   |               |   |           InterestServiceImpl.java
|   |               |   |
|   |               |   +---interestUserList
|   |               |   |   +---Dto
|   |               |   |   |       InterestUserListDto.java
|   |               |   |   |
|   |               |   |   +---entity
|   |               |   |   |       InterestUserList.java
|   |               |   |   |
|   |               |   |   +---mapper
|   |               |   |   |       InterestUserListMapper.java
|   |               |   |   |
|   |               |   |   +---repository
|   |               |   |   |       InterestUserListRepository.java
|   |               |   |   |
|   |               |   |   \---service
|   |               |   |           InterestUserListServiceImp.java
|   |               |   |           InterestUserListServiceInterface.java
|   |               |   |
|   |               |   +---notification
|   |               |   |   +---controller
|   |               |   |   |   |   NotificationController.java
|   |               |   |   |   |
|   |               |   |   |   \---docs
|   |               |   |   |           NotificationApi.java
|   |               |   |   |
|   |               |   |   +---Dto
|   |               |   |   |       CursorPageRequest.java
|   |               |   |   |       CursorPageResponse.java
|   |               |   |   |       NotificationDto.java
|   |               |   |   |
|   |               |   |   +---entity
|   |               |   |   |       Notification.java
|   |               |   |   |
|   |               |   |   +---Enum
|   |               |   |   |       NotificationType.java
|   |               |   |   |
|   |               |   |   +---repository
|   |               |   |   |       NotificationRepository.java
|   |               |   |   |
|   |               |   |   \---service
|   |               |   |           NotificationSchedulerService.java
|   |               |   |           NotificationServiceImpl.java
|   |               |   |           NotificationServiceInterface.java
|   |               |   |
|   |               |   +---user
|   |               |   |   +---controller
|   |               |   |   |       UserController.java
|   |               |   |   |
|   |               |   |   +---dto
|   |               |   |   |       TokenDto.java
|   |               |   |   |       UserDto.java
|   |               |   |   |       UserLoginRequest.java
|   |               |   |   |       UserRegisterRequest.java
|   |               |   |   |       UserUpdateRequest.java
|   |               |   |   |
|   |               |   |   +---entity
|   |               |   |   |       User.java
|   |               |   |   |
|   |               |   |   +---repository
|   |               |   |   |       UserRepository.java
|   |               |   |   |
|   |               |   |   \---service
|   |               |   |       |   UserService.java
|   |               |   |       |
|   |               |   |       \---impl
|   |               |   |               CustomUserDetails.java
|   |               |   |               CustomUserDetailsService.java
|   |               |   |               UserServiceImpl.java
|   |               |   |
|   |               |   \---userActivity
|   |               |       +---controller
|   |               |       |   |   UserActivityController.java
|   |               |       |   |
|   |               |       |   \---docs
|   |               |       |           UserActivityApi.java
|   |               |       |
|   |               |       +---Dto
|   |               |       |       ArticleInfoView.java
|   |               |       |       InterestView.java
|   |               |       |       LikeCommentView.java
|   |               |       |       RecentCommentView.java
|   |               |       |       UserActivityDto.java
|   |               |       |       UserInfoDto.java
|   |               |       |
|   |               |       +---entity
|   |               |       |       UserActivity.java
|   |               |       |
|   |               |       +---maper
|   |               |       |       ArticleInfoViewMapper.java
|   |               |       |       InterestViewMapper.java
|   |               |       |       LikeCommentMapper.java
|   |               |       |       RecentCommentMapper.java
|   |               |       |       UserActivityMapper.java
|   |               |       |
|   |               |       +---repository
|   |               |       |       UserActivityRepository.java
|   |               |       |
|   |               |       \---service
|   |               |               UserActivityServiceImpl.java
|   |               |               UserActivityServiceInterface.java
|   |               |
|   |               +---exeception
|   |               |   |   ErrorCode.java
|   |               |   |   ErrorResponse.java
|   |               |   |   GlobalExceptionHandler.java
|   |               |   |   RestApiException.java
|   |               |   |
|   |               |   +---comment
|   |               |   |       CommentDeleteUnauthorized.java
|   |               |   |       CommentLikeConflict.java
|   |               |   |       CommentLikeNotFound.java
|   |               |   |       CommentNotFound.java
|   |               |   |       CommentUpdateUnauthorized.java
|   |               |   |
|   |               |   +---errorcode
|   |               |   |       ArticleErrorCode.java
|   |               |   |       ArticleViewErrorCode.java
|   |               |   |       CommentErrorCode.java
|   |               |   |       GlobalErrorCode.java
|   |               |   |       InterestErrorCode.java
|   |               |   |       InterestUserListErrorCode.java
|   |               |   |       NotificationErrorCode.java
|   |               |   |       UserActivityErrorCode.java
|   |               |   |
|   |               |   +---notification
|   |               |   |       WrongUserNotification.java
|   |               |   |
|   |               |   \---userActivity
|   |               |           UserActivityErrorUpdate.java
|   |               |           UserActivityNotFoundException.java
|   |               |
|   |               +---health
|   |               |       HealthCheckController.java
|   |               |
|   |               +---log
|   |               |       MongoDBAppender.java
|   |               |
|   |               \---util
|   |                       SecurityUtil.java
|   |
|   \---resources
|       |
|       \---static
|           |   favicon.ico
|           |   index.html
|           |
|           \---assets
|                   index-D30UMZL2.css
|                   index-DF13B-h9.js
|
\---test
   


```

---

## **배포 다이어그램**


---

## **구현 홈페이지**

http://3.38.205.137:8080/

---

## **프로젝트 회고록**

(제작한 발표자료 링크 혹은 첨부파일 첨부)
