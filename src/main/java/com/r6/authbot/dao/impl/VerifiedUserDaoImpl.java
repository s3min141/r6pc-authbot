package com.r6.authbot.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.r6.authbot.configure.DataSource;
import com.r6.authbot.dao.iVerifiedUserDao;
import com.r6.authbot.domain.VerifiedUser;

public class VerifiedUserDaoImpl implements iVerifiedUserDao{

    @Override
    public void register(VerifiedUser user) throws SQLException {
        Connection conn = DataSource.getConn();

        Boolean isExist = false;
        String checkSql = "SELECT EXISTS (SELECT * FROM `verified_user` WHERE `discord_uid`=?)";
        PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
        checkPstmt.setString(1, user.getDiscordUid());
        ResultSet rs = checkPstmt.executeQuery();
        if (rs.next()) {
            isExist = rs.getBoolean(1);
        }

        String nonExistSql = "INSERT INTO `verified_user`(`discord_uid`, `ubisoft_uid`, `ubisoft_uname`, `current_mmr`) VALUES(?,?,?,?)";
        String existSql = "UPDATE `verified_user` SET `ubisoft_uid`=?, `ubisoft_uname`=?, `current_mmr`=? WHERE `discord_uid`=?";
        PreparedStatement pstmt = null;
        if (isExist) {
            pstmt = conn.prepareStatement(existSql);
            pstmt.setString(1, user.getUbisoftUid());
            pstmt.setString(2, user.getUbisoftUname());
            pstmt.setInt(3, user.getCurrentMMR());
            pstmt.setString(4, user.getDiscordUid());
        }
        else {
            pstmt = conn.prepareStatement(nonExistSql);
            pstmt.setString(1, user.getDiscordUid());
            pstmt.setString(2, user.getUbisoftUid());
            pstmt.setString(3, user.getUbisoftUname());
            pstmt.setInt(4, user.getCurrentMMR());
        }

        if (pstmt.executeUpdate() == 0) {
            throw new SQLException("인증된 유저로 등록에 실패했습니다.");
        }

        checkPstmt.close();
        pstmt.close();
        conn.close();
    }

    @Override
    public VerifiedUser get(String discordUid) {
        Connection conn = DataSource.getConn();
        String sql = "SELECT * FROM `verified_user` WHERE `discord_uid`=?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, discordUid);
            ResultSet rs = pstmt.executeQuery();
            VerifiedUser verifiedInfo = null;
            if (rs.next()) {
                verifiedInfo = new VerifiedUser();
                verifiedInfo.setDiscordUid(rs.getString(1));
                verifiedInfo.setUbisoftUid(rs.getString(2));
                verifiedInfo.setUbisoftUname(rs.getString(3));
                verifiedInfo.setCurrentMMR(rs.getInt(4));
            }
            return verifiedInfo;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
