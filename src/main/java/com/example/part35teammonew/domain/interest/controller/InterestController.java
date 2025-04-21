package com.example.part35teammonew.domain.interest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.part35teammonew.domain.interest.dto.InterestCreateRequest;
import com.example.part35teammonew.domain.interest.dto.InterestDto;
import com.example.part35teammonew.domain.interest.service.InterestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
@Validated
public class InterestController {

	private final InterestService interestService;

	@PostMapping
	public ResponseEntity<InterestDto> create(@RequestBody @Valid InterestCreateRequest request){
		InterestDto dto = interestService.createInterest(request);
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}

}
