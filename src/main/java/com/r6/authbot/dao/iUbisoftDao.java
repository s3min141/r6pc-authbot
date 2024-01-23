package com.r6.authbot.dao;

import com.r6.authbot.domain.UbisoftProfile;
import com.r6.authbot.domain.UserRankInfo;

/**
 * Ubosft API관련 Dao
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
public interface iUbisoftDao {

    /**
     * 유비소프트 API 세션을 생성하는 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     */
    public void createSession();

    /**
     * 디스코드 UID로 유비소프트 UID를 조회하는 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>String</b> : 디스코드 UID
     * @return <b>String</b> : 유비소프트 UID
     */
    public String getUserId(String discordUid);

    /**
     * 유비소프트 UID로 유비소프트 프로필 정보 조회하는 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>String</b> : 유비소프트 UID
     * @return <b>UbisoftProfile</b> : 유비소프트 프로필 정보 domain
     */
    public UbisoftProfile getProfile(String userId);

    /**
     * Rank2.0 정보 조회하는 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.23
     * @param <b>String</b> : 유비소프트 UID
     * @return <b>Integer</b> : Rank2.0 정보를 담은 UserRankInfo 도메인
     */
    public UserRankInfo getUserRankInfo(String userId);


    /**
     * 유비소프트 API Session Ticket이 유효한지 확인하는 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     */
    public Boolean isTicketValid();
}
