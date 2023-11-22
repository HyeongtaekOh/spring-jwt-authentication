package com.example.demo.security.redis.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@RedisHash("refresh_token")
public class RefreshToken {

    @Id
    private String username;

    @Indexed
    private String refreshToken;

    @TimeToLive
    private Long ttl;

    public RefreshToken update(String refreshToken, Long ttl) {
        this.refreshToken = refreshToken;
        this.ttl = ttl;
        return this;
    }
}
