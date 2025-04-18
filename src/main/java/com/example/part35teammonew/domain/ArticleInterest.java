package com.example.part35teammonew.domain;

import java.util.UUID;

import com.example.part35teammonew.domain.interest.entity.Interest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "article_interests")
public class ArticleInterest {

	@Id
	@GeneratedValue
	@Column(name = "article_interest_id")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "interest_id", nullable = false)
	private Interest interest;

}
