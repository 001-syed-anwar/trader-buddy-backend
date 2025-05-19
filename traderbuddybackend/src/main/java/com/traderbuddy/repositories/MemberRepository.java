package com.traderbuddy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.traderbuddy.models.Member;
import com.traderbuddy.models.Workspace;

public interface MemberRepository extends JpaRepository<Member, Long> {
	List<Member> findAllByWorkspaceId(Long userId);
	List<Member> findAllByUserId(Long userId);

	@Query("SELECT m.workspaceId FROM Member m WHERE m.userId = :userId")
	List<Long> findWorkspaceIdsByUserId(@Param("userId") Long userId);

	@Query("""
			    SELECT w FROM Member m
			    JOIN Workspace w ON m.workspaceId = w.id
			    WHERE m.userId = :userId AND m.workspaceId = :workspaceId
			""")
	Optional<Workspace> findWorkspaceIfUserIsMember(@Param("userId") Long userId,
			@Param("workspaceId") Long workspaceId);

	Optional<Member> findByUserIdAndWorkspaceId(Long userId, Long workspaceId);
	
	void deleteByWorkspaceId(Long id);
	

}
