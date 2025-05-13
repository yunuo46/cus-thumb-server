package com.zolp.custhumb.domain.thumbnail.dao;

import com.zolp.custhumb.domain.thumbnail.domain.Thumbnail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbnailRepository extends JpaRepository<Thumbnail, Long> {}

