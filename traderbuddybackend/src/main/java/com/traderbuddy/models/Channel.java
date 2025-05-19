package com.traderbuddy.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
@Table(name = "channel", indexes = { @Index(name = "idx_member_workspace_id", columnList = "workspaceId") })
public class Channel {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private Long workspaceId;
}
