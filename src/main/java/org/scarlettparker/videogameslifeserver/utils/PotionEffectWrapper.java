package org.scarlettparker.videogameslifeserver.utils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PotionEffectWrapper implements CustomEffect {
    private final PotionEffect potionEffect;

    public PotionEffectWrapper(PotionEffect potionEffect) {
        this.potionEffect = potionEffect;
    }

    @Override
    public void applyFragility(Player player) {
        player.addPotionEffect(potionEffect);
    }

    public void applyKnockback(Player player) {
        player.addPotionEffect(potionEffect);
    }

    @Override
    public int getDuration() {
        return potionEffect.getDuration();
    }

    @Override
    public String getType() {
        return "";
    }
}