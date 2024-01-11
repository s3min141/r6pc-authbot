package com.r6.authbot.service.impl;

import java.awt.Color;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class ButtonServiceImpl implements iButtonService {

    private iUbisoftService ubisoftService = new UbisoftServiceImpl();
    private iVerifiedUserService verifiedUserService = new VerifiedUserServiceImpl();
    private iAuthBanService authBanService = new AuthBanServiceImpl();

    private HashMap<String, Integer> phase1FailedMap = new HashMap<>();
    private HashMap<String, Integer> phase2FailedMap = new HashMap<>();

    @Override
    public void doAuth(ButtonInteractionEvent event) {
        String discordUid = event.getUser().getId();
        VerifiedUser verifiedInfo = verifiedUserService.getByDiscordUid(discordUid);
        AuthBanInfo banInfo = authBanService.checkBanInfo(discordUid);

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
            String ubisoftUid = ubisoftService.getUserIdByDiscordUid(discordUid);

            if (!verifiedInfo.getUbisoftUid().equals(ubisoftUid)) {
                MessageEmbed authEmbed = new EmbedBuilder()
                        .setTitle("유비소프트 계정 인증")
                        .setDescription(String.format("이미 %s 계정으로 연동되어있습니다. 기존 연동을 취소하고 다시 연동하시겠습니까?\n\n** 30초뒤에 자동 취소됩니다 **", verifiedInfo.getUbisoftUname()))
                        .setColor(Color.BLUE)
                        .build();

                MessageCreateData messageCreateData = new MessageCreateBuilder()
                        .setEmbeds(authEmbed)
                        .setActionRow(
                                Button.of(ButtonStyle.PRIMARY, String.format("doReAuth-%s", discordUid),
                                        "네, 다시 연동합니다."))
                        .build();

                event.reply(messageCreateData).setEphemeral(true).queue();
                return;
            }
        }

        startPhase(event);
    }

    @Override
    public void doReAuth(ButtonInteractionEvent event) {
        event.deferReply().queue();

        String discordUid = event.getUser().getId();
        String buttonId = event.getButton().getId();
        String buttonOwnerUid = buttonId.split("-")[1];

        if (discordUid.equals(buttonOwnerUid)) {
            startPhase(event);
        }
        event.getMessage().delete().queue();
    }

    public void startPhase(ButtonInteractionEvent event) {
        String discordUid = event.getUser().getId();
        Integer phase1Failed = phase1FailedMap.get(discordUid);
        Integer phase2Failed = phase2FailedMap.get(discordUid);

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
            event.getHook().editOriginalEmbeds(embed).queue();
            return;
        }

        VerifiedUser checkAlreadyExist = verifiedUserService.getByUbisoftUid(ubisoftUid);
        if (checkAlreadyExist != null && !checkAlreadyExist.getDiscordUid().equals(discordUid)) {
            phase1FailedMap.put(discordUid, phase1Failed == null ? 1 : phase1Failed + 1);

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("유비소프트 계정 인증")
                    .setDescription(String.format("%s는 이미 연결되어있는 계정입니다.", checkAlreadyExist.getUbisoftUname()))
                    .setColor(Color.RED)
                    .build();
            event.getHook().editOriginalEmbeds(embed).queue();
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
            event.getHook().editOriginalEmbeds(embed).queue();
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
                    .addField("유비소프트 계정", userProfile.getNameOnPlatform(), true)
                    .addField("디스코드 계정", String.format("<@%s>", discordUid), true)
                    .addField("최근 MMR", userRank2MMR.toString(), true)
                    .setColor(Color.GREEN)
                    .build();
            event.getHook().editOriginalEmbeds(embed).queue();
        } else {
            RegisterAuthBan registerBan = new RegisterAuthBan(discordUid, "이미 인증된 계정입니다.", 7, 0, 0);
            authBanService.registerAuthBan(registerBan);

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("유비소프트 계정 인증")
                    .setDescription(String.format("계정 연동은 정상적으로 확인되었으나, TopPlayer 역할은 %s MMR 이상만 신청 가능합니다.",
                            BotConfig.MIN_RANKER_MMR.getIntVal()))
                    .setColor(Color.RED)
                    .build();
            event.getHook().editOriginalEmbeds(embed).queue();
        }

        phase1FailedMap.remove(discordUid);
    }
}
