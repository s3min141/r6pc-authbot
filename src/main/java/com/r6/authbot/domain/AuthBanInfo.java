package com.r6.authbot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 인증 차단 정보를 담는 domain
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthBanInfo {
    private String discordUid;
    private String startDate;
    private String endDate;
    private String banReason;
}
