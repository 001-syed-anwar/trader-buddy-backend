package com.traderbuddy.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.traderbuddy.models.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	
	@Query("""
			SELECT m FROM Message m WHERE m.createdAt < COALESCE(:cursor, CURRENT_TIMESTAMP) AND
			((:channelId IS NULL AND m.channelId IS NULL) OR (m.channelId = :channelId)) AND
			((:messageId IS NULL AND m.parentMessageId IS NULL) OR (m.parentMessageId = :messageId)) AND
			((:dmId IS NULL AND m.directMessageId IS NULL) OR (m.directMessageId = :dmId))
			ORDER BY m.createdAt DESC
			""")
	List<Message> findNextPage(@Param("cursor") LocalDateTime cursor, Long channelId, Long messageId,Long dmId, Pageable pageable);

	List<Message> findAllByParentMessageId(Long parentMessageId);
}
