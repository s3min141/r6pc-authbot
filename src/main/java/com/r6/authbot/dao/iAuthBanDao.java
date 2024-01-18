package com.r6.authbot.dao;

import java.sql.SQLException;

import com.r6.authbot.domain.AuthBanInfo;
import com.r6.authbot.domain.RegisterAuthBan;

/**
 * 인증 차단 관련 Dao
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
public interface iAuthBanDao {

    /**
     * 인증 차단 등록
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>RegisterAuthBan</b> : 인증 차단 정보를 담고 있는 domain
     * @return <b>AuthBanInfo</b> : 인증 차단 후 정보를 담고 있는 domain
     */
    public AuthBanInfo register(RegisterAuthBan banInfo) throws SQLException;

    /**
     * 인증 차단 해제
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>String</b> : 디스코드 UID
     */
    public void delete(String discordUid) throws SQLException;

    /**
     * 인증 차단 조회
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>String</b> : 디스코드 UID
     * @return <b>AuthBanInfo</b> 인증 차단 정보를 담고 있는 domain
     */
    public AuthBanInfo getBanInfoById(String discordUid);

    /**
     * 만료된 차단 삭제
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.15
     */
    public void clean();
}
