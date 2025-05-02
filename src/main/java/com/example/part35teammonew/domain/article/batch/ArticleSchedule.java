package com.example.part35teammonew.domain.article.batch;

import java.io.File;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@RequiredArgsConstructor
public class ArticleSchedule {

  private final JobLauncher jobLauncher;
  private final Job articleJob;
  private final Job backupJob;
  private final Job S3BatchJob;
  
  @Scheduled(cron = "0 33 * * * *") //매 시 5 분

  public void runArticleJob() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis()) // 중복 방지용
        .toJobParameters();
    jobLauncher.run(articleJob, jobParameters);
  }
  @Scheduled(cron = "0 15 0 * * *") //매 시 15분
  public void runS3Job() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
        .toJobParameters();
    jobLauncher.run(S3BatchJob, jobParameters);
  }
  @Scheduled(cron = "0 5 * * * *") //자정
  public void runBackupJob() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
        .toJobParameters();
    jobLauncher.run(backupJob, jobParameters);
  }

  @Scheduled(cron = "0 3 0 * * *") // 매일 자정 3분 후
  public void cleanOldBackupFiles() {
    File dir = new File(".");
    File[] files = dir.listFiles((d, name) -> name.startsWith("articles_") && name.endsWith(".json"));

    LocalDate today = LocalDate.now();
    for (File file : files) {
      String fileDate = file.getName().substring(9, 19); // "articles_2025-04-24.json" 에서 날짜 추출
      if (!fileDate.equals(today.toString())) {
        if (file.delete()) {
          //System.out.println(" 오래된 백업 삭제: " + file.getName());
        } else {
          //System.err.println(" 삭제 실패: " + file.getAbsolutePath());
        }
      }
    }
  }
}
