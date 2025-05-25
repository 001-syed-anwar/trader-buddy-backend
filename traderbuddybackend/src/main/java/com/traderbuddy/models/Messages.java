package com.traderbuddy.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Messages extends BaseAuditingEntity{
	@Id
	@GeneratedValue
	private Long id;
	private String body;
	private String imagePath;
	private Long memberId;
	private Long channelId;
	private Long workspaceId;
	private Long parentMessageId;
	// TODO DirectMessageId;
}
