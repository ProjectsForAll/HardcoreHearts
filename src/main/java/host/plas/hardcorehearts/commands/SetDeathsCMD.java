package host.plas.hardcorehearts.commands;

import host.plas.bou.commands.CommandContext;
import host.plas.bou.commands.SimplifiedCommand;
import host.plas.hardcorehearts.HardcoreHearts;
import host.plas.hardcorehearts.data.players.HHPlayer;
import host.plas.hardcorehearts.data.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.concurrent.atomic.AtomicInteger;

public class SetDeathsCMD extends SimplifiedCommand {
    public SetDeathsCMD() {
        super("hhsetdeaths", HardcoreHearts.getInstance());
    }

    @Override
    public boolean command(CommandContext ctx) {
        if (! ctx.isArgUsable(1)) {
            ctx.sendMessage("&cUsage: /hhsetdeaths <player> <deaths>");
            return false;
        }

        String name = ctx.getStringArg(0);
        OfflinePlayer player = Bukkit.getOfflinePlayer(name);
        if (! player.hasPlayedBefore()) {
            ctx.sendMessage("&cPlayer not found.");
            return false;
        }

        AtomicInteger amount = new AtomicInteger(0);
        ctx.getIntArg(1).ifPresent(amount::set);

        HHPlayer hhPlayer = PlayerManager.getOrCreate(player.getUniqueId().toString());
        hhPlayer.delete();

        hhPlayer = PlayerManager.getOrCreate(player.getUniqueId().toString());
        hhPlayer.subscribeLoading(p -> {
            p.setDeaths(amount.get());

            ctx.sendMessage("&ePlayer&7'&es deaths set to &a" + amount.get() + " &efor &c" + player.getName() + "&8.");
        });

        return true;
    }
}
