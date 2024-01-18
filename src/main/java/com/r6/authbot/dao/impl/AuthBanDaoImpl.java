package com.r6.authbot.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.r6.authbot.configure.DataSource;
import com.r6.authbot.dao.iAuthBanDao;
import com.r6.authbot.domain.AuthBanInfo;
import com.r6.authbot.domain.RegisterAuthBan;

public class AuthBanDaoImpl implements iAuthBanDao {

    @Override
    public AuthBanInfo register(RegisterAuthBan banInfo) throws SQLException {
        Connection conn = DataSource.getConn();

        Boolean isExist = false;
        String checkSql = "SELECT EXISTS (SELECT * FROM `auth_ban` WHERE `discord_uid`=?)";
        PreparedStatement checkPstmt = conn.prepareStatement(checkSql);
        checkPstmt.setString(1, banInfo.getDiscordUid());
        ResultSet rs = checkPstmt.executeQuery();
        if (rs.next()) {
            isExist = rs.getBoolean(1);
        }

        String nonExistSql = "INSERT INTO `auth_ban`(`discord_uid`, `start_date`, `end_date`, `ban_reason`) VALUES(?,?,?,?)";
        String existSql = "UPDATE `auth_ban` SET `start_date`=?, `end_date`=?, `ban_reason`=? WHERE `discord_uid`=?";
        PreparedStatement pstmt = null;

        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = new Date();
        Calendar endDate = Calendar.getInstance();
        endDate.setTime(startDate);
        endDate.add(Calendar.DATE, banInfo.getDay());
        endDate.add(Calendar.HOUR, banInfo.getHour());
        endDate.add(Calendar.MINUTE, banInfo.getMinute());

        if (isExist) {
            pstmt = conn.prepareStatement(existSql);
            pstmt.setString(1, sdformat.format(startDate.getTime()));
            pstmt.setString(2, sdformat.format(endDate.getTime()));
            pstmt.setString(3, banInfo.getBanReason());
            pstmt.setString(4, banInfo.getDiscordUid());
        } else {
            pstmt = conn.prepareStatement(nonExistSql);
            pstmt.setString(1, banInfo.getDiscordUid());
            pstmt.setString(2, sdformat.format(startDate.getTime()));
            pstmt.setString(3, sdformat.format(endDate.getTime()));
            pstmt.setString(4, banInfo.getBanReason());
        }

        if (pstmt.executeUpdate() == 0) {
            throw new SQLException("인증 차단 중 오류가 발생했습니다.");
        }

        AuthBanInfo createdBanInfo = new AuthBanInfo(
            banInfo.getDiscordUid(), 
            sdformat.format(startDate.getTime()), 
            sdformat.format(endDate.getTime()), 
            banInfo.getBanReason()
        );

        checkPstmt.close();
        pstmt.close();
        conn.close();

        return createdBanInfo;
    }

    @Override
    public void delete(String discordUid) throws SQLException {
        Connection conn = DataSource.getConn();
        String sql = "DELETE FROM `auth_ban` WHERE `discord_uid` = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, discordUid);
        if (pstmt.executeUpdate() == 0) {
            throw new SQLException("인증 차단해제 중 오류가 발생했습니다.");
        }
        pstmt.close();
        conn.close();
    }

    @Override
    public AuthBanInfo getBanInfoById(String discordUid) {
        Connection conn = DataSource.getConn();
        String sql = "SELECT * FROM `auth_ban` WHERE `discord_uid`=?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, discordUid);
            ResultSet rs = pstmt.executeQuery();
            AuthBanInfo banInfo = null;
            if (rs.next()) {
                banInfo = new AuthBanInfo();
                banInfo.setDiscordUid(rs.getString(1));
                banInfo.setStartDate(rs.getString(2));
                banInfo.setEndDate(rs.getString(3));
                banInfo.setBanReason(rs.getString(4));
            }
            return banInfo;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public void clean() {
        Connection conn = DataSource.getConn();
        String sql = "DELETE FROM auth_ban WHERE end_date < CURRENT_DATE";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();

            pstmt.close();
            conn.close();
        } catch (SQLException ex) {
            System.out.println("========= 만료된 인증 차단 삭제 중 오류 발생 =========");
            ex.printStackTrace();
        }
    }
}
