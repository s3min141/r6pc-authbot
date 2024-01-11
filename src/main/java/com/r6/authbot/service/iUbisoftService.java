package com.r6.authbot.service;

import com.r6.authbot.domain.UbisoftProfile;

/**
 * 유비소프트 API 서비스
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
public interface iUbisoftService {

    /**
     * 디스코드 UID로 유비소프트 UID 가져오는 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>String</b> : 디스코드 UID
     * @return <b>String</b> 디스코드와 연동된 유비소프트 프로필의 UID를 반환, 없다면 공백 반환
     */
    public String getUserIdByDiscordUid(String discordUid);

    /**
     * 유비소프트 UID로 유비소프트 프로필 가져오는 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>String</b> : 유비소프트 UID
     * @return <b>String</b> 유비소프트 UID로 유비소프트 프로필 정보를 가져와 UbisoftProfile 객체로 반환, 프로필이 없다면 null 반환
     */
    public UbisoftProfile getProfileById(String userId);

    /**
     * 유저의 Rank2.0 MMR 가져오는 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>String</b> : 유비소프트 UID
     * @return <b>Integer</b> 유저의 Rank2.0 MMR을 Integer타입으로 반환, 가져오는데 실패시 0을 반환
     */
    public Integer getUserRank2MMR(String userId);

    /**
     * 서비스용 Session Ticket의 유효성 체크 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @return <b>Boolean</b> Session Ticket이 유효하다면 true 아니라면 false를 반환
     */
    public Boolean isTicketValid();
}