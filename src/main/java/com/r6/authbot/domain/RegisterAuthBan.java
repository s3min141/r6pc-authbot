package com.r6.authbot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 인증 차단 등록용 domain
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
@Getter
@Setter
@AllArgsConstructor
public class RegisterAuthBan {
    private String discordUid;
    private String banReason;
    private Integer day = 0;
    private Integer hour = 0;
    private Integer minute = 0;
}
