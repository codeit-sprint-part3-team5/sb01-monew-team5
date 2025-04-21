package com.example.part35teammonew.domain.article.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ArticleBatchTestController {
  private final JobLauncher jobLauncher;
  private final JobRegistry jobRegistry;

  //API 기반 fistjob 실행
  @GetMapping("/first")
  public String fisrtApi(@RequestParam("value") String value) throws Exception{
    JobParameters jobParameters = new JobParametersBuilder().addString("date", value).toJobParameters();
    jobLauncher.run(jobRegistry.getJob("artlceJob"), jobParameters);
    return "ok";
  }
}
