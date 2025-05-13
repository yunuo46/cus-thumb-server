package com.zolp.custhumb.domain.thumbnail.domain;

import com.zolp.custhumb.domain.thema.domain.Thema;
import com.zolp.custhumb.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Thumbnail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String url;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "thumbnail_thema",
            joinColumns = @JoinColumn(name = "thumbnail_id"),
            inverseJoinColumns = @JoinColumn(name = "thema_id")
    )
    private List<Thema> themas = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void updateUrl(String url) {
        this.url = url;
    }
}
