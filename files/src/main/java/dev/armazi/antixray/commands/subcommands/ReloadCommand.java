package dev.armazi.antixray.commands.subcommands;

import dev.armazi.antixray.ArmaziAntiXray;
import dev.armazi.antixray.commands.BaseCommand;
import org.bukkit.command.CommandSender;

import java.io.IOException;

/**
 * /aax reload - Reload all configuration files
 */
public class ReloadCommand extends BaseCommand {

    public ReloadCommand(ArmaziAntiXray plugin) {
        super(plugin);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            plugin.getConfigManager().reloadAllConfigs();
            String message = plugin.getConfigManager().getConfig("messages.yml")
                .getString("commands.reload-success", "§aConfiguration reloaded!");
            sender.sendMessage(message);
        } catch (IOException e) {
            sender.sendMessage("§cError reloading configuration: " + e.getMessage());
            plugin.getPluginLogger().error("Error reloading config: " + e.getMessage());
        }
    }

    @Override
    public String getPermission() {
        return "armaziantixray.reload";
    }

    @Override
    public String getDescription() {
        return "Reload all configuration files";
    }
}