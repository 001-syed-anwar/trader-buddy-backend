package com.traderbuddy.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.traderbuddy.models.DirectMessage;

@Repository
public interface DirectMessageRepository extends JpaRepository<DirectMessage, Long> {
	@Query("SELECT dm FROM DirectMessage dm WHERE (dm.memberOne = :memberOne AND dm.memberTwo = :memberTwo) OR (dm.memberOne = :memberTwo AND dm.memberTwo = :memberOne)")
	Optional<DirectMessage> findByMemberOneAndMemberTwo(@Param("memberOne") Long memberOne, @Param("memberTwo") Long memberTwo);
}
