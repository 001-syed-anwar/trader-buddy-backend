package com.traderbuddy.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.traderbuddy.auth.user.User;
import com.traderbuddy.models.Reaction;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

	Optional<Reaction> findByMessageId(Long messageId);

	List<Reaction> findAllByMessageId(Long messageId);

	Optional<Reaction> findByMessageIdAndMemberId(Long messageId, Long id);

}
