package org.lockitemsnew;

import org.bukkit.Sound;

public class SoundUtil {

    public static Sound getSound(String soundName) {
        try {
            return Sound.valueOf(soundName);
        } catch (IllegalArgumentException e) {
            // Если чо звук сдохнит то, появится этот звук по умолчанию в случае ошибки
            return Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
        }
    }
}
