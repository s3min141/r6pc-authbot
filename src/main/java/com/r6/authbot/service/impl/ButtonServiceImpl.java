package com.r6.authbot.service.impl;

import java.awt.Color;
import java.io.InputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.time.LocalDateTime;

import com.r6.authbot.domain.AuthBanInfo;
import com.r6.authbot.domain.RegisterAuthBan;
import com.r6.authbot.domain.UbisoftProfile;
import com.r6.authbot.domain.UserRankInfo;
import com.r6.authbot.domain.VerifiedUser;
import com.r6.authbot.enums.BotConfig;
import com.r6.authbot.service.iAuthBanService;
import com.r6.authbot.service.iButtonService;
import com.r6.authbot.service.iUbisoftService;
import com.r6.authbot.service.iVerifiedUserService;
import com.r6.authbot.util.LeaderboardUtil;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

public class ButtonServiceImpl implements iButtonService {

    private iUbisoftService ubisoftService = new UbisoftServiceImpl();
    private iVerifiedUserService verifiedUserService = new VerifiedUserServiceImpl();
    private iAuthBanService authBanService = new AuthBanServiceImpl();

    private HashMap<String, Integer> phase1FailedMap = new HashMap<>();
    private HashMap<String, Integer> phase2FailedMap = new HashMap<>();

    private Integer leaderboardIndex = 0;
    private Long leaderboardLastInteracted;

    @Override
    public void doAuth(ButtonInteractionEvent event) {
        String discordUid = event.getUser().getId();
        VerifiedUser verifiedInfo = verifiedUserService.getByDiscordUid(discordUid);
        AuthBanInfo banInfo = authBanService.checkBanInfo(discordUid);

        if (banInfo != null) {
            event.deferReply().queue();

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("유비소프트 계정 인증")
                    .setDescription(String.format("%s 이후에 재인증이 가능합니다.", banInfo.getEndDate()))
                    .addField("사유", banInfo.getBanReason(), false)
                    .setColor(Color.RED)
                    .build();
            event.getHook().editOriginalEmbeds(embed).queue();
            return;
        }

        if (verifiedInfo != null) {
            event.deferReply().setEphemeral(true).queue();

            String ubisoftUid = ubisoftService.getUserIdByDiscordUid(discordUid);
            UbisoftProfile userProfile = ubisoftService.getProfileById(ubisoftUid);

            if (!verifiedInfo.getUbisoftUid().equals(userProfile.getNameOnPlatform())) {
                MessageEmbed authEmbed = new EmbedBuilder()
                        .setTitle("유비소프트 계정 인증")
                        .setDescription(String.format(
                                "이미 %s 계정으로 연동되어있습니다. 기존 연동을 취소하고 %s 으로 다시 연동하시겠습니까?\n\n** 30초뒤에 자동 취소됩니다 **",
                                verifiedInfo.getUbisoftUname(), userProfile.getNameOnPlatform()))
                        .setColor(Color.BLUE)
                        .build();

                MessageEditData messageEditData = new MessageEditBuilder()
                        .setEmbeds(authEmbed)
                        .setActionRow(
                                Button.of(ButtonStyle.PRIMARY, String.format("doReAuth-%s", discordUid),
                                        "네, 다시 연동합니다."))
                        .build();

                event.getHook().setEphemeral(true).editOriginal(messageEditData)
                        .queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
                return;
            }
        }

        if (!event.isAcknowledged()) {
            event.deferReply().queue();
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

        UserRankInfo userRankInfo = ubisoftService.getUserRankInfo(userProfile.getUserId());
        Boolean isRanker = userRankInfo.getMmr() >= BotConfig.MIN_RANKER_MMR.getIntVal();

        if (isRanker) {
            VerifiedUser registerUserInfo = new VerifiedUser(discordUid, ubisoftUid, userProfile.getNameOnPlatform(),
                    userRankInfo.getMmr(), userRankInfo.getKills(), userRankInfo.getWins());
            verifiedUserService.registerToVerifiedUser(event, registerUserInfo);

            Role specialRole = event.getGuild().getRoleById(BotConfig.SPECIAL_ROLE_ID.getStrVal());
            event.getGuild().addRoleToMember(event.getMember(), specialRole).queue();

            MessageEmbed embed = new EmbedBuilder()
                    .setTitle("유비소프트 계정 인증")
                    .setDescription("계정 연동과 TopPlayer 조건 일치가 확인되어 역할이 정상 부여되었습니다.")
                    .addField("유비소프트 계정", userProfile.getNameOnPlatform(), true)
                    .addField("디스코드 계정", String.format("<@%s>", discordUid), true)
                    .addField("최근 MMR", userRankInfo.getMmr().toString(), true)
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

    @Override
    public void pagingLeaderboard(ButtonInteractionEvent event) {
        Long currentTime = System.currentTimeMillis();
        if (leaderboardLastInteracted != null && currentTime - leaderboardLastInteracted < 5000) { //5초 쿨다운 적용
            MessageEmbed errorEmbed = new EmbedBuilder()
                    .setTitle("R6PC 리더보드")
                    .setDescription("쿨다운이 적용되어있습니다.")
                    .setColor(new Color(139, 31, 59))
                    .build();
            event.replyEmbeds(errorEmbed).setEphemeral(true)
                    .queue(createdMessage -> createdMessage.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return;
        }
        leaderboardLastInteracted = currentTime;

        String actionString = event.getButton().getId().replace("leaderboardPaging", "");
        ArrayList<File> leaderboardImgList = BotConfig.LEADERBOARD_IMGS.getArrayVal();
        Integer tempIndex = leaderboardIndex;

        if (leaderboardImgList == null) {
            return;
        }

        if (actionString.equals("First")) {
            tempIndex = 0;
        } else if (actionString.equals("Prev")) {
            tempIndex--;
            if (tempIndex < 0) {
                MessageEmbed errorEmbed = new EmbedBuilder()
                        .setTitle("R6PC 리더보드")
                        .setDescription("최소 페이지에 도달했습니다.")
                        .setColor(new Color(139, 31, 59))
                        .build();
                event.replyEmbeds(errorEmbed).setEphemeral(true)
                        .queue(createdMessage -> createdMessage.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
                return;
            }
        } else if (actionString.equals("Next")) {
            tempIndex++;
            if (tempIndex >= leaderboardImgList.size()) {
                MessageEmbed errorEmbed = new EmbedBuilder()
                        .setTitle("R6PC 리더보드")
                        .setDescription("최대 페이지에 도달했습니다.")
                        .setColor(new Color(139, 31, 59))
                        .build();
                event.replyEmbeds(errorEmbed).setEphemeral(true)
                        .queue(createdMessage -> createdMessage.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
                return;
            }
        } else if (actionString.equals("Last")) {
            tempIndex = leaderboardImgList.size() - 1;
        }
        leaderboardIndex = tempIndex;

        InputStream leaderboardImgStream = LeaderboardUtil.getLeaderboardImg(leaderboardIndex);

        MessageEmbed leaderboardEmbed = new EmbedBuilder()
                .setTitle("R6PC 리더보드")
                .setDescription("** 인증시스템을 통해 인증된 유저들중 상위 유저들을 표시합니다. **")
                .setImage("attachment://leaderboard.png")
                .setColor(new Color(139, 31, 59))
                .build();

        if (leaderboardImgStream == null) {
            leaderboardEmbed = new EmbedBuilder()
                    .setTitle("R6PC 리더보드")
                    .setDescription("## 이미지 로딩중 오류가 발생했습니다.")
                    .setColor(new Color(139, 31, 59))
                    .build();
        }

        MessageEditCallbackAction editAction = event.editMessageEmbeds(leaderboardEmbed);
        if (leaderboardImgStream != null) {
            if (BotConfig.LEADERBOARD_IMGS.getArrayVal().size() == 1) {
                editAction.setActionRow(
                        Button.secondary("leaderboardPagingFirst",
                                Emoji.fromUnicode("⏪")).asDisabled(),
                        Button.secondary("leaderboardPagingPrev",
                                Emoji.fromUnicode("◀️")),
                        Button.secondary("leaderboardPagingRefresh",
                                Emoji.fromUnicode("🔄")),
                        Button.secondary("leaderboardPagingNext",
                                Emoji.fromUnicode("▶️")),
                        Button.secondary("leaderboardPagingLast",
                                Emoji.fromUnicode("⏩")).asDisabled());
            } else {
                editAction.setActionRow(
                        Button.secondary("leaderboardPagingFirst",
                                Emoji.fromUnicode("⏪")),
                        Button.secondary("leaderboardPagingPrev",
                                Emoji.fromUnicode("◀️")),
                        Button.secondary("leaderboardPagingRefresh",
                                Emoji.fromUnicode("🔄")),
                        Button.secondary("leaderboardPagingNext",
                                Emoji.fromUnicode("▶️")),
                        Button.secondary("leaderboardPagingLast",
                                Emoji.fromUnicode("⏩")));
            }
            editAction.setFiles(FileUpload.fromData(leaderboardImgStream, "leaderboard.png"));
        }
        editAction.queue();
    }
}
