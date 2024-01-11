package com.r6.authbot.configure;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.r6.authbot.enums.APIConfig;
import com.r6.authbot.enums.BotConfig;
import com.r6.authbot.listener.BotListener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.Compression;

/**
 * JDA 빌드 클래스
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
public class BuildJDA {

    public BuildJDA() {
        build();
    }

    private void setProperties() throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties");
        Properties properties = new Properties();
        properties.load(inputStream);

        APIConfig.API_ACCOUNT_USERNAME.set(properties.getProperty("API_ACCOUNT_USERNAME"));
        APIConfig.API_ACCOUNT_PASSWORD.set(properties.getProperty("API_ACCOUNT_PASSWORD"));

        BotConfig.DB_URL.setStrVal(properties.getProperty("DB_URL"));
        BotConfig.DB_USERNAME.setStrVal(properties.getProperty("DB_USERNAME"));
        BotConfig.DB_PASSWORD.setStrVal(properties.getProperty("DB_PASSWORD"));
        BotConfig.BOT_TOKEN.setStrVal(properties.getProperty("BOT_TOKEN"));
        BotConfig.AUTH_CHANNEL_ID.setStrVal(properties.getProperty("AUTH_CHANNEL_ID"));
        BotConfig.LEADERBOARD_CHANNEL_ID.setStrVal(properties.getProperty("LEADERBOARD_CHANNEL_ID"));
        BotConfig.SPECIAL_ROLE_ID.setStrVal(properties.getProperty("SPECIAL_ROLE_ID"));
        inputStream.close();
    }

    private void initCommands(JDA jda) {
        jda.updateCommands()
                .addCommands(Commands.slash("인증봇관리", "인증봇 관리를 위한 명령어 입니다.")
                        .setGuildOnly(true)
                        .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS))
                        .addSubcommands(
                                new SubcommandData("초기설정", "인증임베드와 리더보드 임베드를 생성합니다."),
                                new SubcommandData("상태", "현재 인증봇의 상태를 표시합니다."),
                                new SubcommandData("랭커조건", "랭커인지 판단하는 MMR값을 설정합니다. (기본값 4700)")
                                        .addOption(OptionType.INTEGER, "min_mmr", "최소 MMR 값", true),
                                new SubcommandData("계정관리", "API요청 계정 정보를 설정합니다.")
                                        .addOption(OptionType.STRING, "username", "API요청용 계정의 아이디", true)
                                        .addOption(OptionType.STRING, "password", "API요청용 계정의 비밀번호", true),
                                new SubcommandData("차단해제", "유저의 인증시스템 이용 차단을 해제합니다.")
                                        .addOption(OptionType.STRING, "uid", "차단을 해제하고자 하는 유저의 디스코드 UID", true),
                                new SubcommandData("차단등록", "유저의 인증시스템 이용을 차단합니다.")
                                        .addOption(OptionType.STRING, "uid", "차단하고자 하는 유저의 디스코드 UID", true)
                                        .addOption(OptionType.STRING, "reason", "유저의 차단 이유", true)
                                        .addOption(OptionType.INTEGER, "day", "일 (0: 없음)", true)
                                        .addOption(OptionType.INTEGER, "hour", "시간 (0: 없음)", true)
                                        .addOption(OptionType.INTEGER, "minute", "분 (0: 없음)", true)))
                .queue();
    }

    private void build() {
        try {
            setProperties();
            JDA jda = JDABuilder.createDefault(BotConfig.BOT_TOKEN.getStrVal())
                    .setBulkDeleteSplittingEnabled(true)
                    .setCompression(Compression.NONE)
                    .setChunkingFilter(ChunkingFilter.NONE)
                    .setStatus(OnlineStatus.ONLINE)
                    .setActivity(Activity.playing("계정인증"))
                    .setAutoReconnect(true)
                    .addEventListeners(new BotListener())
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .build()
                    .awaitReady();
            initCommands(jda);
        } catch (Exception ex) {
            System.out.println("Failed to build JDA");
            ex.printStackTrace();
        }
    }
}
