package com.example.part35teammonew.domain.interest.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.part35teammonew.domain.interest.dto.request.InterestCreateRequest;
import com.example.part35teammonew.domain.interest.dto.InterestDto;
import com.example.part35teammonew.domain.interest.dto.request.InterestUpdateRequest;
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
	public ResponseEntity<InterestDto> create(@RequestBody @Valid InterestCreateRequest request) {
		InterestDto dto = interestService.createInterest(request);
		return new ResponseEntity<>(dto, HttpStatus.CREATED);
	}

	@PatchMapping("{interestId}")
	public ResponseEntity<InterestDto> updateKeywords(@PathVariable UUID interestId, @RequestBody @Valid InterestUpdateRequest request) {
		InterestDto dto = interestService.updateKeywords(interestId, request.getKeywords());
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}

	@DeleteMapping("{interestId}")
	public ResponseEntity<InterestDto> delete(@PathVariable UUID interestId) {
		interestService.deleteInterest(interestId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
