package host.plas.hardcorehearts.commands;

import host.plas.bou.commands.CommandContext;
import host.plas.bou.commands.SimplifiedCommand;
import host.plas.hardcorehearts.HardcoreHearts;
import host.plas.hardcorehearts.data.players.HHPlayer;
import host.plas.hardcorehearts.data.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.atomic.AtomicInteger;

public class SetLivesCMD extends SimplifiedCommand {
    public SetLivesCMD() {
        super("hhsetlives", HardcoreHearts.getInstance());
    }

    @Override
    public boolean command(CommandContext ctx) {
        if (! ctx.isArgUsable(1)) {
            ctx.sendMessage("&cUsage: /hhsetlives <player> <lives>");
            return false;
        }

        String name = ctx.getStringArg(0);
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (! player.hasPlayedBefore()) {
            ctx.sendMessage("&cPlayer not found.");
            return false;
        }

        AtomicInteger amount = new AtomicInteger(3);
        ctx.getIntArg(1).ifPresent(amount::set);

        HHPlayer hhPlayer = PlayerManager.getOrCreate(player.getUniqueId().toString());
        hhPlayer.delete();

        hhPlayer = PlayerManager.getOrCreate(player.getUniqueId().toString());
        hhPlayer.subscribeLoading(p -> {
            if (! p.setLives(amount.get())) {
                if (amount.get() < 0) {
                    ctx.sendMessage("&cLives cannot be negative.");
                } else if (amount.get() > p.getMaxLives()) {
                    ctx.sendMessage("&cLives cannot be greater than the max amount of &a" + p.getMaxLives() + "&c.");
                }
            }

            ctx.sendMessage("&ePlayer&7'&es lives set to &a" + amount.get() + " &efor &c" + player.getName() + "&8.");
        });

        return true;
    }
}
