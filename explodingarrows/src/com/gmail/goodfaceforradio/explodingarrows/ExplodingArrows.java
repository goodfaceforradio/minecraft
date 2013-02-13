package com.gmail.goodfaceforradio.explodingarrows;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class ExplodingArrows extends JavaPlugin implements Listener {

    // Variable used to keep track of the arrow we automatically grant the player
    // when an exploding arrow is about to be shot.
    // For some reason we can get multiple calls to onPlayerUse for one shot, this
    // variable is used to make sure we only add an arrow once per shot.
    int bGrantedArrow = 0;

    // Executed when the plugin is enabled (automatically at server startup).
    public void onEnable() {
        // Define a new recipe and register it on the server.
        ItemStack explodingArrow = new ItemStack(Material.BLAZE_ROD, 1);
        ShapedRecipe recipe = new ShapedRecipe(explodingArrow);
        recipe.shape(new String[] { "s", "c" }).setIngredient('s', Material.STICK).setIngredient('c', Material.COAL);
        this.getServer().addRecipe(recipe);

        // Register for events to receive calls to onProjectileHit.
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        // The loaded arrow was shot, if it was an exploding arrow bGrantedArrow is true
        // because we granted the user an ordinary arrow to allow shooting with the bow
        // in that case minecraft already consumed that arrow, just reset bGrantedArrow.
        this.bGrantedArrow = 0;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();

        Arrow arrow;
        Player player;

        // Only interested in arrows.
        if(!(projectile instanceof Arrow)) {
            return;
        }
        
        arrow = (Arrow) projectile;

        // Only interested in players shooting.
        if(!(arrow.getShooter() instanceof Player)) {
            return;
        }
        player = (Player) arrow.getShooter();

        // Player needs to have one or more blaze rods in inventory to
        // get exploding arrows.
        if (player.getInventory().contains(Material.BLAZE_ROD, 1)) {
            // Create an explosion less powerful than the creeper explosion
            // which has power 4f.
            arrow.getWorld().createExplosion(arrow.getLocation(), 2f);

            // Remove a blaze rod from player inventory.
            player.getInventory().removeItem(new ItemStack(Material.BLAZE_ROD, 1));
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerItemHeld(PlayerItemHeldEvent event){
        Player p = event.getPlayer();
        if (this.bGrantedArrow == 1) {
            // We loaded an arrow for the player but he didn't shoot, instead he equipped something else.
            // Remove the arrow if we arn't in creative mode.
            if (p.getGameMode() != GameMode.CREATIVE) {
                p.getInventory().removeItem(new ItemStack(Material.ARROW, 1));
            }
            this.bGrantedArrow = 0;
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onPlayerUse(PlayerInteractEvent event){
        Player p = event.getPlayer();

        if(event.getAction().equals(Action.RIGHT_CLICK_AIR) && 
           this.bGrantedArrow == 0 && // Sometimes we get multiple calls to onPlayerUse, we're only interested in the first.
           p.getItemInHand().getType() == Material.BOW &&
           p.getGameMode() != GameMode.CREATIVE && // No need for arrows in inventory in creative mode
           p.getInventory().contains(Material.BLAZE_ROD, 1)) // We need at least one blaze rod in inventory for explosive arrows.
        {
            p.getInventory().addItem(new ItemStack(Material.ARROW, 1));
            this.bGrantedArrow = 1;
        }
    }
}
