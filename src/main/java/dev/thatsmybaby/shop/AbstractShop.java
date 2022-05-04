package dev.thatsmybaby.shop;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import dev.thatsmybaby.shop.command.ShopCommand;
import dev.thatsmybaby.shop.listener.PlayerFormRespondedListener;
import dev.thatsmybaby.shop.listener.PlayerQuitListener;
import dev.thatsmybaby.shop.object.ShopObject;
import dev.thatsmybaby.shop.utils.DiscordHook;
import lombok.Getter;

import java.io.File;
import java.util.*;

public final class AbstractShop extends PluginBase {

    @Getter private static AbstractShop instance;

    @Getter private final Map<String, List<ShopObject>> categoriesMap = new HashMap<>();

    public static String URL = "https://discord.com/api/webhooks/971255032295063602/XngMfMl-AO5lxoJatSEw8ECLMnmGTHrycsyaMK7qi0fhafhSwldf9xzhzEzMA87KeumY";

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.saveResource("messages.yml");

        for (String k : this.getConfig().getKeys(false)) {
            List<ShopObject> list = new LinkedList<>();

            for (String sectionName : this.getConfig().getSection(k).getKeys(false)) {
                ConfigSection section = this.getConfig().getSection(k).getSection(sectionName);

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

        this.getServer().getPluginManager().registerEvents(new PlayerFormRespondedListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);
    }

    private void startAdapter() {
        Config config = new Config(new File(this.getDataFolder(), "llamashop.yml"));

        for (String k : config.getSection("ShopForm").getKeys(false)) {
            List<String> list = config.getSection("ShopForm").getStringList(k);

            for (String string : list) {
                String[] split = string.split(":");

                ConfigSection section = new ConfigSection();
                section.set("item", String.format("%s:%s", split[1], split[2]));
                section.set("price", Integer.parseInt(split[3]));

                this.getConfig().set(k + "." + split[0], section);
            }
        }

        this.getConfig().save();
    }

    public String getCategoryName(int id) {
        return new ArrayList<>(this.categoriesMap.keySet()).get(id);
    }

    public List<ShopObject> getObjects(String categoryName) {
        return this.categoriesMap.get(categoryName);
    }
}