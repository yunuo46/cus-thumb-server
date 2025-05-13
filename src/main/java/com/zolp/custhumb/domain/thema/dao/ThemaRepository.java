package com.zolp.custhumb.domain.thema.dao;

import com.zolp.custhumb.domain.thema.domain.Thema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThemaRepository extends JpaRepository<Thema, Long> {
    List<Thema> findAllByUserId(Long userId);
}
