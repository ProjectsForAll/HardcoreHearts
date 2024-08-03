package host.plas.hardcorehearts.data.players;

import host.plas.hardcorehearts.HardcoreHearts;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

public class PlayerManager {
    @Getter @Setter
    private static ConcurrentSkipListSet<HHPlayer> players = new ConcurrentSkipListSet<>();

    public static void loadPlayer(HHPlayer player) {
        if (hasPlayer(player.getIdentifier())) {
            unloadPlayer(player.getIdentifier());
        }

        players.add(player);
    }

    public static void unloadPlayer(String identifier) {
        players.removeIf(p -> p.getIdentifier().equals(identifier));
    }

    public static Optional<HHPlayer> getPlayer(String identifier) {
        return players.stream().filter(p -> p.getIdentifier().equals(identifier)).findFirst();
    }

    public static boolean hasPlayer(String identifier) {
        return getPlayer(identifier).isPresent();
    }

    public static HHPlayer createNew(String identifier) {
        HHPlayer player = new HHPlayer(identifier);

        return player;
    }

    public static HHPlayer getOrCreate(String identifier) {
        Optional<HHPlayer> optional = getPlayer(identifier);
        if (optional.isPresent()) return optional.get();

        HHPlayer player = createNew(identifier);
        player.augment(HardcoreHearts.getDatabase().loadPlayerAsync(identifier));

        loadPlayer(player);

        return player;
    }
}
