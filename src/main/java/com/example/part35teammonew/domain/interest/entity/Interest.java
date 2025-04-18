package com.example.part35teammonew.domain.interest.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.part35teammonew.domain.ArticleInterest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interest {

	@Id
	@GeneratedValue
	@Column(name = "interest_id", nullable = false)
	private UUID id;

	@Column(name = "name", nullable = false, unique = true, length = 50)
	private String name;

	@Column(name = "keywords", nullable = false, columnDefinition = "TEXT")
	private String keywords; // todo: 나중에 변환 해야 함

	@Column(name = "subscriber_count", nullable = false)
	private Long subscriberCount = 0L;

	@Column(name = "subscribed_me")
	private boolean subscribedMe = false;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at")
	private Instant updatedAt;

	@OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ArticleInterest> articleInterests = new ArrayList<>();

	@PrePersist
	public void prePersist() {
		Instant now = Instant.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = Instant.now();
	}

}
