package com.traderbuddy.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.traderbuddy.models.Messages;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, Long> {
	@Query("SELECT m FROM Messages m WHERE m.createdAt < COALESCE(:cursor, CURRENT_TIMESTAMP) ORDER BY m.createdAt DESC")
	List<Messages> findNextPage(@Param("cursor") LocalDateTime cursor, Pageable pageable);
	
	@Query("SELECT m FROM Messages m WHERE m.createdAt < COALESCE(:cursor, CURRENT_TIMESTAMP) AND m.channelId = :channelId ORDER BY m.createdAt DESC")
	List<Messages> findNextPageByChannelId(@Param("cursor") LocalDateTime cursor, Long channelId, Pageable pageable);
	
	// change the query
	@Query("SELECT m FROM Messages m WHERE m.createdAt < COALESCE(:cursor, CURRENT_TIMESTAMP) ORDER BY m.createdAt DESC")
	List<Messages> findNextPageByMemberId(@Param("cursor") LocalDateTime cursor, Pageable pageable);
}
