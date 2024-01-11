package com.r6.authbot.service.impl;

import java.awt.Color;
import java.util.HashMap;

import com.r6.authbot.domain.AuthBanInfo;
import com.r6.authbot.domain.RegisterAuthBan;
import com.r6.authbot.domain.UbisoftProfile;
import com.r6.authbot.domain.VerifiedUser;
import com.r6.authbot.enums.BotConfig;
import com.r6.authbot.service.iAuthBanService;
import com.r6.authbot.service.iButtonService;
import com.r6.authbot.service.iUbisoftService;
import com.r6.authbot.service.iVerifiedUserService;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class ButtonServiceImpl implements iButtonService {

    private iUbisoftService ubisoftService = new UbisoftServiceImpl();
    private iVerifiedUserService verifiedUserService = new VerifiedUserServiceImpl();
    private iAuthBanService authBanService = new AuthBanServiceImpl();

    private HashMap<String, Integer> phase1FailedMap = new HashMap<>();
    private HashMap<String, Integer> phase2FailedMap = new HashMap<>();

    @Override
    public void doAuth(ButtonInteractionEvent event) {
        String discordUid = event.getUser().getId();
        VerifiedUser verifiedInfo = verifiedUserService.getByDiscordId(discordUid);
        AuthBanInfo banInfo = authBanService.checkBanInfo(discordUid);
        Integer phase1Failed = phase1FailedMap.get(discordUid);
        Integer phase2Failed = phase2FailedMap.get(discordUid);

        if (banInfo != null) {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("유비소프트 계정 인증")
                    .setDescription(String.format("%s 이후에 재인증이 가능합니다.", banInfo.getEndDate()))
                    .addField("사유", banInfo.getBanReason(), false)
                    .setColor(Color.RED)
                    .build();
            event.replyEmbeds(embed).queue();
            return;
        }

        if (verifiedInfo != null) {

        }

        if (phase1Failed != null && phase1Failed >= 3) {
            RegisterAuthBan registerBan = new RegisterAuthBan(discordUid, "프로필 등록이 되지않은 상태로 여러번 시도.", 7, 0, 0);
            authBanService.registerAuthBan(registerBan);
            return;
        }

        if (phase2Failed != null && phase2Failed >= 3) {
            RegisterAuthBan registerBan = new RegisterAuthBan(discordUid, "인증 3회 이상 실패", 0, 0, 15);
            authBanService.registerAuthBan(registerBan);
            return;
        }

        // Phase 1
        String ubisoftUid = ubisoftService.getUserIdByDiscordUid(discordUid);

        if (ubisoftUid.isBlank()) {
            phase1FailedMap.put(discordUid, phase1Failed == null ? 1 : phase1Failed + 1);

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("유비소프트 계정 인증")
                    .setDescription("유비소프트와 디스코드간 계정 연동을 확인할 수 없습니다. 계정 연결하기 버튼을 먼저 눌러 연동 작업을 진행해 주세요.")
                    .setColor(Color.RED)
                    .build();
            event.replyEmbeds(embed).queue();
            return;
        }

        // Phase 2
        UbisoftProfile userProfile = ubisoftService.getProfileById(ubisoftUid);
        if (userProfile == null) {
            phase2FailedMap.put(discordUid, phase2Failed == null ? 1 : phase2Failed + 1);

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("유비소프트 계정 인증")
                    .setDescription("Ubisoft 프로필을 가져올 수 없습니다.")
                    .setColor(Color.RED)
                    .build();
            event.replyEmbeds(embed).queue();
            return;
        }

        Integer userRank2MMR = ubisoftService.getUserRank2MMR(userProfile.getUserId());
        Boolean isRanker = userRank2MMR >= BotConfig.MIN_RANKER_MMR.getIntVal();

        if (isRanker) {
            VerifiedUser registerUserInfo = new VerifiedUser(discordUid, ubisoftUid, userProfile.getNameOnPlatform(),
                    userRank2MMR);
            verifiedUserService.registerToVerifiedUser(event, registerUserInfo);

            Role specialRole = event.getGuild().getRoleById(BotConfig.SPECIAL_ROLE_ID.getStrVal());
            event.getGuild().addRoleToMember(event.getMember(), specialRole).queue();

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("유비소프트 계정 인증")
                    .setDescription("계정 연동과 TopPlayer 조건 일치가 확인되어 역할이 정상 부여되었습니다.")
                    .addField("계정 닉네임", userProfile.getNameOnPlatform(), true)
                    .addField("계정 ID", userProfile.getUserId(), true)
                    .addField("최근 MMR", userRank2MMR.toString(), true)
                    .setColor(Color.GREEN)
                    .build();
            event.replyEmbeds(embed).queue();
        } else {
            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("유비소프트 계정 인증")
                    .setDescription("계정 연동은 정상적으로 확인되었으나, TopPlayer 역할은 4700 MMR 이상만 신청 가능합니다.")
                    .setColor(Color.RED)
                    .build();
            event.replyEmbeds(embed).queue();
        }

        RegisterAuthBan registerBan = new RegisterAuthBan(discordUid, "이미 인증된 계정입니다.", 7, 0, 0);
        authBanService.registerAuthBan(registerBan);
        phase1FailedMap.remove(discordUid);
    }
}