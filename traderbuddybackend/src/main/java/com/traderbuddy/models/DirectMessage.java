package com.traderbuddy.models;

import java.util.List;

import com.traderbuddy.auth.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@AllArgsConstructor
//@NoArgsConstructor
//@Data
//@Entity
//@Builder
//@Table(name="direct_messages")
public class DirectMessage {
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.UUID)
//	private String id;
//	@ManyToOne
//	@JoinColumn(name="sender_id")
//	private User sender;
//	@ManyToOne
//	@JoinColumn(name="recipient_id")
//	private User recipient;
//	@OneToMany(mappedBy = "direct_messages", fetch = FetchType.EAGER)
//	@OrderBy("createdAt DESC")
//	private List<Message> messages;
//	
//	@Transient
//	public String getLastMessge() {
//		return messages != null && !messages.isEmpty() ? messages.get(0).getContent() : "";
//	}
//	
//	@Transient
//	public long getUnreadMessges(String senderId) {
//		return messages.stream()
//				.filter(m->m.getRecieverId().equals(senderId))
//				.filter(m->m.getState()==MessageState.SENT)
//				.count();
//	}
}
