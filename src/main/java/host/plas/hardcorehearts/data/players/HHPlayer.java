package host.plas.hardcorehearts.data.players;

import host.plas.bou.scheduling.TaskManager;
import host.plas.bou.utils.ColorUtils;
import host.plas.hardcorehearts.sql.HHDB;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import tv.quaint.objects.Identified;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Getter
public class HHPlayer implements Identified {
    private final String identifier;

    private int lives;
    @Setter
    private int deaths;
    @Setter
    private int kills;
    @Setter
    private int revives;
    private boolean deathBanned;

    @Setter
    private boolean doneAugmenting;

    @Setter
    private boolean saving;

    public HHPlayer(String identifier) {
        this.identifier = identifier;

        this.lives = 3;
        this.deaths = 0;
        this.kills = 0;
        this.revives = 0;
        this.deathBanned = false;

        this.doneAugmenting = false;
    }

    public void setDeathBanned(boolean bool) {
        this.deathBanned = bool;

        this.checkDeathBanned();
    }

    public void checkDeathBanned() {
        if (this.deathBanned) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(this.getIdentifier()));
            if (player.isOnline()) {
                Player p = player.getPlayer();
                if (p == null) return;
                TaskManager.runTask(p, () -> {
                    p.kickPlayer(ColorUtils.colorize("&cYou have been death banned as you lost all of your lives!\n\n&7&oTry to get someone to revive you!"));
                });
            }
        }
    }

    public void save() {
        HHDB.getInstance().savePlayer(this);
    }

    public void saveAndUnload() {
        this.save();
        this.unload();
    }

    public void delete() {
        HHDB.getInstance().deletePlayer(this.getIdentifier());

        this.unload();
    }

    public void load() {
        PlayerManager.loadPlayer(this);
    }

    public void unload() {
        PlayerManager.unloadPlayer(this.getIdentifier());
    }

    public void augment(CompletableFuture<Optional<HHPlayer>> future) {
        future.thenAccept(optional -> {
            if (optional.isEmpty()) {
                this.doneAugmenting = true;
                return;
            }
            HHPlayer p = optional.get();

            this.lives = p.getLives();
            this.deaths = p.getDeaths();
            this.kills = p.getKills();
            this.revives = p.getRevives();
            this.deathBanned = p.isDeathBanned();

            this.doneAugmenting = true;
        });
    }

    public void subscribeLoading(Consumer<HHPlayer> consumer, boolean async) {
        if (! async) {
            while (! this.doneAugmenting) {
                Thread.onSpinWait();
            }

            consumer.accept(this);
            return;
        } else {
            CompletableFuture.runAsync(() -> {
                while (!this.doneAugmenting) {
                    Thread.onSpinWait();
                }

                consumer.accept(this);
            });
        }
    }

    public void subscribeLoading(Consumer<HHPlayer> consumer) {
        this.subscribeLoading(consumer, true);
    }

    public int getMaxLives() {
        return 3;
    }

    public boolean setLives(int lives) {
        if (lives < 0) return false;
        if (lives > getMaxLives()) return false;

        this.lives = lives;

        this.checkLives();

        return true;
    }

    public boolean addLife() {
        int temp = this.lives;
        temp ++;
        if (lives < 0) return false;
        if (lives > getMaxLives()) return false;

        this.lives = temp;

        this.checkLives();

        return true;
    }

    public boolean removeLife() {
        int temp = this.lives;
        temp --;
        if (lives < 0) return false;
        if (lives > getMaxLives()) return false;

        this.lives = temp;

        this.checkLives();

        return true;
    }

    public void checkLives() {
        if (this.lives <= 0) {
            this.deathBanned = true;

            this.save();

            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(this.getIdentifier()));
            if (player.isOnline()) {
                Player p = player.getPlayer();
                if (p == null) return;
                TaskManager.runTask(p, () -> {
                    p.kickPlayer(ColorUtils.colorize("&cYou have been death banned as you lost all of your lives!\n\n&7&oTry to get someone to revive you!"));
                });
            }
        }
    }

    public void onDeath() {
        this.addDeath();
        if (! this.removeLife()) {
//            this.deathBanned = true;
        }
        this.save();
    }

    public void onKill() {
        this.addKill();
        this.save();
    }

    public void onRevive(Player reviver) {
        this.revive();
        this.save();

        HHPlayer self = PlayerManager.getOrCreate(reviver.getUniqueId().toString());
        self.subscribeLoading(HHPlayer::addRevive);
    }

    public void onQuit() {
        this.save();
        this.unload();
    }

    public void onJoin() {
//        this.load();
    }

    public void addDeath() {
        this.deaths ++;
    }

    public void removeDeath() {
        this.deaths --;
    }

    public void addKill() {
        this.kills ++;
    }

    public void removeKill() {
        this.kills --;
    }

    public void addRevive() {
        this.revives ++;
    }

    public void removeRevive() {
        this.revives --;
    }

    public void revive() {
        this.lives = 1;
        this.deathBanned = false;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(UUID.fromString(this.getIdentifier()));
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(UUID.fromString(this.getIdentifier()));
    }

    public boolean isOnline() {
        return this.getOfflinePlayer().isOnline() && this.getPlayer() != null;
    }

    public void ifOnline(Consumer<Player> consumer) {
        if (this.isOnline()) {
            consumer.accept(this.getPlayer());
        }
    }

    public void sendMessage(String message) {
        this.ifOnline(player -> {
            player.sendMessage(ColorUtils.colorize(message));
        });
    }
}
