package earth.terrarium.adastra.common.menus.base;

import earth.terrarium.adastra.common.menus.configuration.MenuConfiguration;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseConfigurableContainerMenu<T extends IInventory> extends BaseContainerMenu<T> {

    private final List<MenuConfiguration> configurations = new ArrayList<>();

    protected BaseConfigurableContainerMenu(InventoryPlayer inventory, T entity) {
        super(inventory, entity);
    }

    protected void addConfigSlot(MenuConfiguration configuration) {
        configurations.add(configuration);
    }

    public List<MenuConfiguration> getConfigurations() {
        return Collections.unmodifiableList(configurations);
    }
}
