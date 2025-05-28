package com.traderbuddy.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@SuperBuilder
@EqualsAndHashCode(callSuper=false)
@Table(name = "channel", indexes = { @Index(name = "idx_member_workspace_id", columnList = "workspaceId") })
public class Channel extends BaseAuditingEntity{
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private Long workspaceId;
}
