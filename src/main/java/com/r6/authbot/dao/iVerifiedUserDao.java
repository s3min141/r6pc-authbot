package com.r6.authbot.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import com.r6.authbot.domain.VerifiedUser;

/**
 * 인증된 유저 관련 Dao
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
public interface iVerifiedUserDao {

    /**
     * 인증된 유저 등록
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>VerifiedUser</b> : 인증된 유저 정보를 담고 있는 domain
     */
    public void register(VerifiedUser user) throws SQLException;

    /**
     * 인증된 유저 조회
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>String</b> : 디스코드 UID
     * @return <b>VerifiedUser</b> : 인증된 유저 정보를 담고 있는 domain
     */
    public VerifiedUser getByDiscordUid(String discordUid);

    /**
     * 인증된 유저 조회
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>String</b> : 유비소프트 UID
     * @return <b>VerifiedUser</b> : 인증된 유저 정보를 담고 있는 domain
     */
    public VerifiedUser getByUbisoftUid(String ubisoftUid);

    /**
     * 인증된 유저 목록 조회
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.15
     * @return <b>ArrayList<VerifiedUser></b> : 인증된 유저 정보를 담고 있는 domain List
     */
    public ArrayList<VerifiedUser> getList();
}
