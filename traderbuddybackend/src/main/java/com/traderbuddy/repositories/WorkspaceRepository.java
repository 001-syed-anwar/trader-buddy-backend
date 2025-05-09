package com.traderbuddy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.traderbuddy.models.Workspace;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

}
