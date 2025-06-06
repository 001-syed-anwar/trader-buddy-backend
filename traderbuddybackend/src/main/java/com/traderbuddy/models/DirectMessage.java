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
@Table(name = "direct_message", indexes = {
		@Index(name = "idx_member_pair", columnList = "memberOne, memberTwo", unique = true),
		@Index(name = "idx_direct_message_workspace_id", columnList = "workspaceId") })
public class DirectMessage {

	@Id
	@GeneratedValue
	private Long id;
	private Long workspaceId, memberOne, memberTwo;
}
