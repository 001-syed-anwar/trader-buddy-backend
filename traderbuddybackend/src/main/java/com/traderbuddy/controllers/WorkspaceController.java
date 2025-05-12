package com.traderbuddy.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.dto.request.CreateWorkpaceRequest;
import com.traderbuddy.dto.response.CreateWorkpaceResponse;
import com.traderbuddy.dto.response.GetAllWorkspacesResponse;
import com.traderbuddy.services.WorkspaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
	private final WorkspaceService service;
	private final Logger logger = LoggerFactory.getLogger(WorkspaceService.class);

	@PostMapping("/create")
	public ResponseEntity<CreateWorkpaceResponse> createWorkpace(@RequestBody CreateWorkpaceRequest request) {
		return ResponseEntity.ok(service.create(request));
	}

	@GetMapping("/getAll")
	public ResponseEntity<GetAllWorkspacesResponse> getAllWorkpaces() {
		logger.info("here isnide the getALl controller");
		return ResponseEntity.ok(service.getAllWorkspace());
	}
}
