package host.plas.hardcorehearts.utils;

import host.plas.bou.utils.PluginUtils;
import host.plas.hardcorehearts.HardcoreHearts;
//import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.nbt.TagParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import tv.quaint.thebase.lib.google.gson.Gson;

public class ItemUtils {
    public static final Gson GSON = new Gson();

    public static void registerRecipe(CraftingConfig config) {
        Bukkit.getServer().removeRecipe(PluginUtils.getPluginKey(HardcoreHearts.getInstance(), config.getIdentifier()));

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
        try {
//            CompoundTag compound = TagParser.parseTag(nbt);
            NBTTagCompound compound = MojangsonParser.a(nbt);

            net.minecraft.world.item.ItemStack item = net.minecraft.world.item.ItemStack.a(compound);

            return CraftItemStack.asBukkitCopy(item);
        } catch (Throwable e) {
            HardcoreHearts.getInstance().logSevereWithInfo("Failed to parse NBT: " + nbt, e);
        }

        return new ItemStack(Material.AIR);
    }

    public static String getItemNBT(ItemStack item) {
        net.minecraft.world.item.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
//        CompoundTag compound = new CompoundTag();
//        nmsItem.save(compound);
//
//        return compound.toString();

        NBTTagCompound compound = new NBTTagCompound();
        nmsItem.b(compound);

        return compound.toString();
    }

    public static boolean isItemEqual(ItemStack item1, ItemStack item2) {
        return getItemNBT(item1).equals(getItemNBT(item2));
    }
}
