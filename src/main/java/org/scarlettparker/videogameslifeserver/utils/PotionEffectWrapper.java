package org.scarlettparker.videogameslifeserver.utils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PotionEffectWrapper implements CustomEffect {
    private final PotionEffect potionEffect;

    public PotionEffectWrapper(PotionEffect potionEffect) {
        this.potionEffect = potionEffect;
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(potionEffect);
    }

    @Override
    public int getDuration() {
        return potionEffect.getDuration();
    }
}