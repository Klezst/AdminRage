package tempelchat.AdminRage.lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;

import tempelchat.AdminRage.AdminRage;


public class Config 
{
	final private AdminRage plugin;
	private Properties defaultProperties;
	private Properties userProperties;
	final private File file;
	
	public Config (AdminRage plugin)
	{
		this.plugin = plugin;
		this.file = new File("plugins" + File.separator + plugin.pluginName + File.separator + plugin.pluginName + ".properties");
		
		defaultProperties = new Properties();
		setDefaultProperties();
		
		userProperties = new Properties( defaultProperties );
		readUserProperties();
		
	}
	
	private void setDefaultProperties ()
	{
		defaultProperties.setProperty( "on", "Someone provoked an admin!" ); 
		defaultProperties.setProperty( "off", "Admin calmed down" ); 
		defaultProperties.setProperty( "levelup", "Admin got even more angry!" ); 
		defaultProperties.setProperty( "leveldown", "Admin calmed down a little bit." ); 
		defaultProperties.setProperty( "boltonplayer", "Boom! Krapusch! ZZZZPZPZZZPZZZ--! crack!" ); 
		defaultProperties.setProperty( "boltonplayernum", "10" ); 
	}
	
	private void readUserProperties()
	{
		if(setupFile())
		{
			if(file.canRead())
			{
				try {
					userProperties.load(new FileReader(file));
				} catch (FileNotFoundException e) {
					plugin.message("Failed to find properties file although it was there for a second!", Level.WARNING);
				} catch (IOException e) {
					plugin.message("Failed to read properties file!", Level.WARNING);
				}
			}
			else
			{
				plugin.message("Can't read properties file!", Level.WARNING);
			}
		}
	}
	
	private boolean setupFile()
	{
		if(file.exists())
		{
			return true;
		}
		else
		{
			try {
				file.createNewFile();
				//Template Generation
				defaultProperties.store(new FileWriter(file), null);
				
				return setupFile();
			} catch (IOException e) {
				plugin.message("Unable to write properties file!", Level.WARNING);
			}
		}
		return false;
	}

	
	public Properties getUserProperties() {
		return userProperties;
	}
	
}