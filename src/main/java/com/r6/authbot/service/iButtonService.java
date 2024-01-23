package com.r6.authbot.service;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * 디스코드 버튼클릭 이벤트 서비스
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
public interface iButtonService {

    /**
     * 인증 시작 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>ButtonInteractionEvent</b> : 버튼 인터렉션시 만들어지는 객체
     */
    public void doAuth(ButtonInteractionEvent event);

    /**
     * 재인증 시작 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>ButtonInteractionEvent</b> : 버튼 인터렉션시 만들어지는 객체
     */
    public void doReAuth(ButtonInteractionEvent event);

    /**
     * 리더보드 페이징 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.19
     * @param <b>ButtonInteractionEvent</b> : 버튼 인터렉션시 만들어지는 객체
     */
    public void pagingLeaderboard(ButtonInteractionEvent event);
}
