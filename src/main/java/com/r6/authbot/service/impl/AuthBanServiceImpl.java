package com.r6.authbot.service.impl;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.r6.authbot.dao.iAuthBanDao;
import com.r6.authbot.dao.impl.AuthBanDaoImpl;
import com.r6.authbot.domain.AuthBanInfo;
import com.r6.authbot.domain.RegisterAuthBan;
import com.r6.authbot.service.iAuthBanService;

public class AuthBanServiceImpl implements iAuthBanService {

    private iAuthBanDao authBanDao = new AuthBanDaoImpl();

    @Override
    public AuthBanInfo checkBanInfo(String discordUid) {
        AuthBanInfo banInfo = authBanDao.getBanInfoById(discordUid);

        if (banInfo != null) {
            try {
                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date endDate = sdformat.parse(banInfo.getEndDate());
                Date nowDate = new Date();

                Integer dateCompareResult = endDate.compareTo(nowDate);

                if (dateCompareResult > 0) {
                    return banInfo;
                }
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public AuthBanInfo registerAuthBan(RegisterAuthBan authBanInfo) {
        try {
            return authBanDao.register(authBanInfo);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean unRegisterAuthBan(String discordUid) {
        try {
            authBanDao.delete(discordUid);
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public void cleanExpiredAuthBan() {
        authBanDao.clean();
    }
}
