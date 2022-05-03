package dev.thatsmybaby.shop.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerQuitEvent;

public final class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent ev) {
        Player player = ev.getPlayer();

        PlayerFormRespondedListener.clearCache(player.getName());
    }
}