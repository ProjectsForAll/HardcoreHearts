package host.plas.hardcorehearts.config;

import host.plas.hardcorehearts.HardcoreHearts;
import host.plas.hardcorehearts.data.crafts.LifeCraft;
import host.plas.hardcorehearts.utils.CraftingConfig;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", HardcoreHearts.getInstance(), false);
    }

    @Override
    public void init() {

    }

    public LifeCraft getLifeCraft() {
        return new LifeCraft(getCraftingConfig("life"));
    }

    public LifeCraft getReviveCraft() {
        return new LifeCraft(getCraftingConfig("revive"));
    }

    public CraftingConfig getCraftingConfig(String key) {
        reloadResource();

        return CraftingConfig.fromConfig(getResource().getSection("crafts." + key));
    }
}
