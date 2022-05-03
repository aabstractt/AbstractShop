package dev.thatsmybaby.shop.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import dev.thatsmybaby.shop.AbstractShop;
import dev.thatsmybaby.shop.Placeholders;

public final class ShopCommand extends Command {

    public ShopCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(TextFormat.RED + "Run this command in-game");

            return false;
        }

        FormWindowSimple formWindowSimple = new FormWindowSimple(Placeholders.replacePlaceholders("FORM_TITLE"), Placeholders.replacePlaceholders("FORM_CONTENT"));

        for (String categoryName : AbstractShop.getInstance().getCategoriesMap().keySet()) {
            formWindowSimple.addButton(new ElementButton(Placeholders.replacePlaceholders("CATEGORY_BUTTON", categoryName)));
        }

        ((Player) commandSender).showFormWindow(formWindowSimple, 9832);

        return false;
    }
}