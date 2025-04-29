package com.example.part35teammonew.domain.article.dto;

import com.example.part35teammonew.domain.article.entity.Direction;
import com.example.part35teammonew.domain.article.entity.SortField;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArticleCursorRequest {
  private String cursor;
  private SortField sortField;
  private int size;
  private Direction direction;

  public ArticleCursorRequest(String cursor, SortField sortField, int limit, Direction direction) {
    if(cursor != null){
      this.cursor = cursor;
    }else if(sortField == SortField.publishDate) {
      this.cursor = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }else{
      this.cursor = "0";
    }
    if(limit == 0){
      throw new IllegalArgumentException("limit is 0");
    }
    this.sortField = sortField;
    this.size = limit;
    this.direction = direction;
  }
}
