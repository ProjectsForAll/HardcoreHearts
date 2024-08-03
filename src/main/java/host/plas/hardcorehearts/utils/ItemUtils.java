package host.plas.hardcorehearts.utils;

import host.plas.bou.utils.PluginUtils;
import host.plas.hardcorehearts.HardcoreHearts;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import tv.quaint.thebase.lib.google.gson.Gson;

public class ItemUtils {
    public static final Gson GSON = new Gson();

    public static void registerRecipe(CraftingConfig config) {
        Bukkit.getServer().addRecipe(getRecipe(config));
    }

    public static Recipe getRecipe(CraftingConfig config) {
        ShapedRecipe recipe = new ShapedRecipe(PluginUtils.getPluginKey(HardcoreHearts.getInstance(), config.getIdentifier()), getCraftingResult(config));
        recipe.shape(config.getLine1(), config.getLine2(), config.getLine3());

        for (String key : config.getIngredients().keySet()) {
            recipe.setIngredient(key.charAt(0), getItem(config.getIngredients().get(key)).getType());
        }

        return recipe;
    }

    public static ItemStack getCraftingResult(CraftingConfig config) {
        return getItem(config.getResult());
    }

    public static ItemStack getItem(String nbt) {
        return GSON.fromJson(nbt, ItemStack.class);
    }

    public static String getItemNBT(ItemStack item) {
        return GSON.toJson(item);
    }

    public static boolean isItemEqual(ItemStack item1, ItemStack item2) {
        return getItemNBT(item1).equals(getItemNBT(item2));
    }
}
