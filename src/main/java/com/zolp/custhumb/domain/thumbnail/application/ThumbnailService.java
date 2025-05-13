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
    private final ThemaRepository themaRepository;
    private final UserRepository userRepository;

    public ThumbnailResponse create(CreateThumbnailRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<Thema> themas = themaRepository.findAllById(request.themaIds());

        Thumbnail thumbnail = Thumbnail.builder()
                .url(request.url())
                .user(user)
                .themas(themas)
                .build();

        Thumbnail saved = thumbnailRepository.save(thumbnail);
        return new ThumbnailResponse(saved.getId(), saved.getUrl());
    }

    public ThumbnailResponse edit(Long id, EditThumbnailRequest request) {
        Thumbnail thumbnail = thumbnailRepository.findById(id).orElseThrow();
        thumbnail.updateUrl(request.newUrl());
        return new ThumbnailResponse(thumbnail.getId(), thumbnail.getUrl());
    }
}