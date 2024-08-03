package host.plas.hardcorehearts.data.crafts;

import host.plas.hardcorehearts.utils.CraftingConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;
import tv.quaint.objects.Identifiable;

@Getter @Setter
public class HHCraft implements Identifiable {
    private String identifier;
    private ItemStack result;

    public HHCraft(CraftingConfig config) {
        this.identifier = config.getIdentifier();
        config.register();

        this.result = config.getRecipe().getResult();
    }
}
