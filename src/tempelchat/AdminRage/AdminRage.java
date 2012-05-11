package tempelchat.AdminRage;

import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.gmail.klezst.bukkit.adminrage.bukkitutil.compatibility.Permission;

import tempelchat.AdminRage.RagedAdmin.RageLevel;
import tempelchat.AdminRage.lib.Config;

//import bsh.Interpreter;

/**
 * AdminRage for Bukkit
 *
 * @author Tempelchat
 */
public class AdminRage extends JavaPlugin {
	
	public static final Logger log = Logger.getLogger("Minecraft");
	
    private final AdminRagePlayerListener playerListener = new AdminRagePlayerListener(this);
    
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    
    public String pluginName; 
    
    private World world = null;
    
    private Properties properties;
    
    //public Interpreter beanshell;
    
    //only on can rage!
    private RagedAdmin ragedAdmin = null;
    
    @Override
    public void onEnable() {
    	//EnableDebug();

    	
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(this.playerListener, this);
       
 
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        PluginDescriptionFile pluginDescription = this.getDescription();
        pluginName=pluginDescription.getName();
        //message(pluginDescription.getName() + " version " + pluginDescription.getVersion() + " is enabled!" );
        message("version " + pluginDescription.getVersion() + " is enabled!" );
        
		Config conf = new Config(this);
        properties = conf.getUserProperties();
        
        
        world = getServer().getWorlds().get(0);
        
    }
    @Override
    public void onDisable() {
    	unrageAdmin();

    	PluginDescriptionFile pluginDescription = this.getDescription();
        message("version " + pluginDescription.getVersion() + " is disabled!" );
    }
    
	@Override
	public void onLoad() {
		
	}
	
	/**rages a new admin
	 * 
	 *  @param admin to be raged
	 *  
	 *  
	 *  @return true  if admin was raged
	 *  @return false if there is already a admin raged*/
	
	public boolean rageAdmin(Player p, RageLevel level) {
		if(ragedAdmin == null)
		{
			
			ragedAdmin = new RagedAdmin(p, world.getTime(), level);
			updateWorldMood();
			return true;
		}

		return false;
	}
	
	public boolean rageAdmin(Player p)
	{
		return rageAdmin(p, RageLevel.Low);
	}

	public RagedAdmin getRagedAdmin() {
		return ragedAdmin;
	}
	
	
	public void unrageAdmin ()
	{
		//Player p = ragedAdmin.getPlayer(); 
		if(getRagedAdmin() != null)
		{
			world.setTime(ragedAdmin.getTime() + deltaTime() );
			
			ragedAdmin = null;
			updateWorldMood();
		}
	}
	
	public void updateWorldMood ()
	{
		RagedAdmin ra = getRagedAdmin();
		
		world.setThundering(false);
		world.setStorm(false);

		if(ra == null)
		{
			return;
		}
		
		switch (ra.getRageLevel())
		{
			case Deadful: //Lighting on click
			case High:    world.setThundering(true);
			case Low:     world.setStorm(true);
		}
		
	}
	
	/** The time that passed form setting it to postTime to now */
	public Long deltaTime()
	{
		Long t = world.getTime()-getRagedAdmin().getPostTime();
		
		while(t<0)
		{
			t+=24;
		}
		
		return t;
	}
    
    
	@Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{		
    	
    	//TODO Possible to come from outside
    	/*if(!(sender instanceof Player))
    	{
    		return false;
    	}*/
    	Player p = (Player)sender;
    	
    	if (!Permission.has(p, "adminrage.general")) 
    	{
    		p.sendMessage("You are not allowed to use "+pluginName+"!");
    		return true;
    	}
    	
    	String cmd = command.getName().toLowerCase();
		if(cmd.equalsIgnoreCase(pluginName)||cmd.equalsIgnoreCase("ar"))
		{
			if(args.length == 0)
			{
				//TODO
				sendHelp(p);
				
				/*p.sendMessage("Current time is: "+world.getTime()+"| Curent full time is: "+world.getFullTime());
				p.sendMessage("Specify time 0 <= x <24000");*/
			}
			else if(args.length == 1)
			{
				if(args[0].equalsIgnoreCase("help"))
				{
					sendHelp(p);
				}
				else if(args[0].equalsIgnoreCase("on"))
				{
					if(rageAdmin(p))
					{
						p.sendMessage("You are in rage!");
						getServer().broadcastMessage(properties.getProperty("on"));
					}
					else
					{
						if(getRagedAdmin().getPlayer() == p)
						{
							p.sendMessage("You are already in rage!");
						}
						else
						{
							p.sendMessage("Sorry, there is already an admin in rage. Be patient if possible!");
							p.sendMessage("Else: Get some tnt and port you to the player >:-D");
						}
						
					}
				}
				else if(args[0].equalsIgnoreCase("off"))
				{
					if(getRagedAdmin() != null && p == getRagedAdmin().getPlayer())
					{
						unrageAdmin();
						p.sendMessage("You calmed down");
						getServer().broadcastMessage(properties.getProperty("off"));
					}
					else
					{
						p.sendMessage("You are not in rage!");
					}
					
				}
				else if(args[0].equalsIgnoreCase("level"))
				{
					if(getRagedAdmin()!=null)
					{
						p.sendMessage("Current Level: "+getRagedAdmin().getRageLevel());
						p.sendMessage("Possible Levels: Low, High, Deadful;");
					}
					else
					{
						p.sendMessage("Not in rage!");
					}
				}
				if(args[0].equalsIgnoreCase("override"))
				{
					if(Permission.has(p, "adminrage.override"))
					{
						unrageAdmin();
						p.sendMessage("Admin has been unraged!");
					}
					else
					{
						p.sendMessage("You don't have the permission to do that!");
					}
				}
			}
			else if(args.length >= 2)
			{
				/*if(args[0].equalsIgnoreCase("set"))
				{
					try
					{
						world.setFullTime(Long.parseLong(args[1]));
						p.sendMessage("Time set!");
					}
					catch(NumberFormatException e)
					{
						p.sendMessage("Only numbers allowed!");
					}					
				}*/
				if(getRagedAdmin() != null)
				{
					if(getRagedAdmin().getPlayer() == p)
					{
						
						if(args[0].equalsIgnoreCase("level") && args.length==2)
						{
							RageLevel lvl = null;
							try
							{
								lvl = Enum.valueOf(RageLevel.class,  args[1].substring(0, 1).toUpperCase()+args[1].substring(1).toLowerCase() );
							}
							catch(IllegalArgumentException e)
							{
								p.sendMessage("This is not a vaild rage-level");
								return true;
							}
							
							
							byte change = (byte) lvl.compareTo(getRagedAdmin().getRageLevel());
							
							
							getRagedAdmin().setRageLevel(lvl);
							p.sendMessage("Ragelevel set to: "+lvl);
							
							if(change > 0)
							{
								getServer().broadcastMessage(properties.getProperty("levelup"));
							}
							else if(change < 0)
							{
								getServer().broadcastMessage(properties.getProperty("leveldown"));
							}
							
							

							updateWorldMood();
						}
						else if( (args[0].equalsIgnoreCase("throw")||args[0].equalsIgnoreCase("toss")) && args.length <=3)
						{
							//entity id or playername?
							int id = 0;
							try {
								id=Integer.parseInt(args[1]);
							}
							catch(NumberFormatException e){
								id=-1; //not a entity
							}
							Entity target = null;

							if(id==-1)
							{
								target = getServer().getPlayer(args[1]);
								if(target != null && (((Player) target).isOnline()))
								{
									if(target != p) //Is target the same player as the sender?
									{
										
										String msg = properties.getProperty("boltonplayer");
										if(!msg.isEmpty())
											p.sendMessage(msg);

									}
									else
									{
										p.sendMessage("I don't want that you kill yourself! Kill annoying players instead.");
										target=null;
										return true;
									}

								}
								else
								{
									p.sendMessage("Target player is not online!");
									target=null;
									return true;
								}
							}
							else //test if entity
							{
								List<Entity> entitylist = world.getEntities();
								
								for(Entity entity : entitylist)
								{
									if(entity.getEntityId() == id)
									{
										target = entity;
										break;
									}
								}
							}
							
							if(target==null)
							{
								p.sendMessage("Invalid Target");
								return true;
							}
							
							
							int num=0; //how many bolts?
							int defaultnum = 10;
							
							if(args.length==2) //how many not specified, reading properties
							{
								try	{
									num = Integer.parseInt(properties.getProperty("boltonplayernum"));
									if(num <= 0)
									{
										throw new NumberFormatException();
									}
								}
								catch(NumberFormatException e)	{
									message("The property \"boltonplayernum\" in the properties-file must be a positive Integer!", Level.WARNING);
									num = defaultnum;
								}
							}
							else //args.length == 4
							{
								try
								{
									num = Integer.parseInt(args[2]);
									if(num <= 0)
									{
										throw new NumberFormatException();
									}
								}
								catch(NumberFormatException e)
								{
									p.sendMessage("Invalid amount argument! Must be a positive Integer! ");
									return true;
								}
							}
							
							if(num>200)
								num=200;

							for(int i = 0 ; i<num ; i++)
							{
								world.strikeLightning(target.getLocation());
							}
							
						}
					}
					else
					{
						p.sendMessage("You are not in rage!");
					}
				}
				else
				{
					p.sendMessage("Noone is in rage.");
				}
			}


			return true;
		}

		return false;
	}
    
    
    private void sendHelp(Player p) {
		p.sendMessage("AdminRage:");
		p.sendMessage("/adminrage (help)");
		p.sendMessage("/adminrage [on|off]");
		p.sendMessage("/adminrage override - overrides current raged admin");
		p.sendMessage("/adminrage level - display current rage level");
		p.sendMessage("/adminrage level [Low|High|Deadful]");
		p.sendMessage("   Low: Sky gets dark and it starts raining");
		p.sendMessage("   High: +thunder storm");
		p.sendMessage("   Deadful: Play Zeus and toss deadful bolts with your hands!");
		p.sendMessage("/adminrage [throw|toss] [<player>|<EntityId>] (<amount>) - toss bolt on player (only on Deadful)");
	}
    
	public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {      	
        	
            return false;
        }
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
	
	public void message (String s)
	{
		message(s, Level.INFO);
	}
	
	public void debugmessage (String s)
	{
		log.log(Level.INFO, "["+pluginName+"] DEBUG: "+s);
	}
	
	public void message (String s, Level level)
	{
		log.log(level, "["+pluginName+"] "+s);
	}

	/*public void EnableDebug()
	{
		//enables the beanshell remote interpreter
		beanshell = new Interpreter();
		try {
			beanshell.set( "plugin", this );  // Provide a reference to your app
			beanshell.set( "portnum", 1248 );
			beanshell.eval("setAccessibility(true)"); // turn off access restrictions
			beanshell.eval("server(portnum)");
			
			beanshell.set( "RagedAdmin", this.getRagedAdmin() );
		}
		catch (Exception e){
			message("Beanshell initialization failure",Level.WARNING);
		}

	}*/
   
}

