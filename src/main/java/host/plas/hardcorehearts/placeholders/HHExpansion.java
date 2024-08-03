package host.plas.hardcorehearts.placeholders;

import host.plas.hardcorehearts.HardcoreHearts;
import host.plas.hardcorehearts.data.players.HHPlayer;
import host.plas.hardcorehearts.data.players.PlayerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HHExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "hh";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Drak";
    }

    @Override
    public @NotNull String getVersion() {
        return HardcoreHearts.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean register() {
        HardcoreHearts.getInstance().logInfo("Registering placeholders...");

        return super.register();
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        HHPlayer p = PlayerManager.getOrCreate(player.getUniqueId().toString());

        if (params.equals("lives")) {
            return String.valueOf(p.getLives());
        } else if (params.equals("deaths")) {
            return String.valueOf(p.getDeaths());
        } else if (params.equals("kills")) {
            return String.valueOf(p.getKills());
        } else if (params.equals("revives")) {
            return String.valueOf(p.getRevives());
        } else if (params.equals("deathbanned")) {
            return String.valueOf(p.isDeathBanned());
        }

        return null;
    }
}
