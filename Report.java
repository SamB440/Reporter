package me.SamB440.Report;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Report extends JavaPlugin implements Listener {
    public static Connection con;
    FileConfiguration config = getConfig();
	private static final Logger log = Logger.getLogger("Minecraft");
	public static Report instance;
	public static File configf;
	
	@Override
	public void onEnable() {
	    try {
	        HttpURLConnection c = (HttpURLConnection)new URL("http://www.spigotmc.org/api/general.php").openConnection();
	        c.setDoOutput(true);
	        c.setRequestMethod("POST");
	        c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=14043").getBytes("UTF-8"));
	        String oldVersion = this.getDescription().getVersion();
	        String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
	        if(!newVersion.equals(oldVersion)) {
	        	log.warning("[Report] There is a new version available! New version: " + newVersion + " Your version: " + oldVersion);
	        }
	        if(newVersion.equals(oldVersion)) {
	        	log.info("[Report] You are running the latest version.");
	        }
	    }
	      catch(Exception e) {
	        log.severe("[Report] Failed to check for updates. Do you have a valid connection?");
	      }
		Bukkit.getPluginManager().registerEvents(this, this);
		log.info("[Report] Enabled");
			instance = this;
			createFiles();
		}
																		
	private void createFiles() {
		File dir = new File("plugins/Report");
		log.info("[Report] Importing files");
		if (!dir.exists()){
			dir.mkdir();
			configf = new File(getDataFolder(), "config.yml");  				    	
	        saveDefaultConfig();
			log.info("[Report] Config file created!");
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(args.length == 2) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("ReportEntry") + ChatColor.DARK_AQUA + sender.getName()));
	            try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getDataFolder() + "/logger.txt", true)))) {
                out.println("[" + LocalDateTime.now() + "] " + "Reporter: " + sender.getName() + ", Reported: " + args[0] + ", For: " + args[1]);
                Bukkit.broadcast(ChatColor.DARK_RED + "" + ChatColor.ITALIC + sender.getName() + ChatColor.RED +  " has reported " + ChatColor.DARK_RED  + ChatColor.ITALIC + args[0] + ChatColor.RED + " for: " + ChatColor.AQUA + ChatColor.ITALIC + args[1], "Reporter.ReportView");
                config.set("Reports", + 1);
            	}catch (IOException e) {
            		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("LogFileError")));
           	}
		}
		else {
			if(args.length == 3) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("ReportEntry") + ChatColor.DARK_AQUA + sender.getName()));
		            try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getDataFolder() + "/logger.txt", true)))) {
	                out.println("[" + LocalDateTime.now() + "] " + " Reporter: " + sender.getName() + ", Reported: " + args[0] + ", For: " + args[1] + " " + args[2]);
	                Bukkit.broadcast(ChatColor.DARK_RED + "" + ChatColor.ITALIC + sender.getName() + ChatColor.RED +  " has reported " + ChatColor.DARK_RED  + ChatColor.ITALIC + args[0] + ChatColor.RED + " for: " + ChatColor.AQUA + ChatColor.ITALIC + args[1] + " " + args[2], "Reporter.ReportView");
	                config.set("Reports", + 1);
		            }catch (IOException e) {
	            		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("LogFileError")));
	            	}
			}
			else {
				if(args.length == 4) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("ReportEntry") + ChatColor.DARK_AQUA + sender.getName()));
						try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getDataFolder() + "/logger.txt", true)))) {
						out.println("[" + LocalDateTime.now() + "] " + "Reporter: " + sender.getName() + ", Reported: " + args[0] + ", For: " + args[1] + " " + args[2] + " " + args[3]);
						Bukkit.broadcast(ChatColor.DARK_RED + "" + ChatColor.ITALIC + sender.getName() + ChatColor.RED +  " has reported " + ChatColor.DARK_RED  + ChatColor.ITALIC + args[0] + ChatColor.RED + " for: " + ChatColor.AQUA + ChatColor.ITALIC + args[1] + " " + args[2] + " " + args[3], "Reporter.ReportView");
						config.set("Reports", + 1);
						}catch (IOException e) {
	            			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("LogFileError")));
	            		}
				}
				else {						
					if(args.length == 5) {
						Player p = (Player)sender;
						p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Too many args. Maximum: 3.");						
					}
					else {
					if(args.length == 0) {
						Player p = (Player)sender;
						p.sendMessage(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------");
						p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "                       Reporter                  ");
						p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "                    Author: SamB440              ");
						p.sendMessage(ChatColor.WHITE + "- " + ChatColor.BLUE + "" + ChatColor.GRAY + "" + ChatColor.ITALIC + "Please note these may be changed in later updates.");
						p.sendMessage(ChatColor.WHITE + "- " + ChatColor.BLUE + "report <name> <reason> " + ChatColor.GRAY + "" + ChatColor.ITALIC + "Reports a player.");
						p.sendMessage(ChatColor.WHITE + "- " + ChatColor.BLUE + "report data " + ChatColor.GRAY + "" + ChatColor.ITALIC + "Shows how much moderation the server needs.");
						p.sendMessage(ChatColor.WHITE + "- " + ChatColor.BLUE + "report list " + ChatColor.GRAY + "" + ChatColor.ITALIC + "Reads a list of reports from logger.txt.");
						p.sendMessage(ChatColor.WHITE + "- " + ChatColor.BLUE + "report clear " + ChatColor.GRAY + "" + ChatColor.ITALIC + "Deletes/Clears the file 'logger.txt'.");
						p.sendMessage(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------");
					}
					else {
						if(args.length == 1) {
							if(args[0].equalsIgnoreCase("list")) {
								if(sender.hasPermission("reporter.list")) {
									Player p = (Player)sender;
									p.sendMessage(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------");
									try {
										File fileToRead = new File("plugins/report/logger.txt");
										@SuppressWarnings("resource")
										BufferedReader br = new BufferedReader(new FileReader(fileToRead));
										String line = null;
											while ((line = br.readLine()) != null) {
											p.sendMessage(ChatColor.WHITE + "- " + ChatColor.translateAlternateColorCodes('&', config.getString("ListColour")  + line));
										}
											} catch (FileNotFoundException e) {
										p.sendMessage("No file is available!");
									} catch (IOException e) {
										e.printStackTrace();
									}
									p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Logs are also in plugins/report/logger.txt");
									p.sendMessage(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------");
								}
								else if(!sender.hasPermission("reporter.list")) {
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("No_Permission")));
								}
							}						
						
								else {
									if(args[0].equalsIgnoreCase("clear")) {
										if(sender.hasPermission("reporter.clear")) {
										Player p = (Player) sender;
										File logger = new File("plugins/report/logger.txt");
										File loggerbackup = new File("plugins/report/loggerbackup.txt");
										if (loggerbackup != null) {
											p.sendMessage(ChatColor.GREEN + "Deleting existing loggerbackup.txt...");
											loggerbackup.delete();
										}
										p.sendMessage(ChatColor.GREEN + "Renaming current logger.txt to loggerbackup.txt...");
										logger.renameTo(loggerbackup);
										p.sendMessage(ChatColor.GREEN + "File 'logger.txt' deleted successfully. " + ChatColor.GRAY + "Please create a report to generate a fresh file.");
									}
									else if(!sender.hasPermission("reporter.clear")) {
										sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("No_Permission")));
									}
								}													
					
							else {
								if(args[0].equalsIgnoreCase("data")) {
									if(sender.hasPermission("reporter.data")) {
									Player p = (Player)sender;
									p.sendMessage(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------");
									p.sendMessage(" ");
									p.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "When re-installing the jar, please delete plugins/report.");
									p.sendMessage(ChatColor.DARK_PURPLE + "Reports since install: " + config.getInt("Reports"));
						               if (config.getInt("Reports") == 0) {
						                    p.sendMessage((Object)ChatColor.BLUE + "Vulnerability: " + (Object)ChatColor.AQUA + "VERY LOW" + (Object)ChatColor.GRAY + " - " + (Object)ChatColor.RED + "No moderation needed!");
						                    p.sendMessage(" ");
						                    p.sendMessage((Object)ChatColor.WHITE + "" + (Object)ChatColor.STRIKETHROUGH + "--------------------------------------------");
						                } else if (config.getInt("Reports") <= 4) {
						                    p.sendMessage((Object)ChatColor.BLUE + "Vulnerability: " + (Object)ChatColor.GREEN + "LOW" + (Object)ChatColor.GRAY + " - " + (Object)ChatColor.RED + "No moderation needed!");
						                    p.sendMessage(" ");
						                    p.sendMessage(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------");
						                } else if (config.getInt("Reports") <= 9) {
						                    p.sendMessage((Object)ChatColor.BLUE + "Vulnerability: " + (Object)ChatColor.YELLOW + "Moderate" + (Object)ChatColor.GRAY + " - " + (Object)ChatColor.RED + "Moderate moderation needed!");
						                    p.sendMessage(" ");
						                    p.sendMessage((Object)ChatColor.WHITE + "" + (Object)ChatColor.STRIKETHROUGH + "--------------------------------------------");
						                } else if (config.getInt("Reports") >= 10) {
						                    p.sendMessage((Object)ChatColor.BLUE + "Vulnerability: " + (Object)ChatColor.RED + "High" + (Object)ChatColor.GRAY + " - " + (Object)ChatColor.RED + "High moderation needed - This is bad, you should try to reduce this!");
						                    p.sendMessage(" ");
						                    p.sendMessage((Object)ChatColor.WHITE + "" + (Object)ChatColor.STRIKETHROUGH + "--------------------------------------------");
											}
										}
										else if(!sender.hasPermission("reporter.data")) {
											sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("No_Permission")));
										}
									}
								}
							}																				
						}
					}
				}
			}
		}
	}
		return true;
}
			
			
	
				

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent s) {
		Player p = s.getPlayer();
		if (p.getName().equalsIgnoreCase("SamB440")) {
			log.info("SamB440 has been detected as a developer of Reporter, saving...");
			p.sendMessage("[Statistics] " + ChatColor.AQUA + "" + ChatColor.BOLD + "This server is using Reporter!");
			log.info("Saved.");
			log.info("This is only used for the author to record statistics. We have sent a message to the author.");
			}
		}
	}
