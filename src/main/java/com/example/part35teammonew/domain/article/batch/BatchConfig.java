package com.example.part35teammonew.domain.article.batch;

import com.example.part35teammonew.domain.article.api.NewsSearch;
import com.example.part35teammonew.domain.article.dto.ArticleBaseDto;
import com.example.part35teammonew.domain.article.entity.Article;
import com.example.part35teammonew.domain.article.repository.ArticleRepository;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager platformTransactionManager;
  private final ArticleRepository articleRepository;
  private final NewsSearch newsSearch;

  @Bean
  public Job articleJob() {
    //JobBuilder("실행할 Job 이름", 작업 트래킹 > jobRepository)
    return new JobBuilder("articleJob", jobRepository).start(articleStep()).build();
  }

  @Bean
  public Step articleStep() {
    System.out.println("articleStep");
    //10개씩 끊어서 처리
    return new StepBuilder("articleStep", jobRepository).<Article, Article>chunk(10,
            platformTransactionManager)
        .reader(articleReader())
        .processor(articleProcessor())
        .writer(articleWriter())
        .build();
  }

  @Bean
  @StepScope
  public ItemReader<Article> articleReader() {
    return new ItemReader<>() {
      private Queue<Article> queue = new LinkedList<>();
      private int page = 0;

      @Override
      public Article read() throws Exception {
        if (!queue.isEmpty()) {
          return queue.poll();
        }

        // 다 꺼냈다면 다음 뉴스 API 호출
        if (page >= 10) { // 최대 100개까지만 읽기 (10페이지 x 10개)
          return null;
        }

        page++;
        //키워드 파라미터로 받도록 수정 필요
        String json = newsSearch.getNews("금융", 10, (page - 1) * 10 + 1, "date");
        JSONObject jsonObject = new JSONObject(json);
        JSONArray items = jsonObject.getJSONArray("items");

        for (int i = 0; i < items.length(); i++) {
          JSONObject item = items.getJSONObject(i);
          String title = item.getString("title").replaceAll("<.*?>", "");
          String summary = item.getString("description").replaceAll("<.*?>", "");
          String link = item.getString("link");
          //item이라는 JSON 객체에서 "originallink"라는 키가 존재하면 → 그 값을 쓰고, 없으면 "null"라는 기본값을 쓰겠다는 뜻
          String source = item.optString("originallink", "null");
          String pubDate = item.getString("pubDate");
          Article article = new Article(
              new ArticleBaseDto(title, summary, link, source, parsePubDate(pubDate)));
          queue.add(article);
        }

        return queue.poll(); 
      }
    };
  }


  @Bean
  public ItemProcessor<Article, Article> articleProcessor() {
    //중복 검사
    return article ->
        articleRepository.findByTitleAndDate(article.getTitle(), article.getDate()) != null ? null : article;
  }

  @Bean
  public RepositoryItemWriter<Article> articleWriter() {
    //S3 저장 필요
    return new RepositoryItemWriterBuilder<Article>().repository(articleRepository)
        .methodName("save").build();
    //어떤 레포지토리로 어떤 메소드를 날릴 것 인지?
  }

  private LocalDateTime parsePubDate(String pubDate) {
    DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
    return ZonedDateTime.parse(pubDate, formatter).toLocalDateTime();
  }

}
