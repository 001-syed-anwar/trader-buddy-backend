package com.traderbuddy.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.traderbuddy.models.Channel;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
	void deleteAllByWorkspaceId(Long workspaceId);

	List<Channel> findAllByWorkspaceId(Long workspaceId);
}
