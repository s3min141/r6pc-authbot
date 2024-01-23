package com.r6.authbot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Rank2.0 정보를 담는 domain
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.23
 */
@Getter
@Setter
@AllArgsConstructor
public class UserRankInfo {
    private Integer mmr;
    private Integer kills;
    private Integer wins;
}
