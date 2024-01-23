package com.r6.authbot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 인증된 유저의 정보를 담는 domain
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
public class VerifiedUser {
    private String discordUid;
    private String ubisoftUid;
    private String ubisoftUname;
    private Integer currentMMR;
    private Integer currentKills;
    private Integer currentWins;
}
