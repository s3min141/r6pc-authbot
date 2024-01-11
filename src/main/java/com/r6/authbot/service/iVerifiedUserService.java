package com.r6.authbot.service;

import com.r6.authbot.domain.VerifiedUser;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

/**
 * 인증 유저 관련 서비스
 * <hr/>
 * 
 * @author 세민
 * @version 1.0
 * @since 2024.01.11
 */
public interface iVerifiedUserService {

    /**
     * 유저를 인증된 유저 DB에 등록
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>ButtonInteractionEvent</b> : 디스코드 버튼 클릭시 생성되는 이벤트 객체
     * @param <b>VerifiedUser</b> : 등록할 유저의 정보를 담고 있는 객체
     */
    public void registerToVerifiedUser(ButtonInteractionEvent event, VerifiedUser userInfo);

    /**
     * 디스코드 UID로 인증된 유저 DB에서 정보 가져오는 함수
     * <hr/>
     * 
     * @author 세민
     * @version 1.0
     * @since 2024.01.11
     * @param <b>String</b> : 디스코드 UID
     * @return <b>VerifiedUser</b> 디스코드 UID로 인증된 유저 목록에서 정보를 가져와 있다면 VerifiedUser객체 없다면 null을 반환
     */
    public VerifiedUser getByDiscordId(String discordUid);
}
