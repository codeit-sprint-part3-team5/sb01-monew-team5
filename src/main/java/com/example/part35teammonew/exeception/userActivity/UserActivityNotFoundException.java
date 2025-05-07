package com.example.part35teammonew.exeception.userActivity;

public class UserActivityNotFoundException extends RuntimeException{
  public UserActivityNotFoundException(String message) { //댓글을 찾을 수 없을 때
    super(message);
  }
}
