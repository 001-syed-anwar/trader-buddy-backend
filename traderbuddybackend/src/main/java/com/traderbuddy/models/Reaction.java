package com.traderbuddy.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
@Table(name = "reaction", indexes = { @Index(name = "idx_reaction_message_id", columnList = "messageId"),
		@Index(name = "idx_message_member_pair", columnList = "messageId, memberId", unique = true),})
public class Reaction {
	@Id
	@GeneratedValue
	private Long id;
	private Long workspaceId, messageId, memberId;
	private String value;
}
