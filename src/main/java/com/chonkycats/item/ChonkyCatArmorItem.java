package com.chonkycats.item;

import net.minecraft.world.item.Item;

public class ChonkyCatArmorItem extends Item {
    private final int armorBonus;
    private final int armorType; // 1=leather, 2=iron, 3=gold, 4=diamond, 5=netherite

    public ChonkyCatArmorItem(int armorBonus, int armorType, Properties properties) {
        super(properties);
        this.armorBonus = armorBonus;
        this.armorType = armorType;
    }

    public int getArmorBonus() {
        return armorBonus;
    }

    public int getArmorType() {
        return armorType;
    }
}
