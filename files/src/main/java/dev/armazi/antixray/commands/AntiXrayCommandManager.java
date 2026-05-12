package dev.armazi.antixray.commands;

import dev.armazi.antixray.ArmaziAntiXray;
import dev.armazi.antixray.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Main command manager with subcommand routing
 */
public class AntiXrayCommandManager implements CommandExecutor, TabCompleter {
    
    private final ArmaziAntiXray plugin;
    private final Map<String, BaseCommand> subcommands;

    public AntiXrayCommandManager(ArmaziAntiXray plugin) {
        this.plugin = plugin;
        this.subcommands = new HashMap<>();
        registerSubcommands();
    }

    private void registerSubcommands() {
        subcommands.put("reload", new ReloadCommand(plugin));
        subcommands.put("help", new HelpCommand(plugin));
        subcommands.put("toggle", new ToggleCommand(plugin));
        subcommands.put("mode", new ModeCommand(plugin));
        subcommands.put("debug", new DebugCommand(plugin));
        subcommands.put("stats", new StatsCommand(plugin));
        subcommands.put("inspect", new InspectCommand(plugin));
        subcommands.put("alerts", new AlertsCommand(plugin));
        subcommands.put("bypass", new BypassCommand(plugin));
        subcommands.put("fakeores", new FakeOresCommand(plugin));
        subcommands.put("detections", new DetectionsCommand(plugin));
        subcommands.put("profile", new ProfileCommand(plugin));
        subcommands.put("violations", new ViolationsCommand(plugin));
        subcommands.put("logs", new LogsCommand(plugin));
        subcommands.put("test", new TestCommand(plugin));
        subcommands.put("benchmark", new BenchmarkCommand(plugin));
        subcommands.put("chunkinfo", new ChunkInfoCommand(plugin));
        subcommands.put("status", new StatusCommand(plugin));
    }

    public void register() {
        plugin.getCommand("aax").setExecutor(this);
        plugin.getCommand("aax").setTabCompleter(this);
        plugin.getCommand("azantixray").setExecutor(this);
        plugin.getCommand("azantixray").setTabCompleter(this);
        plugin.getCommand("armaziantixray").setExecutor(this);
        plugin.getCommand("armaziantixray").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String subcommandName = args[0].toLowerCase();
        BaseCommand subcommand = subcommands.get(subcommandName);

        if (subcommand == null) {
            sender.sendMessage("§cUnknown subcommand. Use /aax help");
            return true;
        }

        if (!sender.hasPermission(subcommand.getPermission())) {
            String message = plugin.getConfigManager().getConfig("messages.yml")
                .getString("commands.no-permission", "§cYou don't have permission.");
            sender.sendMessage(message);
            return true;
        }

        try {
            subcommand.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        } catch (Exception e) {
            sender.sendMessage("§cError executing command: " + e.getMessage());
            plugin.getPluginLogger().error("Error executing command: " + e.getMessage());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return subcommands.keySet().stream()
                .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                .filter(cmd -> sender.hasPermission("armaziantixray.admin"))
                .toList();
        }

        BaseCommand subcommand = subcommands.get(args[0].toLowerCase());
        if (subcommand != null) {
            return subcommand.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        return new ArrayList<>();
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("§c§l════════════════════════════════════");
        sender.sendMessage("§b§lArmaziAntiXray §f§lHelp Menu");
        sender.sendMessage("§c§l════════════════════════════════════");
        subcommands.forEach((name, cmd) -> {
            if (sender.hasPermission(cmd.getPermission())) {
                sender.sendMessage("§b/aax " + name + " §f- " + cmd.getDescription());
            }
        });
        sender.sendMessage("§c§l════════════════════════════════════");
    }
}