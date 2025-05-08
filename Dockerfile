# 1. 경량 OpenJDK 베이스 이미지
FROM openjdk:17-jdk-slim

# 2. JAR 파일을 컨테이너에 복사
COPY build/libs/part3-5team-monew-0.0.1-SNAPSHOT.jar app.jar

# 3. 앱 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 4. 포트
EXPOSE 8080
