package com.zolp.custhumb.domain.thumbnail.application;

import com.zolp.custhumb.domain.thema.dao.ThemaRepository;
import com.zolp.custhumb.domain.thema.domain.Thema;
import com.zolp.custhumb.domain.thumbnail.dao.ThumbnailRepository;
import com.zolp.custhumb.domain.thumbnail.domain.Thumbnail;
import com.zolp.custhumb.domain.thumbnail.dto.request.CreateThumbnailRequest;
import com.zolp.custhumb.domain.thumbnail.dto.request.EditThumbnailRequest;
import com.zolp.custhumb.domain.thumbnail.dto.response.ThumbnailResponse;
import com.zolp.custhumb.domain.user.dao.UserRepository;
import com.zolp.custhumb.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ThumbnailService {

    private final ThumbnailRepository thumbnailRepository;
    private final UserRepository userRepository;

    public ThumbnailResponse create(CreateThumbnailRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        // AI 연동 전까지는 썸네일 URL을 빈 문자열로 설정
        Thumbnail thumbnail = Thumbnail.builder()
                .url("https://dummy.thumbnail.jpg")
                .user(user)
                .build();

        Thumbnail saved = thumbnailRepository.save(thumbnail);
        return new ThumbnailResponse(saved.getId(), saved.getUrl());
    }

    public ThumbnailResponse edit(Long id, EditThumbnailRequest request) {
        Thumbnail thumbnail = thumbnailRepository.findById(id).orElseThrow();
        String newURL = "https://new-dummy.thumbnail.jpg";
        thumbnail.updateUrl(newURL);
        return new ThumbnailResponse(thumbnail.getId(), thumbnail.getUrl());
    }
}