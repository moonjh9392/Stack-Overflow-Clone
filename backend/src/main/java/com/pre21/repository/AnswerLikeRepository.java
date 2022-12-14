package com.pre21.repository;


import com.pre21.entity.AnswerLikes;
import com.pre21.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerLikeRepository extends JpaRepository<AnswerLikes, Long> {
    Optional<AnswerLikes> findAnswerLikesByUsers(User user);
}
