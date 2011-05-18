package tempelchat.AdminRage;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

import tempelchat.AdminRage.RagedAdmin.RageLevel;

/**
 * Handle events for all Player related events
 * @author Tempelchat
 */

public class AdminRagePlayerListener extends PlayerListener {

	private final AdminRage plugin;

    public AdminRagePlayerListener(AdminRage instance) {
        plugin = instance;
    }
    
    @Override
    public void onPlayerQuit(PlayerQuitEvent event)
    {
    	RagedAdmin ra = plugin.getRagedAdmin();
    	Player p = event.getPlayer();
    	
    	if(ra != null && ra.getPlayer() == p)
    	{
    		plugin.unrageAdmin();
    		plugin.getServer().broadcastMessage("Angry admin left server!");
    	}

    }
    
    /*@Override
    public void onPlayerDamage(PlayerDamageEvent event)
    {
    	TODO Any damage event? Make raged invincible.
    }*/

    @Override
    public void onPlayerInteract(PlayerInteractEvent event)
    {
    	Player p = event.getPlayer();
    	RagedAdmin ra = plugin.getRagedAdmin();

    	if(ra!=null && ra.getPlayer() == p)
    	{
    		if( (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
    				&& (ra.getRageLevel() == RageLevel.Deadful))
    		{
    			if(p.getItemInHand().getType() == Material.AIR)
    			{
    				Location loc = p.getTargetBlock(null, 300).getLocation();
    				if(! p.getLocation().toVector().isInSphere(loc.toVector(), 5))
    				{
    					p.getWorld().strikeLightning(loc);
    				}
    			}
    		}
    	}
    }
}

