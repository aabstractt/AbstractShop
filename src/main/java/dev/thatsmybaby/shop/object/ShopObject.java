package dev.thatsmybaby.shop.object;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.item.Item;
import dev.thatsmybaby.shop.AbstractShop;
import dev.thatsmybaby.shop.Placeholders;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lldv.llamaeconomy.LlamaEconomy;

import java.io.IOException;

@AllArgsConstructor @Getter
public final class ShopObject {

    private String name;
    private int price;

    private Item item;
    private String command;

    public void tryBuy(Player player, int amount) throws IOException {
        int price = amount * this.price;

        if (price > LlamaEconomy.getAPI().getMoney(player)) {
            player.sendMessage(Placeholders.replacePlaceholders("NO_ENOUGH_MONEY"));

            return;
        }

        Item item = null;

        if (this.item != null) {
            item = this.item.clone();

            item.setCount(amount);
        }

        if (item != null && !player.getInventory().canAddItem(item)) {
            player.sendMessage(Placeholders.replacePlaceholders("PLAYER_INVENTORY_FULL"));

            return;
        }

        LlamaEconomy.getAPI().reduceMoney(player, price);

        player.sendMessage(Placeholders.replacePlaceholders("OBJECT_BOUGHT", this.name, String.valueOf(amount), Placeholders.formatDouble(price)));

        if (item == null) {
            Server.getInstance().getScheduler().scheduleDelayedTask(AbstractShop.getInstance(), () -> Server.getInstance().dispatchCommand(new ConsoleCommandSender(), this.command.replace("<player>", player.getName()).replace("<amount>", String.valueOf(amount))), 10);
        } else {
            player.getInventory().addItem(item);
        }

//        DiscordHook.EmbedObject embedObject = new DiscordHook.EmbedObject();
//
//        embedObject.setTitle("Purchase successfully");
//        embedObject.setDescription("**" + player.getName() + " ha comprado " + this.getName() + " x" + amount + "\n\nEsto es un log para evitar bugs o cosa asi");
//        embedObject.setColor(Color.YELLOW);
//        embedObject.setFooter(new DiscordHook.EmbedObject.Footer("Developed by ! Abstract#0457", "https://media.discordapp.net/attachments/884566428731195403/950452253242818580/logonew_2.png"));
//
//        DiscordHook discordHook = new DiscordHook(AbstractShop.URL);
//
//        discordHook.setUsername("AbstractShop");
//        discordHook.setAvatarUrl("https://media.discordapp.net/attachments/884566428731195403/950452253242818580/logonew_2.png");
//        discordHook.addEmbed(embedObject);
//
//        discordHook.execute();
    }
}