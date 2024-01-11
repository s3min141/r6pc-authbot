package com.r6.authbot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 유비소프트 프로필 정보를 담는 domain
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
@Getter
@Setter
@AllArgsConstructor
public class UbisoftProfile {
    private String profileId;
    private String userId;
    private String platformType;
    private String idOnPlatform;
    private String nameOnPlatform;
}
