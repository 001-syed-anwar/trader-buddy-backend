package com.traderbuddy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.traderbuddy.auth.dto.request.JoinWorkpaceRequest;
import com.traderbuddy.dto.request.CreateWorkpaceRequest;
import com.traderbuddy.dto.request.GetWorkspaceRequest;
import com.traderbuddy.dto.request.UpdateWorkspaceRequest;
import com.traderbuddy.dto.response.CreateWorkpaceResponse;
import com.traderbuddy.dto.response.GetAllWorkspacesResponse;
import com.traderbuddy.dto.response.GetWorkspaceResponse;
import com.traderbuddy.dto.response.UpdateWorkspaceResponse;
import com.traderbuddy.services.WorkspaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/workspace")
@RequiredArgsConstructor
public class WorkspaceController {
	private final WorkspaceService service;

	@PostMapping("/create")
	public ResponseEntity<CreateWorkpaceResponse> createWorkpace(@RequestBody CreateWorkpaceRequest request,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		return ResponseEntity.ok(service.create(request, authToken));
	}

	@PostMapping("/join")
	public ResponseEntity<Void> addPeople(@RequestBody JoinWorkpaceRequest request,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		service.addPeople(request.getId(), request.getJoinCode(), authToken);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/getAll")
	public ResponseEntity<GetAllWorkspacesResponse> getAllWorkpaces(
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		return ResponseEntity.ok(service.getAllWorkspace(authToken));
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<GetWorkspaceResponse> getWorkpace(@PathVariable Long id,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		return ResponseEntity.ok(service.getWorkspace(GetWorkspaceRequest.builder().id(id).build(), authToken));
	}

	@PutMapping("/regenerate/{id}")
	public ResponseEntity<Void> updateJoinCode(@PathVariable Long id,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			service.regenerateJoinCode(id, authToken);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<UpdateWorkspaceResponse> updateWorkspace(@PathVariable Long id,
			@RequestBody UpdateWorkspaceRequest request,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			return ResponseEntity.ok(service.update(id, request, authToken));
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<Void> deleteWorkspace(@PathVariable Long id,
			@CookieValue(name = "access_token", defaultValue = "") String authToken) {
		try {
			service.delete(id, authToken);
			return ResponseEntity.ok().build();
		} catch (IllegalAccessException e) {
			return ResponseEntity.badRequest().build();
		}
	}
}
