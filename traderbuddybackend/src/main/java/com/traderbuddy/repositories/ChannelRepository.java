package com.traderbuddy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.traderbuddy.models.Channel;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
	void deleteAllByWorkspaceId(Long workspaceId);

	List<Channel> findAllByWorkspaceId(Long workspaceId);
}
