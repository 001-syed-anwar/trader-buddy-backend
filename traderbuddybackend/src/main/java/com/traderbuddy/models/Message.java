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
@Table(name = "message", indexes = {
	    @Index(name = "idx_createdAt_channel_id", columnList = "createdAt, channelId"),
	    @Index(name = "idx_createdAt_direct_message_id", columnList = "createdAt, directMessageId"),
	    @Index(name = "idx_createdAt_channel_id_parent_message_id", columnList = "createdAt, channelId, parentMessageId"),
	    @Index(name = "idx_createdAt_direct_message_id_parent_message_id", columnList = "createdAt, directMessageId, parentMessageId"),
	    @Index(name = "idx_message_parent_created", columnList = "parentMessageId, createdAt"),
	    @Index(name = "idx_message_parent", columnList = "parentMessageId"),
	    @Index(name = "idx_message_dm", columnList = "directMessageId"),
	    @Index(name = "idx_message_channel", columnList = "channelId")
	})
	public class Message extends BaseAuditingEntity {

	    @Id
	    @GeneratedValue
	    private Long id;

	    private String body;
	    private String imagePath;

	    private Long memberId;
	    private Long channelId;
	    private Long workspaceId;
	    private Long parentMessageId;
	    private Long directMessageId;
	// TODO add messageState = "seen", "sent"
}
