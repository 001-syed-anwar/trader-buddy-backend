package com.traderbuddy.dto.response;

import java.util.List;

import com.traderbuddy.models.Workspace;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetAllWorkspacesResponse {
	private List<Workspace> workspaces; 
}
