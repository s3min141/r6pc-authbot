package com.r6.authbot.configure;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.r6.authbot.domain.VerifiedUser;
import com.r6.authbot.enums.BotConfig;
import com.r6.authbot.service.iAuthBanService;
import com.r6.authbot.service.iVerifiedUserService;
import com.r6.authbot.service.impl.AuthBanServiceImpl;
import com.r6.authbot.service.impl.VerifiedUserServiceImpl;
import com.r6.authbot.util.LeaderboardUtil;
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
                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = new Date();

                System.out.println(String.format("[%s] ========== 만료된 인증차단 삭제 시작 ==========", sdformat.format(startDate)));
                authBanService.cleanExpiredAuthBan();
            }
        };

        Timer reloadLeaderboardTimer = new Timer();
        TimerTask reloadLeaderboardTask = new TimerTask() {
            @Override
            public void run() {
                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = new Date();

                System.out.println(String.format("[%s] ========== 리더보드 이미지 업데이트 시작 ==========", sdformat.format(startDate)));

                try {
                    LeaderboardUtil.refreshLeaderboard();
                } catch (Exception ex) {
                    System.out.println(String.format("[%s] ========== 리더보드 이미지 업데이트 실패 ==========", sdformat.format(startDate)));
                    ex.printStackTrace();
                }
            }
        };

        clearBanTimer.scheduleAtFixedRate(clearBanTask, 0, TimeUnit.MICROSECONDS.convert(dayPeriod, TimeUnit.DAYS));
        reloadLeaderboardTimer.scheduleAtFixedRate(reloadLeaderboardTask, 0,
                TimeUnit.MICROSECONDS.convert(dayPeriod, TimeUnit.DAYS));
    }
}
