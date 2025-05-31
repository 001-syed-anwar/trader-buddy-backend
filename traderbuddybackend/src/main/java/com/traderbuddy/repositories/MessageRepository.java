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
	List<Message> findNextPage(@Param("cursor") LocalDateTime cursor, Long channelId, Long messageId, Long dmId,
			Pageable pageable);

	// Case 1: channelId is NOT NULL, directMessageId and parentMessageId are NULL
	@Query("""
			    SELECT m FROM Message m
			    WHERE m.createdAt < COALESCE(:cursor, CURRENT_TIMESTAMP)
			      AND m.channelId = :channelId
			      AND m.directMessageId IS NULL
			      AND m.parentMessageId IS NULL
			    ORDER BY m.createdAt DESC
			""")
	List<Message> findByChannelIdBeforeCreatedAt(@Param("channelId") Long channelId,
			@Param("cursor") LocalDateTime cursor, Pageable pageable);

	// Case 2: directMessageId is NOT NULL, others are NULL
	@Query("""
			    SELECT m FROM Message m
			    WHERE m.createdAt < COALESCE(:cursor, CURRENT_TIMESTAMP)
			      AND m.directMessageId = :dmId
			      AND m.channelId IS NULL
			      AND m.parentMessageId IS NULL
			    ORDER BY m.createdAt DESC
			""")
	List<Message> findByDirectMessageIdBeforeCreatedAt(@Param("dmId") Long dmId, @Param("cursor") LocalDateTime cursor,
			Pageable pageable);

	// Case 3: channelId and parentMessageId NOT NULL, directMessageId is NULL
	@Query("""
			    SELECT m FROM Message m
			    WHERE m.createdAt < COALESCE(:cursor, CURRENT_TIMESTAMP)
			      AND m.channelId = :channelId
			      AND m.parentMessageId = :parentMessageId
			      AND m.directMessageId IS NULL
			    ORDER BY m.createdAt DESC
			""")
	List<Message> findByChannelIdAndParentMessageIdBeforeCreatedAt(@Param("channelId") Long channelId,
			@Param("parentMessageId") Long parentMessageId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

	// Case 4: directMessageId and parentMessageId NOT NULL, channelId is NULL
	@Query("""
			    SELECT m FROM Message m
			    WHERE m.createdAt < COALESCE(:cursor, CURRENT_TIMESTAMP)
			      AND m.directMessageId = :dmId
			      AND m.parentMessageId = :parentMessageId
			      AND m.channelId IS NULL
			    ORDER BY m.createdAt DESC
			""")
	List<Message> findByDirectMessageIdAndParentMessageIdBeforeCreatedAt(@Param("dmId") Long dmId,
			@Param("parentMessageId") Long parentMessageId, @Param("cursor") LocalDateTime cursor, Pageable pageable);

	// does not utilize the index nor the guaranteed ordering
	List<Message> findAllByParentMessageId(Long parentMessageId);

	// utilizes the index and ensure the order
	List<Message> findAllByParentMessageIdOrderByCreatedAtAsc(Long parentMessageId);

	void deleteAllByChannelId(Long channelId);

	void deleteAllByDirectMessageId(Long channelId);

	void deleteAllByParentMessageId(Long channelId);
}
