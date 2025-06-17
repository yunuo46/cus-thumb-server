package com.zolp.custhumb.domain.thumbnail.dao;

import com.zolp.custhumb.domain.thumbnail.domain.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long> {
    // 사용자의 모든 썸네일을 생성일자 내림차순으로 조회하는 메서드
    List<Thumbnail> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}

