package com.r6.authbot.service;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

/**
 * 디스코드 슬래시 커맨드 이벤트 서비스
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
public interface iCommandService {

    /**
     * 초기설정 커맨드 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>SlashCommandInteractionEvent</b> : 슬래시 커맨드 사용시 만들어지는 이벤트 객체
     */
    public void initSetting(SlashCommandInteractionEvent event);

    /**
     * API요청용 계정 정보 수정 커맨드 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>SlashCommandInteractionEvent</b> : 슬래시 커맨드 사용시 만들어지는 이벤트 객체
     */
    public void editAPIAccount(SlashCommandInteractionEvent event);

    /**
     * 봇 상태 조회 커맨드 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>SlashCommandInteractionEvent</b> : 슬래시 커맨드 사용시 만들어지는 이벤트 객체
     */
    public void botStatus(SlashCommandInteractionEvent event);

    /**
     * 랭커 조건 수정 커맨드 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>SlashCommandInteractionEvent</b> : 슬래시 커맨드 사용시 만들어지는 이벤트 객체
     */
    public void setRankerCondition(SlashCommandInteractionEvent event);

    /**
     * 유저의 인증봇 이용 차단 커맨드 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>SlashCommandInteractionEvent</b> : 슬래시 커맨드 사용시 만들어지는 이벤트 객체
     */
    public void blockUser(SlashCommandInteractionEvent event);

    /**
     * 유저의 인증봇 이용 차단 해제 커맨드 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>SlashCommandInteractionEvent</b> : 슬래시 커맨드 사용시 만들어지는 이벤트 객체
     */
    public void unblockUser(SlashCommandInteractionEvent event);
}