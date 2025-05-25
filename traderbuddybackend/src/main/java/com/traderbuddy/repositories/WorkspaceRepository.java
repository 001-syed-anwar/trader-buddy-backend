package com.traderbuddy.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.traderbuddy.models.Workspace;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
}
