package com.jarhax.prestige.compat.crt;

import crafttweaker.api.player.IPlayer;
import crafttweaker.api.world.IWorld;

public interface IEnabledAction {
    
    void process(IWorld world, IPlayer player);
    
}
