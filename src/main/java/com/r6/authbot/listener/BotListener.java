package com.r6.authbot.listener;

import com.r6.authbot.service.iButtonService;
import com.r6.authbot.service.iCommandService;
import com.r6.authbot.service.impl.ButtonServiceImpl;
import com.r6.authbot.service.impl.CommandServiceImpl;

import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BotListener extends ListenerAdapter {

    private iCommandService commandService = new CommandServiceImpl();
    private iButtonService buttonService = new ButtonServiceImpl();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();
        if (buttonId.equals("doAuth")) {
            buttonService.doAuth(event);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        if (commandName.equals("인증봇관리")) {
            String subCommandName = event.getSubcommandName();
            if (subCommandName.equals("초기설정")) {
                commandService.initSetting(event);
            } else if (subCommandName.equals("계정관리")) {
                commandService.editAPIAccount(event);
            } else if (subCommandName.equals("상태")) {
                commandService.botStatus(event);
            } else if (subCommandName.equals("랭커조건")) {
                commandService.setRankerCondition(event);
            } else if (subCommandName.equals("차단등록")) {
                commandService.blockUser(event);
            } else if (subCommandName.equals("차단해제")) {
                commandService.unblockUser(event);
            }
        }
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        // TODO Auto-generated method stub
        super.onChannelCreate(event);
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        // TODO Auto-generated method stub
        super.onModalInteraction(event);
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        // TODO Auto-generated method stub
        super.onStringSelectInteraction(event);
    }

    @Override
    public void onUserContextInteraction(UserContextInteractionEvent event) {
        // TODO Auto-generated method stub
        super.onUserContextInteraction(event);
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        // TODO Auto-generated method stub
        super.onGuildReady(event);
    }

    @Override
    public void onReady(ReadyEvent event) {
        // TODO Auto-generated method stub
        super.onReady(event);
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        // TODO Auto-generated method stub
        super.onGuildJoin(event);
    }
}
