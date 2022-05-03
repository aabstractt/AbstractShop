package dev.thatsmybaby.shop;

import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Placeholders {

    private static Map<String, Object> messages = new HashMap<>();

    public static String replacePlaceholders(String text, String... args) {
        if (messages.isEmpty()) {
            messages = (new Config(new File(AbstractShop.getInstance().getDataFolder(), "messages.yml"))).getAll();
        }

        if (messages.containsKey(text)) {
            text = messages.get(text).toString();
        }

        for (int i = 0; i < args.length; i++) {
            text = text.replaceAll("\\{%" + i + "}", args[i]);
        }

        return TextFormat.colorize(text);
    }

    public static Item parseItem(String string) {
        String[] split = string.split(":");

        if (split.length < 2) {
            return null;
        }

        Item item = Item.get(Integer.parseInt(split[0]));

        if (item == null) return null;

        item.setDamage(Integer.parseInt(split[1]));

        return item;
    }

    public static int parseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (Exception ignored) {
            return -1;
        }
    }

    public static String formatDouble(double d) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "EN"));
        return nf.format(d);
    }
}