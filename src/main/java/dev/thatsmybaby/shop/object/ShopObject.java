package dev.thatsmybaby.shop.object;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.item.Item;
import dev.thatsmybaby.shop.Placeholders;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lldv.llamaeconomy.LlamaEconomy;

@AllArgsConstructor @Getter
public final class ShopObject {

    private String name;
    private int price;

    private Item item;
    private String command;

    public void tryBuy(Player player, int amount) {
        int price = amount * this.price;

        if (LlamaEconomy.getAPI().getMoney(player) < price) {
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
            Server.getInstance().dispatchCommand(new ConsoleCommandSender(), this.command.replace("<player>", player.getName()).replace("<amount>", String.valueOf(amount)));
        } else {
            player.getInventory().addItem(item);
        }
    }
}