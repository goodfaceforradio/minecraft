package com.gmail.goodfaceforradio.explodingarrows;

import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class ExplodingArrows extends JavaPlugin implements Listener {

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
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();

		Arrow arrow;
		Player player;
		
		// Only interested in arrows.
		try {
			arrow = (Arrow) projectile;
		} catch (ClassCastException e) {
			return;
		}
	    
		// Only interested in players shooting.
		try {
			player = (Player) arrow.getShooter();
		} catch (ClassCastException e) {
			return;
		}

		// Player needs to have one or more blaze rods in inventory to
		// get exploding arrows.
	    if (player.getInventory().contains(Material.BLAZE_ROD, 1)) {
	    	// Create an explosion less powerful than the creeper explosion
	    	// which has power 4f.
		    arrow.getWorld().createExplosion(arrow.getLocation(), 2f);
	    }	    
	}	
}