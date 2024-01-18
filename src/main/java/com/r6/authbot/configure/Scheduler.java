package com.r6.authbot.configure;

import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Collections;

import java.io.InputStream;

import com.r6.authbot.domain.VerifiedUser;
import com.r6.authbot.enums.BotConfig;
import com.r6.authbot.service.iAuthBanService;
import com.r6.authbot.service.iVerifiedUserService;
import com.r6.authbot.service.impl.AuthBanServiceImpl;
import com.r6.authbot.service.impl.VerifiedUserServiceImpl;
import com.r6.authbot.util.CommonUtil;
import com.r6.authbot.util.VerifiedUserComparator;

/**
 * 하루마다 실행하는 스케쥴러 class
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.15
 */
public class Scheduler {

    private Integer dayPeriod;
    private iAuthBanService authBanService = new AuthBanServiceImpl();
    private iVerifiedUserService verifiedUserService = new VerifiedUserServiceImpl();

    public Scheduler() {
        // dayPeriod 파라미터가 없을경우 기본값 하루로 설정
        this.dayPeriod = 1;
    }

    public Scheduler(Integer dayPeriod) {
        this.dayPeriod = dayPeriod;
    }

    public void start() {
        Timer clearBanTimer = new Timer();
        TimerTask clearBanTask = new TimerTask() {
            @Override
            public void run() {
                authBanService.cleanExpiredAuthBan();
            }
        };

        Timer reloadLeaderboardTimer = new Timer();
        TimerTask reloadLeaderboardTask = new TimerTask() {
            @Override
            public void run() {
                ArrayList<InputStream> imgArray = new ArrayList<>();
                ArrayList<VerifiedUser> verifiedUsers = verifiedUserService.getVerifiedUserList();
                Collections.sort(verifiedUsers, new VerifiedUserComparator());

                String tbodyString = "";

                Integer userPerPage = 5;
                Integer currentPage = 1;
                Integer userCount = 1;

                while (currentPage <= verifiedUsers.size() / userPerPage + 1) {
                    tbodyString += CommonUtil.createTbodyElement(verifiedUsers.get(0), 1);
                    tbodyString += "<tr>\r\n" + // divder
                            "                <td style='border-top: 4px solid gray;'></td>\r\n" + //
                            "                <td style='border-top: 4px solid gray;'></td>\r\n" + //
                            "                <td style='border-top: 4px solid gray;'></td>\r\n" + //
                            "            </tr>";
                    for (Integer i = userCount; i < verifiedUsers.size(); i++) {
                        if (verifiedUsers.get(i) == null || userCount >= currentPage * userPerPage) {
                            break;
                        }

                        tbodyString += CommonUtil.createTbodyElement(verifiedUsers.get(i), i + 1);
                        userCount++;
                    }

                    imgArray.add(CommonUtil.createLeaderboardImg(tbodyString));
                    tbodyString = "";
                    currentPage++;
                }

                BotConfig.LEADERBOARD_IMGS.setArrayVal(imgArray);
            }
        };

        clearBanTimer.scheduleAtFixedRate(clearBanTask, 0, TimeUnit.MICROSECONDS.convert(dayPeriod, TimeUnit.DAYS));
        reloadLeaderboardTimer.scheduleAtFixedRate(reloadLeaderboardTask, 0,
                TimeUnit.MICROSECONDS.convert(dayPeriod, TimeUnit.DAYS));
    }
}
