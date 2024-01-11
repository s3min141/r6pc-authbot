package com.r6.authbot.service.impl;

import java.awt.Color;
import java.sql.SQLException;

import com.r6.authbot.dao.iVerifiedUserDao;
import com.r6.authbot.dao.impl.VerifiedUserDaoImpl;
import com.r6.authbot.domain.VerifiedUser;
import com.r6.authbot.service.iVerifiedUserService;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class VerifiedUserServiceImpl implements iVerifiedUserService {

    private iVerifiedUserDao verifiedUserDao = new VerifiedUserDaoImpl();

    @Override
    public void registerToVerifiedUser(ButtonInteractionEvent event, VerifiedUser userInfo) {
        try {
            verifiedUserDao.register(userInfo);
        } catch (SQLException ex) {
            ex.printStackTrace();
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("유비소프트 계정 인증")
                    .setDescription("인증된 유저로 등록하는 중에 오류가 발생했습니다 (문의 요망)")
                    .setColor(Color.RED)
                    .build();
            event.replyEmbeds(embed).queue();
            return;
        }
    }

    @Override
    public VerifiedUser getByDiscordUid(String discordUid) {
        return verifiedUserDao.getByDiscordUid(discordUid);
    }

    @Override 
    public VerifiedUser getByUbisoftUid(String ubisoftUid) {
        return verifiedUserDao.getByUbisoftUid(ubisoftUid);
    }
}
