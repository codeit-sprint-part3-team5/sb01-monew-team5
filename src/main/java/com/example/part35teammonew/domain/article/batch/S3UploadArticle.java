package com.example.part35teammonew.domain.article.batch;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.part35teammonew.exception.RestApiException;
import com.example.part35teammonew.exception.errorcode.ArticleErrorCode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3UploadArticle {

  private final AmazonS3 amazonS3Client;

  @Value("${cloud.aws.s3.bucket}")
  private String bucketName;

  public void upload(File file, String filename) {
    try (FileInputStream inputStream = new FileInputStream(file)) {
      ObjectMetadata metadata = new ObjectMetadata();
      metadata.setContentLength(file.length());
      metadata.setContentType("application/json");

      PutObjectRequest request = new PutObjectRequest(bucketName, filename, inputStream, metadata);
      amazonS3Client.putObject(request);
    } catch (Exception e) {
      log.error("S3에 파일을 업로드 과정 중 실패했습니다.", e);
      throw new IllegalArgumentException("Failed to upload file to S3", e);
    }
  }

  public void delete(String filename) {
    try {
      amazonS3Client.deleteObject(bucketName, filename);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }
  public boolean exists(String fileName){
    return amazonS3Client.doesObjectExist(bucketName, fileName);
  }

  public void download(File file) {
    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      String today = LocalDate.now().toString();
      amazonS3Client.getObject(bucketName, "articles_" + today + ".json")
          .getObjectContent()
          .transferTo(outputStream);
    } catch (Exception e) {
      throw new IllegalArgumentException("S3에서 파일 다운로드 실패: " + file.getName(), e);
    }
  }

  public void removeArticleFromS3Json(String titleToDelete) {
    try {
      String today = LocalDate.now().toString();
      File file = new File("articles_" + today + ".json");

      // 1. 다운로드
      if (exists(file.getName())) {
        download(file);
      } else {
        return; // 삭제할 것도 없음
      }

      // 2. 파일 읽기
      String content = Files.readString(file.toPath());
      JSONArray jsonArray = new JSONArray(content);

      // 3. 삭제 대상 필터링
      JSONArray updatedArray = new JSONArray();
      for (int i = 0; i < jsonArray.length(); i++) {
        JSONObject obj = jsonArray.getJSONObject(i);
        String title = obj.getString("title");

        if (!title.equals(titleToDelete)) {
          updatedArray.put(obj);
        }
      }

      // 4. 덮어쓰기
      try (FileWriter writer = new FileWriter(file)) {
        writer.write(updatedArray.toString(2));
      }

      // 5. 다시 업로드
      upload(file, file.getName());

    } catch (Exception e) {
      log.error("S3 JSON 삭제 중 오류 발생", e);
      throw new RestApiException(ArticleErrorCode.S3_FAIL_TO_UPLOAD, "S3 JSON 삭제 중 오류 발생");
    }
  }
}