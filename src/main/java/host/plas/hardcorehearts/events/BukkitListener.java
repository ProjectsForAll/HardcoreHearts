package host.plas.hardcorehearts.events;

import host.plas.bou.utils.ColorUtils;
import host.plas.hardcorehearts.HardcoreHearts;
import host.plas.hardcorehearts.data.players.HHPlayer;
import host.plas.hardcorehearts.data.players.PlayerManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener implements Listener {
    public BukkitListener() {
        Bukkit.getPluginManager().registerEvents(this, HardcoreHearts.getInstance());
        HardcoreHearts.getInstance().logInfo("Registered MainListener!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        HHPlayer hhPlayer = PlayerManager.getOrCreate(player.getUniqueId().toString());
        hhPlayer.subscribeLoading(p -> {
            if (p.isDeathBanned()) {
                player.kickPlayer(ColorUtils.colorize("&cYou are death banned!\n\n&7&oTry to get someone to revive you!"));
            }
        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        HHPlayer hhPlayer = PlayerManager.getOrCreate(player.getUniqueId().toString());

        hhPlayer.subscribeLoading(p -> {
            p.save();
            p.unload();
        });
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        HHPlayer hhPlayer = PlayerManager.getOrCreate(player.getUniqueId().toString());

        hhPlayer.subscribeLoading(p -> {
            p.addDeath();
            p.save();
        });
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        HHPlayer hhPlayer = PlayerManager.getOrCreate(player.getUniqueId().toString());

        hhPlayer.subscribeLoading(p -> {
            p.addKill();
            p.save();
        });
    }
}
