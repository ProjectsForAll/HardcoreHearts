package host.plas.hardcorehearts.data.crafts;

import host.plas.bou.utils.ColorUtils;
import host.plas.hardcorehearts.HardcoreHearts;
import host.plas.hardcorehearts.utils.CraftingConfig;
import host.plas.hardcorehearts.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

public class ReviveCraft extends HHCraft {
    public ReviveCraft(CraftingConfig config) {
        super(config);
    }

    public static void populateFirst() {
        String version = HardcoreHearts.getMainConfig().getResource().getString("crafts.revive.version");
        if (version != null && version.equals("1.0")) {
            return;
        }

        getDefaultConfig().save(HardcoreHearts.getMainConfig().getResource().getSection("crafts" + "." + "revive"));

        HardcoreHearts.getMainConfig().write("crafts.revive.version", "1.0");
    }

    public static CraftingConfig getDefaultConfig() {
        return new CraftingConfig(
                "revive",
                "aaa",
                "aba",
                "aaa",
                getDefaultIngredients(),
                ItemUtils.getItemNBT(getDefaultResult())
        );
    }

    public static ConcurrentSkipListMap<String, String> getDefaultIngredients() {
        ConcurrentSkipListMap<String, String> map = new ConcurrentSkipListMap<>();

        map.put("a", ItemUtils.getItemNBT(new ItemStack(Material.NETHERITE_INGOT)));
        map.put("b", ItemUtils.getItemNBT(LifeCraft.getDefaultResult()));

        return map;
    }

    public static ItemStack getDefaultResult() {
        ItemStack stack = new ItemStack(Material.NETHER_STAR);

        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtils.colorize("&c&lRevive Crystal"));

            List<String> lore = List.of(
                    ColorUtils.colorize("&7A crystal that can"),
                    ColorUtils.colorize("&7be used to revive"),
                    ColorUtils.colorize("&7a player who has"),
                    ColorUtils.colorize("&7been death banned.")
            );

            meta.setLore(lore);

            stack.setItemMeta(meta);
        }

        return stack;
    }
}
