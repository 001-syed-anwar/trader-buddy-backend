package com.traderbuddy.models;

import com.traderbuddy.auth.user.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(	name = "member",
		indexes = { 
		@Index(name = "idx_member_user_id", columnList = "userId"),
		@Index(name = "idx_member_workspace_id", columnList = "workspaceId") 
		})
public class Member {
	@Id
	@GeneratedValue
	private Long id;
	// foreign key user id
	private Long userId;
	// foreign key user id
	private Long workspaceId;
	@Enumerated(EnumType.STRING)
	private Role role;
	private String firstname;
	private String lastname;
	private String profileImg;
}
