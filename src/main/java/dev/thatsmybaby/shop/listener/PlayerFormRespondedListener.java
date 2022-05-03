package dev.thatsmybaby.shop.listener;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import dev.thatsmybaby.shop.AbstractShop;
import dev.thatsmybaby.shop.Placeholders;
import dev.thatsmybaby.shop.object.ShopObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class PlayerFormRespondedListener implements Listener {

    private final static Map<String, String> playerCategory = new HashMap<>();
    private final static Map<String, ShopObject> objectMap = new HashMap<>();

    @EventHandler
    public void onPlayerFormRespondedEvent(PlayerFormRespondedEvent ev) {
        Player player = ev.getPlayer();

        if (ev.wasClosed()) {
            playerCategory.remove(player.getName());

            return;
        }

        if (ev.getFormID() != 9832 && ev.getFormID() != 9833 && ev.getFormID() != 9834) return;

        if (ev.getFormID() == 9832 || ev.getFormID() == 9833) {
            FormResponseSimple formResponseSimple = (FormResponseSimple) ev.getResponse();

            String categoryName;

            if (ev.getFormID() == 9832) {
                categoryName = AbstractShop.getInstance().getCategoryName(formResponseSimple.getClickedButtonId());
            } else {
                categoryName = playerCategory.remove(player.getName());
            }

            if (categoryName == null) return;

            this.handleFormWindowSimple(player, categoryName, ev.getFormID(), formResponseSimple.getClickedButtonId());

            return;
        }

        ShopObject object = objectMap.remove(player.getName());

        if (object == null) return;

        FormResponseCustom formResponseCustom = (FormResponseCustom) ev.getResponse();

        int amount = Placeholders.parseInt(formResponseCustom.getInputResponse(0));

        if (amount < 1) {
            player.sendMessage(Placeholders.replacePlaceholders("INVALID_AMOUNT"));

            // TODO: Send sound
            return;
        }

        CompletableFuture.runAsync(() -> object.tryBuy(player, amount));
    }

    private void handleFormWindowSimple(Player player, String categoryName, int formId, int clickedButtonId) {
        List<ShopObject> list = AbstractShop.getInstance().getObjects(categoryName);

        if (list == null) return;

        if (formId == 9832) {
            FormWindowSimple formWindowSimple = new FormWindowSimple(categoryName, Placeholders.replacePlaceholders("FORM_CATEGORY_" + categoryName.toUpperCase() + "_CONTENT"));

            for (ShopObject object : list) {
                formWindowSimple.addButton(new ElementButton(Placeholders.replacePlaceholders("FORM_BUTTON", object.getName(), String.valueOf(object.getPrice()))));
            }

            formWindowSimple.addButton(new ElementButton(Placeholders.replacePlaceholders("BACK_FORM_BUTTON")));

            playerCategory.put(player.getName(), categoryName);

            player.showFormWindow(formWindowSimple, 9833);

            return;
        }

        ShopObject object = list.get(clickedButtonId);

        if (object == null) {
            Server.getInstance().dispatchCommand(player, "tienda");

            return;
        }

        FormWindowCustom formWindowCustom = new FormWindowCustom(object.getName());

        formWindowCustom.addElement(new ElementInput(Placeholders.replacePlaceholders("FORM_CONTENT_INFO", object.getName().toLowerCase(), String.valueOf(object.getPrice())), Placeholders.replacePlaceholders("FORM_INPUT_PLACEHOLDER_AMOUNT"), "1"));

        objectMap.put(player.getName(), object);

        player.showFormWindow(formWindowCustom, 9834);
    }

    public static void clearCache(String name) {
        playerCategory.remove(name);

        objectMap.remove(name);
    }
}