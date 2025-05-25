package com.traderbuddy.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Entity
public class Message extends BaseAuditingEntity{
//	@Id
//	@SequenceGenerator(name="msg_seq", sequenceName = "msg_seq", allocationSize = 1)
//	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "msg_seq")
//	private Long id;
//	
//	@Column(columnDefinition = "TEXT")
//	private String content;
//	private String imagePath;
//	
//	@Enumerated(EnumType.STRING)
//	private MessageState state;
//	@Enumerated(EnumType.STRING)
//	private MessegeType type;
//	@ManyToOne
//	@JoinColumn(name="dm_id")
//	private DirectMessage directMessage;
//	@Column(nullable = false)
//	private String senderId;
//	@Column(nullable = false)
//	private String recieverId;
}
