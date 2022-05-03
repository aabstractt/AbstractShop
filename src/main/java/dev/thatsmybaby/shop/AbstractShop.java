package dev.thatsmybaby.shop;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.ConfigSection;
import dev.thatsmybaby.shop.command.ShopCommand;
import dev.thatsmybaby.shop.object.ShopObject;
import lombok.Getter;

import java.util.*;

public final class AbstractShop extends PluginBase {

    @Getter private static AbstractShop instance;

    @Getter private final Map<String, List<ShopObject>> categoriesMap = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.saveResource("messages.yml");

        ConfigSection mainSection = this.getConfig().getSection("categories");

        for (String k : mainSection.getKeys()) {
            List<ShopObject> list = new LinkedList<>();

            for (String sectionName : mainSection.getSection(k).getKeys()) {
                ConfigSection section = mainSection.getSection(k).getSection(sectionName);

                if (!section.isInt("price")) {
                    this.getLogger().warning("Section " + sectionName + " on category " + k + " have a invalid price");

                    continue;
                }

                list.add(new ShopObject(
                        sectionName,
                        section.getInt("price"),
                        section.containsKey("item") ? Placeholders.parseItem(section.getString("item")) : null,
                        section.getString("command")
                ));
            }

            this.categoriesMap.put(k, list);
        }

        this.getServer().getCommandMap().register("abstractshop", new ShopCommand("tienda", "Abre un menú para comprar ítems"));
    }

    public String getCategoryName(int id) {
        return new ArrayList<>(this.categoriesMap.keySet()).get(id);
    }

    public List<ShopObject> getObjects(String categoryName) {
        return this.categoriesMap.get(categoryName);
    }
}