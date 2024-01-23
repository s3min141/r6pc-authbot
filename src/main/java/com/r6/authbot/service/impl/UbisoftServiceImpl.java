package com.r6.authbot.service.impl;

import com.r6.authbot.dao.iUbisoftDao;
import com.r6.authbot.dao.impl.UbisoftDaoImpl;
import com.r6.authbot.domain.UbisoftProfile;
import com.r6.authbot.domain.UserRankInfo;
import com.r6.authbot.service.iUbisoftService;

public class UbisoftServiceImpl implements iUbisoftService {

    private iUbisoftDao ubisoftDao = new UbisoftDaoImpl();

    @Override
    public String getUserIdByDiscordUid(String discordUid) {
        if (!ubisoftDao.isTicketValid()) {
            ubisoftDao.createSession();
        }

        return ubisoftDao.getUserId(discordUid);
    }

    @Override
    public UbisoftProfile getProfileById(String userId) {
        if (!ubisoftDao.isTicketValid()) {
            ubisoftDao.createSession();
        }

        return ubisoftDao.getProfile(userId);
    }

    @Override
    public UserRankInfo getUserRankInfo(String userId) {
        if (!ubisoftDao.isTicketValid()) {
            ubisoftDao.createSession();
        }

        return ubisoftDao.getUserRankInfo(userId);
    }

    @Override
    public Boolean isTicketValid() {
        if (!ubisoftDao.isTicketValid()) {
            ubisoftDao.createSession();
        }

        return ubisoftDao.isTicketValid();
    }
}
