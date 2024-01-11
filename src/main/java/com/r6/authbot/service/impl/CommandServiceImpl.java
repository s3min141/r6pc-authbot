package com.r6.authbot.service.impl;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.r6.authbot.domain.AuthBanInfo;
import com.r6.authbot.domain.RegisterAuthBan;
import com.r6.authbot.enums.APIConfig;
import com.r6.authbot.enums.BotConfig;
import com.r6.authbot.service.iAuthBanService;
import com.r6.authbot.service.iCommandService;
import com.r6.authbot.service.iUbisoftService;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class CommandServiceImpl implements iCommandService {

        private iUbisoftService ubisoftService = new UbisoftServiceImpl();
        private iAuthBanService authBanService = new AuthBanServiceImpl();

        @Override
        public void initSetting(SlashCommandInteractionEvent event) {
                MessageEmbed resultEmbed = null;

                try {
                        // Auth Embed
                        MessageEmbed authEmbed = new EmbedBuilder()
                                        .setTitle("유비소프트 계정 인증")
                                        .setDescription("계정 인증 진행을 위해 ** 인증하기 **✅ 버튼을 눌러주세요.")
                                        .setColor(Color.BLUE)
                                        .build();

                        MessageCreateData messageCreateData = new MessageCreateBuilder()
                                        .setEmbeds(authEmbed)
                                        .addActionRow(
                                                        Button.link("https://account.ubisoft.com/ko-KR/account-information",
                                                                        "계정 연결하기"),
                                                        Button.of(ButtonStyle.PRIMARY, "doAuth", "인증하기 ✅"))
                                        .build();

                        MessageChannelUnion authChannel = event.getGuild().getChannelById(MessageChannelUnion.class,
                                        BotConfig.AUTH_CHANNEL_ID.getStrVal());
                        authChannel.sendMessage(messageCreateData).queue();

                        // Leaderboard Embed
                        // TBD

                        // Result Embed
                        resultEmbed = new EmbedBuilder()
                                        .setTitle("R6PC 인증봇 초기설정")
                                        .setDescription("작업을 성공적으로 완료했습니다.")
                                        .setColor(Color.GREEN)
                                        .build();
                } catch (Exception ex) {
                        // Result Embed
                        resultEmbed = new EmbedBuilder()
                                        .setTitle("R6PC 인증봇 초기설정")
                                        .setDescription("초기설정에 실패했습니다.")
                                        .setColor(Color.RED)
                                        .build();
                        ex.printStackTrace();
                }

                event.replyEmbeds(resultEmbed).setEphemeral(true).queue();
        }

        @Override
        public void editAPIAccount(SlashCommandInteractionEvent event) {
                String accountUsername = event.getOption("username").getAsString();
                String accountPassword = event.getOption("password").getAsString();

                if (accountUsername.isBlank() || accountPassword.isBlank()) {
                        MessageEmbed embed = new EmbedBuilder()
                                        .setTitle("R6PC 인증봇 API요청 계정 관리")
                                        .setDescription("아이디 또는 비밀번호가 공백입니다.")
                                        .setColor(Color.RED)
                                        .build();
                        event.replyEmbeds(embed).queue();
                        return;
                }

                APIConfig.API_ACCOUNT_USERNAME.set(accountUsername);
                APIConfig.API_ACCOUNT_PASSWORD.set(accountPassword);

                MessageEmbed embed = new EmbedBuilder()
                                .setTitle("R6PC 인증봇 API요청 계정 관리")
                                .setDescription("API요청 계정을 성공적으로 아래와 같이 변경하였습니다")
                                .addField("아이디", accountUsername, true)
                                .addField("비밀번호", accountPassword, true)
                                .setColor(Color.GREEN)
                                .build();
                event.replyEmbeds(embed).queue();
        }

        @Override
        public void botStatus(SlashCommandInteractionEvent event) {
                Boolean ticketStatus = ubisoftService.isTicketValid();
                MessageEmbed embed = new EmbedBuilder()
                                .setTitle("R6PC 인증봇 상태")
                                .setDescription("현재 봇의 상태는 아래와 같습니다.")
                                .addField("API요청 세션 " + (ticketStatus ? "🟢" : "🔴"),
                                                "세션아이디: " + APIConfig.API_SESSION_ID.get(), true)
                                .addField("API 요청 계정",
                                                String.format("%s || %s", APIConfig.API_ACCOUNT_USERNAME.get(),
                                                                APIConfig.API_ACCOUNT_PASSWORD.get()),
                                                true)
                                .setColor(Color.GRAY)
                                .build();
                event.replyEmbeds(embed).queue();
        }

        @Override
        public void setRankerCondition(SlashCommandInteractionEvent event) {
                Integer minMMR = event.getOption("min_mmr").getAsInt();
                BotConfig.MIN_RANKER_MMR.setIntVal(minMMR);

                MessageEmbed embed = new EmbedBuilder()
                                .setTitle("랭커조건 수정")
                                .setDescription(String.format("랭커 조건을 ** %d MMR **로 변경하였습니다.", minMMR))
                                .setColor(Color.GREEN)
                                .build();
                event.replyEmbeds(embed).queue();
        }

        @Override
        public void blockUser(SlashCommandInteractionEvent event) {
                String discordUid = event.getOption("uid").getAsString();
                String banReason = event.getOption("reason").getAsString();

                Integer day = event.getOption("day").getAsInt();
                Integer hour = event.getOption("hour").getAsInt();
                Integer minute = event.getOption("minute").getAsInt();

                Member targetUser = event.getGuild().getMemberById(discordUid);
                if (targetUser == null) {
                        MessageEmbed embed = new EmbedBuilder()
                                        .setTitle("R6PC 인증봇 유저 차단")
                                        .setDescription("존재하지않는 유저입니다.")
                                        .setColor(Color.RED)
                                        .build();
                        event.replyEmbeds(embed).queue();
                        return;
                }

                RegisterAuthBan registerBan = new RegisterAuthBan(discordUid, banReason, day, hour, minute);
                AuthBanInfo createdBanInfo = authBanService.registerAuthBan(registerBan);
                MessageEmbed embed = new EmbedBuilder()
                                .setTitle("R6PC 인증봇 유저 차단")
                                .setDescription("유저 차단에 실패했습니다.")
                                .setColor(Color.RED)
                                .build();

                if (createdBanInfo != null) {
                        embed = new EmbedBuilder()
                                        .setTitle("R6PC 인증봇 유저 차단")
                                        .setThumbnail(targetUser.getEffectiveAvatarUrl())
                                        .setDescription("성공적으로 유저를 차단했습니다.")
                                        .addField("디스코드 UID", discordUid, true)
                                        .addField("디스코드 아이디", targetUser.getUser().getName(), true)
                                        .addField("사유", banReason, false)
                                        .addField("차단 해제 일시", createdBanInfo.getEndDate(), true)
                                        .setFooter("차단일시: " + createdBanInfo.getStartDate())
                                        .setColor(Color.GREEN)
                                        .build();
                }
                event.replyEmbeds(embed).queue();
        }

        @Override
        public void unblockUser(SlashCommandInteractionEvent event) {
                String discordUid = event.getOption("uid").getAsString();

                Member targetUser = event.getGuild().getMemberById(discordUid);
                if (targetUser == null) {
                        MessageEmbed embed = new EmbedBuilder()
                                        .setTitle("R6PC 인증봇 유저 차단 해제")
                                        .setDescription("존재하지않는 유저입니다.")
                                        .setColor(Color.RED)
                                        .build();
                        event.replyEmbeds(embed).queue();
                        return;
                }

                Boolean result = authBanService.unRegisterAuthBan(discordUid);

                SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = new Date();
                MessageEmbed embed = new EmbedBuilder()
                                .setTitle("R6PC 인증봇 유저 차단 해제")
                                .setThumbnail(targetUser.getEffectiveAvatarUrl())
                                .setDescription("성공적으로 유저의 차단을 해제했습니다.")
                                .addField("디스코드 UID", discordUid, true)
                                .addField("디스코드 아이디", targetUser.getUser().getName(), true)
                                .setFooter("차단 해제 일시: " + sdformat.format(startDate.getTime()))
                                .setColor(Color.GREEN)
                                .build();

                if (!result) {
                        embed = new EmbedBuilder()
                                        .setTitle("R6PC 인증봇 유저 차단 해제")
                                        .setDescription("유저 차단 해제에 실패했습니다.")
                                        .setColor(Color.RED)
                                        .build();
                }
                event.replyEmbeds(embed).queue();
        }
}
