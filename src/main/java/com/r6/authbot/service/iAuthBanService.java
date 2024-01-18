package com.r6.authbot.service;

import com.r6.authbot.domain.AuthBanInfo;
import com.r6.authbot.domain.RegisterAuthBan;

/**
 * 인증 차단 서비스
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
public interface iAuthBanService {

    /**
     * 인증 차단 유무 조회 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>String</b> : 디스코드 UID
     * @return <b>AuthBanInfo</b> 디스코드 UID로 등록된 인증 차단이 있는지 확인, 있다면 AuthBanInfo객체 없다면
     *         null을 반환
     */
    public AuthBanInfo checkBanInfo(String discordUid);

    /**
     * 인증 차단 등록 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>RegisterAuthBan</b> : 인증 차단 정보 객체
     * @return <b>AuthBanInfo</b> 인증 차단 후 결과를 AuthBanInfo객체로 반환, 실패라면 null을 반환
     */
    public AuthBanInfo registerAuthBan(RegisterAuthBan authBanInfo);

    /**
     * 인증 차단 해제 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>String</b> : 디스코드 UID
     * @return <b>Boolean</b> 차단 해제 성공이라면 true 아니라면 false를 반환
     */
    public Boolean unRegisterAuthBan(String discordUid);

    /**
     * 만료된 인증 차단 삭제 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.15
     */
    public void cleanExpiredAuthBan();
}
