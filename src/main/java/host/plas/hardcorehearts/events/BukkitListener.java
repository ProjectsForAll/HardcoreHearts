package host.plas.hardcorehearts.events;

import host.plas.bou.commands.Sender;
import host.plas.bou.utils.ColorUtils;
import host.plas.hardcorehearts.HardcoreHearts;
import host.plas.hardcorehearts.data.crafts.CraftingManager;
import host.plas.hardcorehearts.data.players.HHPlayer;
import host.plas.hardcorehearts.data.players.PlayerManager;
import host.plas.hardcorehearts.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class BukkitListener implements Listener {
    public BukkitListener() {
        Bukkit.getPluginManager().registerEvents(this, HardcoreHearts.getInstance());
        HardcoreHearts.getInstance().logInfo("Registered MainListener!");
    }

    @EventHandler
    public void onPreJoin(PlayerPreLoginEvent event) {
        String uuid = event.getUniqueId().toString();

        HHPlayer hhPlayer = PlayerManager.getOrCreate(uuid);
        hhPlayer.subscribeLoading(p -> {
            if (p.isDeathBanned()) {
                event.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, ColorUtils.colorize("&cYou are death banned!\n\n&7&oTry to get someone to revive you!"));
                HardcoreHearts.getInstance().logInfo("Player " + event.getName() + " is death banned... Disallowing their join.");
            }
        }, false);
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
        hhPlayer.subscribeLoading(HHPlayer::onQuit);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        HHPlayer hhPlayer = PlayerManager.getOrCreate(player.getUniqueId().toString());
        hhPlayer.subscribeLoading(HHPlayer::onDeath);
    }

    @EventHandler
    public void onKill(PlayerDeathEvent event) {
        Player player = event.getEntity().getKiller();
        if (player == null) return;

        if (player.equals(event.getEntity())) return;

        HHPlayer hhPlayer = PlayerManager.getOrCreate(player.getUniqueId().toString());
        hhPlayer.subscribeLoading(HHPlayer::onKill);
    }

    @EventHandler
    private void onClickEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getItemMeta() == null) return;

        Entity entity = event.getRightClicked();
        String name = entity.getName();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
        if (! offlinePlayer.hasPlayedBefore()) {
            Sender sender = new Sender(player);
            sender.sendMessage("&cThis player has never played before...!");
            return;
        }

        ItemStack hand = player.getInventory().getItemInMainHand();
        boolean isReviveItem = ItemUtils.isItemEqual(hand, CraftingManager.getReviveCraft().getResult());
        boolean isLifeItem = ItemUtils.isItemEqual(hand, CraftingManager.getLifeCraft().getResult());

        Sender sender = new Sender(player);

        HHPlayer hhPlayer = PlayerManager.getOrCreate(offlinePlayer.getUniqueId().toString());
        hhPlayer.subscribeLoading(p -> {
            boolean isSelf = false;
            if (p.getIdentifier().equals(player.getUniqueId().toString())) {
                isSelf = true;
            }

            if (isReviveItem) {
                if (p.isDeathBanned() && ! isSelf) {
                    p.onRevive(player);
                    sender.sendMessage("&aYou have successfully &2&lrevived &c" + offlinePlayer.getName() + "&8!");
                } else {
                    String playerHolder = "This player is";
                    if (isSelf) {
                        playerHolder = "You are";
                    }

                    sender.sendMessage("&c" + playerHolder + " not death banned!");
                }
            }

            if (isLifeItem) {
                if (! p.addLife()) {
                    sender.sendMessage("&c" + (isSelf ? "You have" : "This player has") + " reached the maximum amount of lives!");
                    return;
                }
                p.save();
                if (! isSelf) {
                    sender.sendMessage("&aYou have successfully granted &c" + offlinePlayer.getName() + " &aan &2&lextra &alife&8!");
                }
                p.sendMessage("&aYou have been granted an &2&lextra &alife&8!");
            }
        });
    }
}
