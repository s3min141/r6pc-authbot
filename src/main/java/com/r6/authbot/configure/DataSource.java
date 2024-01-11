package com.r6.authbot.configure;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.r6.authbot.enums.BotConfig;

/**
 * 인증 차단 서비스
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
public class DataSource {

    /**
     * 인증 차단 서비스
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @return <b>java.sql.Connection</b> : DB 연결 객체를 반환
     * @since 2024.01.11
     */
    public static Connection getConn() {
        Connection connection = null;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mariadb://" + BotConfig.DB_URL.getStrVal(),
                    BotConfig.DB_USERNAME.getStrVal(), BotConfig.DB_PASSWORD.getStrVal());
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    }
}
