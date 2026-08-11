package me.robomonkey.versus.util;

import me.robomonkey.versus.Versus;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.lang.reflect.Method;

public class EffectUtil {

    public static String fireworkNoDamageFlag = "nodamage";
    public static EntityType FIREWORK_TYPE;

    private static Method stopAllSoundsMethod;

    static {
        EntityType type;
        try {
            type = EntityType.valueOf("FIREWORK_ROCKET");
        } catch (IllegalArgumentException e) {
            // Fallback to the old version if FIREWORK_ROCKET is not found
            type = EntityType.valueOf("FIREWORK");
        }
        FIREWORK_TYPE = type;

        try {
            stopAllSoundsMethod = Player.class.getMethod("stopAllSounds");
        } catch (NoSuchMethodException e) {
            stopAllSoundsMethod = null;
        }
    }

    /**
     * Stops all sounds for a player. Uses reflection to call stopAllSounds() (1.19+).
     * Silently does nothing on older versions where the method is unavailable.
     */
    public static void stopAllSounds(Player player) {
        if (stopAllSoundsMethod != null) {
            try {
                stopAllSoundsMethod.invoke(player);
            } catch (Exception e) {
                // Silently ignore
            }
        }
    }

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, Float.POSITIVE_INFINITY, 1F);
    }

    public static void spawnFireWorks(Location loc, int amount, Integer power, Color color) {
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, FIREWORK_TYPE);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(color).flicker(true).trail(false).build());
        fireworkMeta.setPower(power);
        for (int i = 0; i < amount; i++) {
            Firework firework1 = (Firework) loc.getWorld().spawnEntity(loc, FIREWORK_TYPE);
            firework1.setFireworkMeta(fireworkMeta);
            firework1.setMetadata(fireworkNoDamageFlag, new FixedMetadataValue(Versus.getInstance(), true));
            firework1.detonate();
        }
    }

    public static void spawnFireWorks(Location loc, Color color) {
        Firework firework = (Firework) loc.getWorld().spawnEntity(loc, FIREWORK_TYPE);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder().withColor(color).flicker(true).trail(false).build());
        fireworkMeta.setPower(50);
        firework.setFireworkMeta(fireworkMeta);
        firework.setMetadata(fireworkNoDamageFlag, new FixedMetadataValue(Versus.getInstance(), true));
        firework.detonate();
    }


    public static void spawnFireWorksDelayed(Location loc, int amount, Integer power, Long delayTicks, Color color) {
        Bukkit.getScheduler().runTaskLater(Versus.getInstance(), () -> {
            spawnFireWorks(loc, amount, power, color);
        }, delayTicks);
    }

    public static void freezePlayer(Player player) {
        player.setWalkSpeed(0);
        player.setSprinting(false);
        player.setFlySpeed(0);
    }

    public static void unfreezePlayer(Player player) {
        player.setInvulnerable(false);
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.1F);
    }

    public static void sendTitle(Player player, String title, int ticks, boolean fade) {
        int fadeTime = (fade) ? 20 : 0;
        player.sendTitle(MessageUtil.color(title), "", fadeTime, ticks, fadeTime);
    }

    public static void sendTitle(Player player, String title, String subtitle, int ticks, boolean fade) {
        int fadeTime = (fade) ? 20 : 0;
        player.sendTitle(MessageUtil.color(title), MessageUtil.color(subtitle), fadeTime, ticks, fadeTime);
    }

}
