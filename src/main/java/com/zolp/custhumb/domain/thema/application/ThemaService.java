package com.zolp.custhumb.domain.thema.application;

import com.zolp.custhumb.domain.thema.dao.ThemaRepository;
import com.zolp.custhumb.domain.thema.domain.Thema;
import com.zolp.custhumb.domain.thema.dto.request.ThemaRequest;
import com.zolp.custhumb.domain.thema.dto.response.ThemaResponse;
import com.zolp.custhumb.domain.user.dao.UserRepository;
import com.zolp.custhumb.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ThemaService {

    private final ThemaRepository themaRepository;
    private final UserRepository userRepository;

    public ThemaResponse create(ThemaRequest request, Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Thema thema = Thema.builder()
                .prompt(request.prompt())
                .url(request.imageUrl())
                .user(user)
                .build();

        Thema saved = themaRepository.save(thema);
        return new ThemaResponse(saved.getId(), saved.getPrompt(), saved.getUrl());
    }

    public List<ThemaResponse> getUserThemas(Long userId) {
        List<Thema> themas = themaRepository.findAllByUserId(userId);
        return themas.stream()
                .map(t -> new ThemaResponse(t.getId(), t.getPrompt(), t.getUrl()))
                .toList();
    }
}