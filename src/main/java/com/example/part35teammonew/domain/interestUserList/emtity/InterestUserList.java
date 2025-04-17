package com.example.part35teammonew.domain.interestUserList.emtity;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "interestUserList")
@Getter
public class InterestUserList {

  @Id
  private ObjectId id;
  private UUID interest;
  private Set<UUID> chooseUser;
  private BigInteger count;

  @Builder
  private InterestUserList(UUID interest) {
    this.interest = interest;
    this.chooseUser = new HashSet<>();
    this.count = new BigInteger("0");
  }

  public static InterestUserList setUpNewInterestUserList(UUID interest) {
    return InterestUserList.builder()
        .interest(interest)
        .build();
  }

  public void addUser(UUID readerId) {
    if (!chooseUser.contains(readerId)) {
      this.count = count.add(new BigInteger("1"));
      chooseUser.add(readerId);
    }
  }

  public void subtractUser(UUID readerId) {
    if (chooseUser.contains(readerId)) {
      this.count = count.subtract(new BigInteger("1"));
      chooseUser.remove(readerId);
    }
  }

  public boolean findUser(UUID readerId) {
    return chooseUser.contains(readerId);
  }
}
