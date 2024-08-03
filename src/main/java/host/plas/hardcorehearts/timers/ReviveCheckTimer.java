package host.plas.hardcorehearts.timers;

import host.plas.bou.commands.Sender;
import host.plas.bou.scheduling.BaseRunnable;
import host.plas.bou.scheduling.TaskManager;
import host.plas.bou.utils.EntityUtils;
import host.plas.hardcorehearts.data.crafts.CraftingManager;
import host.plas.hardcorehearts.data.players.HHPlayer;
import host.plas.hardcorehearts.data.players.PlayerManager;
import host.plas.hardcorehearts.utils.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Getter @Setter
public class ReviveCheckTimer extends BaseRunnable {
    public ReviveCheckTimer() {
        super(0, 1);
    }

    @Override
    public void run() {
        EntityUtils.collectEntitiesThenDo(entity -> {
            if (! (entity instanceof Item)) return;
            Item item = (Item) entity;

            ItemStack stack = item.getItemStack();
            if (! ItemUtils.isItemEqual(stack, CraftingManager.getReviveCraft().getResult())) return;

            item.getNearbyEntities(1, 1, 1).stream()
                    .filter(e -> e instanceof Item)
                    .forEach(e -> {
                        Item nearbyItem = (Item) e;
                        ItemStack nearbyStack = nearbyItem.getItemStack();

                        if (nearbyStack.getType() != Material.PAPER) return;

                        String name = nearbyStack.getItemMeta().getDisplayName();

                        CompletableFuture.runAsync(() -> {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
                            if (! player.hasPlayedBefore()) return;
                            String uuid = player.getUniqueId().toString();

                            HHPlayer hhPlayer = PlayerManager.getOrCreate(uuid);
                            hhPlayer.subscribeLoading(p -> {
                                if (! p.isDeathBanned()) {
                                    TaskManager.runTask(nearbyItem, () -> {
                                        UUID thrower = nearbyItem.getThrower();
                                        if (thrower == null) return;
                                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(thrower);
                                        if (offlinePlayer.isOnline()) {
                                            Player onlinePlayer = offlinePlayer.getPlayer();
                                            if (onlinePlayer == null) return;
                                            Sender sender = new Sender(onlinePlayer);

                                            sender.sendMessage("&cThis player is not death banned!");
                                        }
                                    });

                                    return;
                                }

                                p.revive();
                                p.save();

                                TaskManager.runTask(nearbyItem, () -> {
                                    UUID thrower = nearbyItem.getThrower();
                                    if (thrower == null) return;
                                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(thrower);
                                    if (offlinePlayer.isOnline()) {
                                        Player onlinePlayer = offlinePlayer.getPlayer();
                                        if (onlinePlayer == null) return;
                                        Sender sender = new Sender(onlinePlayer);

                                        sender.sendMessage("&aYou have revived &e" + player.getName() + "&8!");

                                        HHPlayer throwerPlayer = PlayerManager.getOrCreate(thrower.toString());
                                        throwerPlayer.subscribeLoading(tp -> {
                                            tp.addRevive();
                                            tp.save();
                                        });
                                    }
                                });

                                TaskManager.runTask(item, item::remove);
                                TaskManager.runTask(nearbyItem, nearbyItem::remove);
                            });
                        });
                    });
        });
    }
}
