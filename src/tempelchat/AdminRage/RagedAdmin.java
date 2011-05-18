package tempelchat.AdminRage;

import org.bukkit.entity.Player;

public class RagedAdmin {
	/** special Admin */
	private Player p;
	/** FullTime when admin got in rage */
	private Long time;
	/** FullTime after Timechanging */
	private Long postTime=15*1000L;
	/** The rage-level */
	private RageLevel ragelevel;
	
	public enum RageLevel
	{
		Low, High, Deadful;
	}
	
	RagedAdmin(Player p, Long fullTime)
	{
		this(p, fullTime, RageLevel.Low);
	}

	RagedAdmin(Player p, Long fullTime, RageLevel ragelevel)
	{
		this.p = p;
		this.time = fullTime;
		this.ragelevel = ragelevel;
		
		p.getWorld().setTime(postTime);
	}


	public Player getPlayer() {
		return p;
	}
	
	public Long getTime() {
		return time;
	}
	
	public Long getPostTime() {
		return postTime;
	}

	public void setRageLevel(RageLevel level) {
		this.ragelevel = level;
	}

	public RageLevel getRageLevel() {
		return ragelevel;
	}
	
}
