package com.traderbuddy.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.traderbuddy.auth.user.UserRepository;
import com.traderbuddy.dto.request.CreateWorkpaceRequest;
import com.traderbuddy.dto.response.CreateWorkpaceResponse;
import com.traderbuddy.dto.response.GetAllWorkspacesResponse;
import com.traderbuddy.models.Workspace;
import com.traderbuddy.repositories.WorkspaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
	private final WorkspaceRepository workspaceRepository;
	private final UserRepository userRepository;
	private final Logger logger=LoggerFactory.getLogger(WorkspaceService.class);
	public CreateWorkpaceResponse create(CreateWorkpaceRequest request) {
		logger.info("here inside the getAll service");
		Workspace workpace = Workspace.builder().name(request.getName())
				.userId(userRepository.findByEmail(request.getEmail())
						.orElseThrow(() -> new UsernameNotFoundException("user doesn't exist in db")).getId())
				.build();
		Workspace saved = workspaceRepository.save(workpace);
		logger.info("saved workspace {}",saved);
		CreateWorkpaceResponse response = CreateWorkpaceResponse.builder().id(saved.getId()).build();
		return response;
	}

	public GetAllWorkspacesResponse getAllWorkspace() {
		
		List<Workspace> all = workspaceRepository.findAll();
		GetAllWorkspacesResponse response=GetAllWorkspacesResponse.builder().workspaces(all).build();
		return response;
	}

}
