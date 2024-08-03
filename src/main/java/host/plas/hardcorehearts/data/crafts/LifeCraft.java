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

public class LifeCraft extends HHCraft {
    public static final String IDENTIFIER = "life";

    public LifeCraft(CraftingConfig config) {
        super(config);
    }

    public static void populateFirst() {
        String version = HardcoreHearts.getMainConfig().getResource().getString("crafts." + IDENTIFIER + ".version");
        if (! (version != null && version.equals("1.0"))) {
            getDefaultConfig().save(HardcoreHearts.getMainConfig().getResource().getSection("crafts." + IDENTIFIER));

            HardcoreHearts.getMainConfig().write("crafts." + IDENTIFIER + ".version", "1.0");
        }

        CraftingManager.loadCraft(new LifeCraft(getDefaultConfig()));
    }

    public static CraftingConfig getDefaultConfig() {
        return new CraftingConfig(
                "life",
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
        map.put("b", ItemUtils.getItemNBT(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE)));

        return map;
    }

    public static ItemStack getDefaultResult() {
        ItemStack stack = new ItemStack(Material.QUARTZ);

        ItemMeta meta = stack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtils.colorize("&c&lLife Crystal"));

            List<String> lore = List.of(
                    ColorUtils.colorize("&7A crystal that can"),
                    ColorUtils.colorize("&7be used to add a"),
                    ColorUtils.colorize("&7life to whoever"),
                    ColorUtils.colorize("&7uses it.")
            );

            meta.setLore(lore);

            stack.setItemMeta(meta);
        }

        return stack;
    }
}
