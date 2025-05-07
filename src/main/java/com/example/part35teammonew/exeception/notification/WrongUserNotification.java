package com.example.part35teammonew.exeception.notification;

public class WrongUserNotification  extends RuntimeException{
  public WrongUserNotification(String message) { //댓글을 찾을 수 없을 때
    super(message);
  }
}
