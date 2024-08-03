package host.plas.hardcorehearts;

import host.plas.bou.BetterPlugin;
import host.plas.hardcorehearts.config.DatabaseConfig;
import host.plas.hardcorehearts.config.MainConfig;
import host.plas.hardcorehearts.events.BouListener;
import host.plas.hardcorehearts.events.BukkitListener;
import host.plas.hardcorehearts.sql.HHDB;
import host.plas.hardcorehearts.timers.ReviveCheckTimer;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public final class HardcoreHearts extends BetterPlugin {
    @Getter @Setter
    private static HardcoreHearts instance;
    @Getter @Setter
    private static MainConfig mainConfig;
    @Getter @Setter
    private static DatabaseConfig databaseConfig;

    @Getter @Setter
    private static BukkitListener bukkitListener;
    @Getter @Setter
    private static BouListener bouListener;

    @Getter @Setter
    private static HHDB database;

    @Getter @Setter
    private static ReviveCheckTimer reviveCheckTimer;

    public HardcoreHearts() {
        super();
    }

    @Override
    public void onBaseEnabled() {
        // Plugin startup logic
        setInstance(this);

        setMainConfig(new MainConfig());
        setDatabaseConfig(new DatabaseConfig());

        setBukkitListener(new BukkitListener());
        setBouListener(new BouListener());

        setDatabase(new HHDB());

        setReviveCheckTimer(new ReviveCheckTimer());
    }

    @Override
    public void onBaseDisable() {
        // Plugin shutdown logic
    }
}
