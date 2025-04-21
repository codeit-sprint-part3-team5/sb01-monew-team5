package com.example.part35teammonew.domain.article.batch;

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

  @Scheduled(cron = "0 0 * * * *") //매 분
  public void runJob() throws Exception {
    JobParameters jobParameters = new JobParametersBuilder().addLong("time",
            System.currentTimeMillis()) // 중복 방지용
        .toJobParameters();
    jobLauncher.run(articleJob, jobParameters);
  }

}
