package host.plas.hardcorehearts.data.crafts;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListSet;

public class CraftingManager {
    @Getter @Setter
    private static ConcurrentSkipListSet<HHCraft> crafts = new ConcurrentSkipListSet<>();

    public static void loadCraft(HHCraft craft) {
        if (hasCraft(craft.getIdentifier())) {
            unloadCraft(craft.getIdentifier());
        }

        crafts.add(craft);
    }

    public static void unloadCraft(String identifier) {
        crafts.removeIf(c -> c.getIdentifier().equals(identifier));
    }

    public static boolean hasCraft(String identifier) {
        return getCraft(identifier).isPresent();
    }

    public static Optional<HHCraft> getCraft(String identifier) {
        return crafts.stream().filter(c -> c.getIdentifier().equals(identifier)).findFirst();
    }

    public static LifeCraft getLifeCraft() {
        return (LifeCraft) getCraft("life").get();
    }

    public static ReviveCraft getReviveCraft() {
        return (ReviveCraft) getCraft("regen").get();
    }
}
