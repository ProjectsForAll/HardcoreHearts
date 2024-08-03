package host.plas.hardcorehearts.data.players;

import host.plas.hardcorehearts.sql.HHDB;
import lombok.Getter;
import lombok.Setter;
import tv.quaint.objects.Identified;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Getter @Setter
public class HHPlayer implements Identified {
    private final String identifier;

    private int lives;
    private int deaths;
    private int kills;
    private int revives;
    private boolean isDeathBanned;

    private boolean doneAugmenting;

    public HHPlayer(String identifier) {
        this.identifier = identifier;

        this.lives = 3;
        this.deaths = 0;
        this.kills = 0;
        this.revives = 0;
        this.isDeathBanned = false;

        this.doneAugmenting = false;
    }

    public void save() {
        HHDB.getInstance().savePlayer(this);
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
            if (optional.isEmpty()) return;
            HHPlayer p = optional.get();

            this.lives += p.getLives();
            this.deaths += p.getDeaths();
            this.kills += p.getKills();
            this.revives += p.getRevives();
            this.isDeathBanned = p.isDeathBanned();

            this.doneAugmenting = true;
        });
    }

    public void subscribeLoading(Consumer<HHPlayer> consumer) {
        CompletableFuture.runAsync(() -> {
            while (! this.doneAugmenting) {
                Thread.onSpinWait();
            }

            consumer.accept(this);
        });
    }

    public void addLife() {
        this.lives ++;
    }

    public void removeLife() {
        this.lives --;
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
        this.lives = 3;
        this.isDeathBanned = false;
    }
}
