package com.la.jsmod.commands;

import com.la.jsmod.JSMod;
import com.la.jsmod.ScriptLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class HotReloadCommand extends CommandBase {
    @Override
    public String getName() {
        return "hotreload";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/hotreload";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(TextFormatting.GOLD + "Hot Reload..."));
        ScriptLoader.instance.requestHotReload();
    }
}
