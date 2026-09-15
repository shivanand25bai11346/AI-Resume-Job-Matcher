package com.matcher.repository;

import com.matcher.entity.MatchResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchResultRepository extends JpaRepository<MatchResultEntity, Long> {
    List<MatchResultEntity> findTop10ByOrderByCreatedAtDesc();
}
