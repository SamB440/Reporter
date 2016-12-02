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
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Report extends JavaPlugin implements Listener {
    private final ArrayList<String> cooldown = new ArrayList<String>();
    private final ArrayList<String> off = new ArrayList<String>();
    public static Connection con;
    private static Connection connection;
    private String host, database, username, password;
    private int port;
    FileConfiguration config = getConfig();
	private static final Logger log = Logger.getLogger("Minecraft");
	public static Report instance;
	public static File configf;
    Inventory MainMenu = Bukkit.createInventory(null, 45, ChatColor.GREEN + "Reporter > Main Menu"); {
    // The first parameter, is the inventory owner. I make it null to let everyone use it.
    //The second parameter, is the slots in a inventory. Must be a multiple of 9. Can be up to 54.
    //The third parameter, is the inventory name. This will accept chat colors
    		ItemStack Head = new ItemStack(Material.SKULL_ITEM,1 , (byte)3);
    		SkullMeta HeadMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
    		HeadMeta.setOwner("Chewbacca");
    		HeadMeta.setDisplayName(ChatColor.RED + "Report a player!");
    		//HeadMeta.setLore(Arrays.asList("Hi, I'm Chewie, also known as ChewBacca!", "I report players for you!"));
    		Head.setItemMeta(HeadMeta);
    		ItemStack InkSack = new ItemStack(Material.INK_SACK);
    		ItemMeta InkSackMeta = InkSack.getItemMeta();
    		InkSackMeta.setDisplayName(ChatColor.GREEN + "Details");
    		InkSackMeta.setLore(Arrays.asList(ChatColor.WHITE + "Author: SamB440", ChatColor.WHITE + "Version: " + this.getDescription().getVersion(), ChatColor.WHITE + "MySQL: " + getConfig().getString("MySQL_Enabled")));
    		InkSack.setItemMeta(InkSackMeta);
    		ItemStack Paper = new ItemStack(Material.PAPER);
    		ItemMeta PaperMeta = Paper.getItemMeta();
    		PaperMeta.setDisplayName(ChatColor.GREEN + "Commands");
    		PaperMeta.setLore(Arrays.asList(ChatColor.WHITE + "report <name> <reason> - " + "Reports a player.", ChatColor.WHITE + "report list - " + "List all reports", ChatColor.WHITE + "report clear - " + "Deletes/Clears the file 'logger.txt'"));
    		Paper.setItemMeta(PaperMeta);
    		ItemStack GlassBlock = new ItemStack(Material.GLASS);
    		ItemMeta GlassBlockMeta = GlassBlock.getItemMeta();
    		GlassBlockMeta.setDisplayName(ChatColor.GREEN + "Clear reports list");
    		GlassBlockMeta.setLore(Arrays.asList(ChatColor.WHITE + "Please note: If you have used /report list recently,", ChatColor.WHITE + "then this will not work."));
    		GlassBlock.setItemMeta(GlassBlockMeta);
    		ItemStack Book = new ItemStack(Material.BOOK);
    		ItemMeta BookMeta = Book.getItemMeta();
    		BookMeta.setDisplayName(ChatColor.GREEN + "List reports");
    		Book.setItemMeta(BookMeta);
    		ItemStack RedstoneBlock = new ItemStack(Material.REDSTONE_BLOCK);
	        ItemMeta RedstoneBlockMeta = RedstoneBlock.getItemMeta();        
	        RedstoneBlockMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Exit Main Menu");
	        RedstoneBlock.setItemMeta(RedstoneBlockMeta);
	        MainMenu.setItem(4, Head);
	        MainMenu.setItem(32, InkSack);
	        MainMenu.setItem(30, Paper);
	        MainMenu.setItem(28, GlassBlock);
	        MainMenu.setItem(34, Book);
            MainMenu.setItem(40, RedstoneBlock);
    	}
    	Inventory MainMenu2 = Bukkit.createInventory(null, 45, ChatColor.GREEN + "Reporter > Player Main Menu"); {
        		ItemStack Head = new ItemStack(Material.SKULL_ITEM,1 , (byte)3);
        		SkullMeta HeadMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
        		HeadMeta.setOwner("Chewbacca");
        		HeadMeta.setDisplayName(ChatColor.RED + "Report a player!");
        		//HeadMeta.setLore(Arrays.asList("Hi, I'm Chewie, also known as ChewBacca!", "I report players for you!"));
        		Head.setItemMeta(HeadMeta);
        		ItemStack Paper = new ItemStack(Material.PAPER);
        		ItemMeta PaperMeta = Paper.getItemMeta();
        		PaperMeta.setDisplayName(ChatColor.GREEN + "Commands");
        		PaperMeta.setLore(Arrays.asList(ChatColor.WHITE + "report <name> <reason> - " + "Reports a player.", ChatColor.WHITE + "report list - " + "List all reports", ChatColor.WHITE + "report clear - " + "Deletes/Clears the file 'logger.txt'"));
        		Paper.setItemMeta(PaperMeta);
        		ItemStack RedstoneBlock = new ItemStack(Material.REDSTONE_BLOCK);
    	        ItemMeta RedstoneBlockMeta = RedstoneBlock.getItemMeta();        
    	        RedstoneBlockMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Exit Main Menu");
    	        RedstoneBlock.setItemMeta(RedstoneBlockMeta);
        		ItemStack InkSack = new ItemStack(Material.INK_SACK);
        		ItemMeta InkSackMeta = InkSack.getItemMeta();
        		InkSackMeta.setDisplayName(ChatColor.GREEN + "Details");
        		InkSackMeta.setLore(Arrays.asList(ChatColor.WHITE + "Author: SamB440", ChatColor.WHITE + "Version: " + this.getDescription().getVersion(), ChatColor.WHITE + "MySQL: " + getConfig().getString("MySQL_Enabled")));
        		InkSack.setItemMeta(InkSackMeta);
        		MainMenu2.setItem(30, Paper);
        		MainMenu2.setItem(40, RedstoneBlock);
        		MainMenu2.setItem(32, InkSack);
        		MainMenu2.setItem(4, Head);
        	}
        //for (Player a : Bukkit.getOnlinePlayers()) {
            //String name = a.getPlayer().getName();
            //ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
            //SkullMeta meta = (SkullMeta)item.getItemMeta();
            //meta.setDisplayName(name);
            //meta.setOwner(a.getName());
            //item.setItemMeta(meta);
            //PlayerReport.addItem(item);
          //}
        public static Inventory AnvilInput = Bukkit.createInventory(null, InventoryType.ANVIL, ChatColor.RED + "Enter Reason");
        	static {
        		//ItemStack Paper = new ItemStack(Material.PAPER);
        		//ItemMeta PaperMeta = Paper.getItemMeta();
        		//PaperMeta.setDisplayName("Paper");
        		//Paper.setItemMeta(PaperMeta);
        		//AnvilInput.setItem(2, Paper);
        		ItemStack Paper2 = new ItemStack(Material.PAPER);
        		ItemMeta PaperMeta2 = Paper2.getItemMeta();
        		PaperMeta2.setDisplayName(null);
        		Paper2.setItemMeta(PaperMeta2);
        		AnvilInput.setItem(0, Paper2);
        		//ItemStack Paper3 = new ItemStack(Material.PAPER);
        		//ItemMeta PaperMeta3 = Paper.getItemMeta();
        		//PaperMeta3.setDisplayName("Paper");
        		//Paper3.setItemMeta(PaperMeta3);
        		//AnvilInput.setItem(1, Paper3);
        	}
            Inventory PlayerReport = Bukkit.createInventory(null, 45, ChatColor.GREEN + "Reporter > Report Menu"); {
            
            }
           // Inventory ChooseReason = Bukkit.createInventory(null, 9, ChatColor.GREEN + "Choose Reason"); {
            	//Material s1 = Material.getMaterial(getConfig().getString("Reasons.Slot_2.Material"));
            	//Material s2 = Material.getMaterial(getConfig().getString("Reasons.Slot_3.Material"));
            	//Material s3 = Material.getMaterial(getConfig().getString("Reasons.Slot_4.Material"));
            	//Material s4 = Material.getMaterial(getConfig().getString("Reasons.Slot_5.Material"));
            	//Material s5 = Material.getMaterial(getConfig().getString("Reasons.Slot_6.Material"));
            	//Material s6 = Material.getMaterial(getConfig().getString("Reasons.Slot_7.Material"));
            	//Material s7 = Material.getMaterial(getConfig().getString("Reasons.Slot_8.Material"));
            	//ItemStack CustomReason = new ItemStack(Material.PAPER);
            	//ItemMeta CustomReasonMeta = CustomReason.getItemMeta();
            	//CustomReasonMeta.setDisplayName(ChatColor.RED + "Enter custom reason");
            	//CustomReasonMeta.setLore(Arrays.asList(ChatColor.GRAY + "[Left Click]"));
            	//CustomReason.setItemMeta(CustomReasonMeta);
            	//ChooseReason.addItem(CustomReason);
            	//ItemStack Slot0 = new ItemStack(Material.getMaterial(getConfig().getString("Reasons.Slot_1.Material")));
            	//ItemMeta Slot0Meta = Slot0.getItemMeta();
            	//Slot0Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Reasons.Slot_1.Name")));
            	//Slot0Meta.setLore(Arrays.asList(ChatColor.GRAY + "[Left Click]"));
            	//Slot0.setItemMeta(Slot0Meta);
            	//ChooseReason.addItem(Slot0);
            //}

	@Override
	public void onEnable() {
		
        host = getConfig().getString("Host");
        port = getConfig().getInt("Port");
        database = getConfig().getString("Database");
        username = getConfig().getString("Username");
        password = getConfig().getString("Password");
        if(getConfig().getBoolean("MySQL_Enabled")) {
        try {    
            openConnection();
			createReportTable();
			log.info("Opened connection to MySQL server and created Report Table!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        	}
        }
        if(getConfig().getBoolean("Update_Check")) {
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
        }
		Bukkit.getPluginManager().registerEvents(this, this);
		log.info("[Report] The plugin has been enabled!");
			instance = this;
			createFiles();
			log.info("[Report] All set to go!");
		}
	@Override
	public void onDisable() {
		if(config.getBoolean("MySQL_Enabled")) {
		try {
			connection.close();
			log.info("[Report] Closed MySQL connection!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		log.info("Disabling plugin.");
		getServer().getPluginManager().disablePlugin(this);
	}
}
																		
	private void createFiles() {
		File dir = new File("plugins/Report");
		File dir2 = new File("plugins/Report/lang");
		File dir3 = new File("plugins/Report/players");
		log.info("[Report] Importing files");
		if (!dir.exists()){
			dir.mkdir();
			configf = new File(getDataFolder(), "config.yml");  				    	
	        saveDefaultConfig();
			log.info("[Report] Config file created!");
		}
		if (!dir2.exists()){
			dir2.mkdir();
		}
		if(!dir3.exists()) {
			dir3.mkdir();
		}
	}
	public void openConnection() throws SQLException, ClassNotFoundException {
	    if (connection != null && !connection.isClosed()) {
	        return;
	    }

	    synchronized (this) {
	        if (connection != null && !connection.isClosed()) {
	            return;
	        }
	        Class.forName("com.mysql.jdbc.Driver");
	        connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
	    }
	}
    public static void createReportTable() {
        try {
 
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ReportDatabase (ID INT(10) NOT NULL AUTO_INCREMENT, DATE DATETIME,REPORTER VARCHAR(16) NOT NULL,REPORTED VARCHAR(16) NOT NULL,REASON VARCHAR(100) NOT NULL, PRIMARY KEY (ID))");
            //statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,For) VALUES('TestDate','TestReporter','TestReported','TestFor'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void dropReportTable() {
    	try {
    		Statement statement = connection.createStatement();
    		statement.executeUpdate("DROP TABLE IF EXISTS ReportDatabase");
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(args.length == 2) {
			if(!args[0].equalsIgnoreCase(sender.getName()) && !cooldown.contains(sender.getName())) {
				cooldown.add(sender.getName());
	    		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
	                @Override
	                public void run() {
	                	cooldown.remove(sender.getName());
	            }
	        }, 160L);
	    			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("ReportEntry") + ChatColor.DARK_AQUA + sender.getName()));
				if(getConfig().getBoolean("MySQL_Enabled")) {
					try {
						Statement statement = connection.createStatement();
						statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,Reason) VALUES('" + LocalDateTime.now() + "','" + sender.getName() + "','" + args[0] + "','" + args[1] + "')");
						//statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,Reason) VALUES('2016-10-09','Player','Test','Testreason')");
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getDataFolder() + "/logger.txt", true)))) {
					out.println("[" + LocalDateTime.now() + "] " + "Reporter: " + sender.getName() + ", Reported: " + args[0] + ", For: " + args[1]);
                	if(getConfig().getBoolean("Bungeecord") != true) {
                		for(Player player:Bukkit.getOnlinePlayers()) {
                			if(player.hasPermission("reporter.reportview") && !off.contains(player.getName())) {
                				player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + sender.getName() + ChatColor.RED +  " has reported " + ChatColor.DARK_RED  + ChatColor.ITALIC + args[0] + ChatColor.RED + " for: " + ChatColor.AQUA + ChatColor.ITALIC + args[1]);
                				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                				TitleManager.sendTitle(player, ChatColor.translateAlternateColorCodes('&', config.getString("ReportTitle")), ChatColor.translateAlternateColorCodes('&', config.getString("ReportSubtitle")), 60);
              				}
              			}
                	}              	
            	} catch (IOException e) {
            		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("LogFileError")));
            	}
			}
			if(args[0].equalsIgnoreCase(sender.getName())) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Invalid_Player")));
			}
			if(cooldown.contains(sender.getName())) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Cooldown")));
			}
		}
		else {
			if(args.length == 3) {
				if(!args[0].equalsIgnoreCase(sender.getName()) && !cooldown.contains(sender.getName())) {
					cooldown.add(sender.getName());
		    		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
		                @Override
		                public void run() {
		                	cooldown.remove(sender.getName());
		            }
		        }, 160L);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("ReportEntry") + ChatColor.DARK_AQUA + sender.getName()));
					if(getConfig().getBoolean("MySQL_Enabled")) {
						try {
							Statement statement = connection.createStatement();
							statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,For) VALUES('" + LocalDateTime.now() + "','" + sender.getName() + "','" + args[0] + "','" + args[1] + "','" + args[2] + "'");
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}    
					try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getDataFolder() + "/logger.txt", true)))) {
						out.println("[" + LocalDateTime.now() + "] " + " Reporter: " + sender.getName() + ", Reported: " + args[0] + ", For: " + args[1] + " " + args[2]);
						for(Player player:Bukkit.getOnlinePlayers()) {
							if(player.hasPermission("reporter.reportview") && !off.contains(player.getName())) {
								player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + sender.getName() + ChatColor.RED +  " has reported " + ChatColor.DARK_RED  + ChatColor.ITALIC + args[0] + ChatColor.RED + " for: " + ChatColor.AQUA + ChatColor.ITALIC + args[1] + " " + args[2]);
								player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
								TitleManager.sendTitle(player, ChatColor.translateAlternateColorCodes('&', config.getString("ReportTitle")), ChatColor.translateAlternateColorCodes('&', config.getString("ReportSubtitle")), 60);
	              				}
	              			}
		            	}catch (IOException e) {
		            		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("LogFileError")));
		            	}
					}
				if(args[0].equalsIgnoreCase(sender.getName())) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Invalid_Player")));
				}
				if(cooldown.contains(sender.getName())) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Cooldown")));
				}
			}
			else {
				if(args.length == 4) {
					if(!args[0].equalsIgnoreCase(sender.getName()) && !cooldown.contains(sender.getName())) {
						cooldown.add(sender.getName());
			    		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			                @Override
			                public void run() {
			                	cooldown.remove(sender.getName());
			            }
			        }, 160L);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("ReportEntry") + ChatColor.DARK_AQUA + sender.getName()));
						if(getConfig().getBoolean("MySQL_Enabled")) {
							try {
								Statement statement = connection.createStatement();
								statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,For) VALUES('" + LocalDateTime.now() + "','" + sender.getName() + "','" + args[0] + "','" + args[1] + "','" + args[2] + "','" + args[4] + "'");
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getDataFolder() + "/logger.txt", true)))) {
							out.println("[" + LocalDateTime.now() + "] " + "Reporter: " + sender.getName() + ", Reported: " + args[0] + ", For: " + args[1] + " " + args[2] + " " + args[3]);
							for(Player player:Bukkit.getOnlinePlayers()) {
								if(player.hasPermission("reporter.reportview") && !off.contains(player.getName())) {
									player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + sender.getName() + ChatColor.RED +  " has reported " + ChatColor.DARK_RED  + ChatColor.ITALIC + args[0] + ChatColor.RED + " for: " + ChatColor.AQUA + ChatColor.ITALIC + args[1] + " " + args[2]);
									player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
									TitleManager.sendTitle(player, ChatColor.translateAlternateColorCodes('&', config.getString("ReportTitle")), ChatColor.translateAlternateColorCodes('&', config.getString("ReportSubtitle")), 60);
		              				}
		              			}
							}catch (IOException e) {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("LogFileError")));
							}
						}
					if(args[0].equalsIgnoreCase(sender.getName())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Invalid_Player")));
					}
					if(cooldown.contains(sender.getName())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Cooldown")));
					}
				}
				else {						
					if(args.length >= 5) {
						Player p = (Player)sender;
						p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Too many args. Maximum: 3.");						
					}
					else {
					if(args.length == 0) {
						Player p = (Player)sender;
						if(p.hasPermission("Reporter.GUI.Staff")) {
							p.openInventory(MainMenu);
						}
						else if(p.hasPermission("Reporter.GUI.Player")) {
							p.openInventory(MainMenu2);
						}
					}
					else {
						if(args.length == 1) {
							if(args[0].equalsIgnoreCase("off") && !off.contains(sender.getName()) && sender.hasPermission("reporter.toggle")) {
								off.add(sender.getName());
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Toggled_Off")));
							}
							if(args[0].equalsIgnoreCase("off") && off.contains(sender.getName()) && sender.hasPermission("reporter.toggle")) {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Already_Toggled_Off")));
							}
							if(args[0].equalsIgnoreCase("on") && off.contains(sender.getName()) && sender.hasPermission("reporter.toggle")) {
								off.remove(sender.getName());
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Toggled_On")));
							}
							if(args[0].equalsIgnoreCase("on") && !off.contains(sender.getName()) && sender.hasPermission("reporter.toggle")) {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Already_Toggled_On")));
							}
							else if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off") && !sender.hasPermission("reporter.toggle")) {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("No_Permission")));
							}
							if(args[0].equalsIgnoreCase("list")) {
								if(sender.hasPermission("reporter.list")) {
									Player p = (Player)sender;
									p.sendMessage(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------");
									try {
										File fileToRead = new File("plugins/Report/logger.txt");
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
										File logger = new File("plugins/Report/logger.txt");
										File loggerbackup = new File("plugins/Report/loggerbackup.txt");
										if (loggerbackup != null) {
											p.sendMessage(ChatColor.GREEN + "Deleting existing loggerbackup.txt...");
											loggerbackup.delete();
										}
										p.sendMessage(ChatColor.GREEN + "Renaming current logger.txt to loggerbackup.txt...");
										logger.renameTo(loggerbackup);
										p.sendMessage(ChatColor.GREEN + "File 'logger.txt' deleted successfully. " + ChatColor.GRAY + "Please create a report to generate a fresh file.");
										if(getConfig().getBoolean("MySQL_Enabled")) {
											p.sendMessage(ChatColor.GREEN + "Deleting MySQL entries...");
											dropReportTable();
											p.sendMessage(ChatColor.GREEN + "MySQL entries deleted successfully.");
										}
									}
									else if(!sender.hasPermission("reporter.clear")) {
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
		return true;
}
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent c) {
	    final Player p = (Player) c.getWhoClicked();
	    ItemStack clicked = c.getCurrentItem();
	    Inventory inventory = c.getInventory();
	    try {
	    	if(clicked == null || inventory.getType() == null) {
	    		log.severe("Clicked is null!");
	    	}
	    	if(clicked.getType() == null || inventory == null) {
	    		log.severe("InventoryClickEvent is null!");
	    	}
	    	if (clicked != null && clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && (inventory.getName().equals(MainMenu.getName()) || inventory.getName().equals(PlayerReport.getName()) || inventory.getName().equals(MainMenu2.getName()))) {
	    		c.setCancelled(true);
	    		p.playSound(p.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0F, 1.0F);
	    	}
	    	if (clicked != null && clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && (inventory.getName().equals(MainMenu.getName()) || inventory.getName().equals(MainMenu2.getName())) && clicked.getType() == Material.REDSTONE_BLOCK) {
	    		c.setCancelled(true);
	    		p.closeInventory();
	    	}
	    	if (clicked != null && clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && inventory.getName().equals(MainMenu.getName()) && clicked.getType().equals(Material.BOOK)) {
	    		c.setCancelled(true);
	    		p.performCommand("report list");
	    		p.closeInventory();
	    	}
	    	if (clicked != null && clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && inventory.getName().equals(MainMenu.getName()) && clicked.getType().equals(Material.GLASS)) {
	    		c.setCancelled(true);
	    		p.performCommand("report clear");
	    		p.closeInventory();
	    	}
	    	if (clicked != null && clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && (inventory.getName().equals(MainMenu.getName()) || inventory.getName().equals(MainMenu2.getName())) && (clicked.getType() == Material.PAPER)) {
	    		c.setCancelled(true);
	    	}
	    	if (clicked != null && clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && inventory.getName().equals(MainMenu.getName()) || inventory.getName().equals(MainMenu2.getName()) && clicked.getType() == Material.INK_SACK) {
	    		c.setCancelled(true);
	    	}
	    	if (clicked != null && clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && (inventory.getName().equals(MainMenu.getName()) || inventory.getName().equals(MainMenu2.getName())) && (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Report a player!") && clicked.getType() == Material.SKULL_ITEM)) {
	    		p.openInventory(PlayerReport);
	    		c.setCancelled(true);
	    	}
	    	if(clicked.getType() != null && clicked.getType() != Material.AIR && inventory.getName().equals(PlayerReport.getName()) && !clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Report a player!") && clicked.getType() == Material.SKULL_ITEM) {
	    		if(clicked.getItemMeta().getDisplayName().equals(p.getName())) {
	    			p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Invalid_Player")));
	    		}
	    		if(!clicked.getItemMeta().getDisplayName().equals(p.getName())) {
	    			final String reported = clicked.getItemMeta().getDisplayName();
	    			AnvilGUI gui = new AnvilGUI(p, new AnvilGUI.AnvilClickEventHandler() {
	    				@Override
	    				public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
	    					if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
	    						event.setWillClose(true);
	    						event.setWillDestroy(true);
	    						p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("ReportEntry") + ChatColor.DARK_AQUA + p.getName()));
	    						if(getConfig().getBoolean("MySQL_Enabled")) {
	    							try {
	    								Statement statement = connection.createStatement();
	    								statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,Reason) VALUES('" + LocalDateTime.now() + "','" + p.getName() + "','" + reported + "','" + event.getName() + "')");
	    								//statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,Reason) VALUES('2016-10-09','Player','Test','Testreason')");
	    							} catch (SQLException e) {
	    								e.printStackTrace();
	    							}
	    						}
	    						try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getDataFolder() + "/logger.txt", true)))) {
	    							out.println("[" + LocalDateTime.now() + "] " + "Reporter: " + p.getName() + ", Reported: " + reported + ", For: " + event.getName());
	    							for(Player player:Bukkit.getOnlinePlayers()) {
	    								if(player.hasPermission("reporter.reportview") && !off.contains(player.getName())) {
	    									player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + p.getName() + ChatColor.RED +  " has reported " + ChatColor.DARK_RED  + ChatColor.ITALIC + reported + ChatColor.RED + " for: " + ChatColor.AQUA + ChatColor.ITALIC + event.getName());
	    									player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
	    									TitleManager.sendTitle(player, ChatColor.translateAlternateColorCodes('&', config.getString("ReportTitle")), ChatColor.translateAlternateColorCodes('&', config.getString("ReportSubtitle")), 60);
                          					}
                          				}
                        			} catch (IOException e) {
                        				p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("LogFileError")));
                        			}
	    					} else {
	    						event.setWillClose(false);
	    						event.setWillDestroy(false);
	    					}
	    				}
	    			});
	    			ItemStack paper = new ItemStack(Material.PAPER);
	    			ItemMeta papermeta = paper.getItemMeta();
	    			papermeta.setDisplayName("Enter reason!");
	    			paper.setItemMeta(papermeta);
	    			gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, paper);
	    			try {
	    				gui.open();
	    			} catch (IllegalAccessException e) {
	    				e.printStackTrace();
	    			} catch (InvocationTargetException e) {
	    				e.printStackTrace();
	    			} catch (InstantiationException e) {
	    				e.printStackTrace();
	    			} catch (NullPointerException e) {
	    				log.severe("Caught null exception.");
	    			}
	    			
    			}
    		}
    	} finally {
    		
    	}
	}
	
    public Boolean readUUIDfile (String strFileName, Player p)  {
    	Boolean bool_uuid_present = false;
    	try {
    		File fileToRead = new File(strFileName);
    		BufferedReader br = new BufferedReader(new FileReader(fileToRead));
    		String line = null;
    		line = br.readLine();
    		while ((line = br.readLine()) != null && !bool_uuid_present) {
    			bool_uuid_present=line.equals(p.getUniqueId().toString());
    		}
    		br.close();
				} catch (FileNotFoundException e1) {
					p.sendMessage("No file is available!");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    	return bool_uuid_present;
    }		

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent s) {
		Player p = s.getPlayer();
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
        skullMeta.setOwner(p.getName());
        skullMeta.setDisplayName(p.getName());
        skullMeta.setLore(Arrays.asList(ChatColor.GRAY + "[Left Click]", ChatColor.WHITE + "Report this player!"));
        skull.setItemMeta(skullMeta);
        PlayerReport.addItem(skull);
		if (p.getName().equalsIgnoreCase("SamB440")) {
			p.sendMessage("[Statistics] " + ChatColor.AQUA + "" + ChatColor.BOLD + "This server is using Reporter!");
		}
	}
    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
    	Player p = event.getPlayer();
    	if(p == null) {
    		log.severe("PlayerQuitEvent is null!");
    	}
    	if(p != null) {
    		for (ItemStack item : PlayerReport.getContents()) {
    			if (item.getType() == Material.SKULL_ITEM && item.hasItemMeta()) {
    				if (((SkullMeta) item.getItemMeta()).getOwner().equals(event.getPlayer().getName())) {
    					PlayerReport.remove(item);
    					return;
                	}
               	}
            }
        }
    }
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent ic) {
		return;
	}
}
    private final ArrayList<String> off = new ArrayList<String>();
    public static Connection con;
    private static Connection connection;
    private String host, database, username, password;
    private int port;
    FileConfiguration config = getConfig();
	private static final Logger log = Logger.getLogger("Minecraft");
	public static Report instance;
	public static File configf;
    Inventory MainMenu = Bukkit.createInventory(null, 45, ChatColor.GREEN + "Reporter > Main Menu"); {
    // The first parameter, is the inventory owner. I make it null to let everyone use it.
    //The second parameter, is the slots in a inventory. Must be a multiple of 9. Can be up to 54.
    //The third parameter, is the inventory name. This will accept chat colors
    		ItemStack Head = new ItemStack(Material.SKULL_ITEM,1 , (byte)3);
    		SkullMeta HeadMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
    		HeadMeta.setOwner("Chewbacca");
    		HeadMeta.setDisplayName(ChatColor.RED + "Report a player!");
    		//HeadMeta.setLore(Arrays.asList("Hi, I'm Chewie, also known as ChewBacca!", "I report players for you!"));
    		Head.setItemMeta(HeadMeta);
    		ItemStack InkSack = new ItemStack(Material.INK_SACK);
    		ItemMeta InkSackMeta = InkSack.getItemMeta();
    		InkSackMeta.setDisplayName(ChatColor.GREEN + "Details");
    		InkSackMeta.setLore(Arrays.asList(ChatColor.WHITE + "Author: SamB440", ChatColor.WHITE + "Version: " + this.getDescription().getVersion(), ChatColor.WHITE + "MySQL: " + getConfig().getString("MySQL_Enabled")));
    		InkSack.setItemMeta(InkSackMeta);
    		ItemStack Paper = new ItemStack(Material.PAPER);
    		ItemMeta PaperMeta = Paper.getItemMeta();
    		PaperMeta.setDisplayName(ChatColor.GREEN + "Commands");
    		PaperMeta.setLore(Arrays.asList(ChatColor.WHITE + "report <name> <reason> - " + "Reports a player.", ChatColor.WHITE + "report list - " + "List all reports", ChatColor.WHITE + "report clear - " + "Deletes/Clears the file 'logger.txt'"));
    		Paper.setItemMeta(PaperMeta);
    		ItemStack GlassBlock = new ItemStack(Material.GLASS);
    		ItemMeta GlassBlockMeta = GlassBlock.getItemMeta();
    		GlassBlockMeta.setDisplayName(ChatColor.GREEN + "Clear reports list");
    		GlassBlockMeta.setLore(Arrays.asList(ChatColor.WHITE + "Please note: If you have used /report list recently,", ChatColor.WHITE + "then this will not work."));
    		GlassBlock.setItemMeta(GlassBlockMeta);
    		ItemStack Book = new ItemStack(Material.BOOK);
    		ItemMeta BookMeta = Book.getItemMeta();
    		BookMeta.setDisplayName(ChatColor.GREEN + "List reports");
    		Book.setItemMeta(BookMeta);
    		ItemStack RedstoneBlock = new ItemStack(Material.REDSTONE_BLOCK);
	        ItemMeta RedstoneBlockMeta = RedstoneBlock.getItemMeta();        
	        RedstoneBlockMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Exit Main Menu");
	        RedstoneBlock.setItemMeta(RedstoneBlockMeta);
	        MainMenu.setItem(4, Head);
	        MainMenu.setItem(32, InkSack);
	        MainMenu.setItem(30, Paper);
	        MainMenu.setItem(28, GlassBlock);
	        MainMenu.setItem(34, Book);
            MainMenu.setItem(40, RedstoneBlock);
    	}
    	Inventory MainMenu2 = Bukkit.createInventory(null, 45, ChatColor.GREEN + "Reporter > Player Main Menu"); {
        		ItemStack Head = new ItemStack(Material.SKULL_ITEM,1 , (byte)3);
        		SkullMeta HeadMeta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
        		HeadMeta.setOwner("Chewbacca");
        		HeadMeta.setDisplayName(ChatColor.RED + "Report a player!");
        		//HeadMeta.setLore(Arrays.asList("Hi, I'm Chewie, also known as ChewBacca!", "I report players for you!"));
        		Head.setItemMeta(HeadMeta);
        		ItemStack Paper = new ItemStack(Material.PAPER);
        		ItemMeta PaperMeta = Paper.getItemMeta();
        		PaperMeta.setDisplayName(ChatColor.GREEN + "Commands");
        		PaperMeta.setLore(Arrays.asList(ChatColor.WHITE + "report <name> <reason> - " + "Reports a player.", ChatColor.WHITE + "report list - " + "List all reports", ChatColor.WHITE + "report clear - " + "Deletes/Clears the file 'logger.txt'"));
        		Paper.setItemMeta(PaperMeta);
        		ItemStack RedstoneBlock = new ItemStack(Material.REDSTONE_BLOCK);
    	        ItemMeta RedstoneBlockMeta = RedstoneBlock.getItemMeta();        
    	        RedstoneBlockMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Exit Main Menu");
    	        RedstoneBlock.setItemMeta(RedstoneBlockMeta);
        		ItemStack InkSack = new ItemStack(Material.INK_SACK);
        		ItemMeta InkSackMeta = InkSack.getItemMeta();
        		InkSackMeta.setDisplayName(ChatColor.GREEN + "Details");
        		InkSackMeta.setLore(Arrays.asList(ChatColor.WHITE + "Author: SamB440", ChatColor.WHITE + "Version: " + this.getDescription().getVersion(), ChatColor.WHITE + "MySQL: " + getConfig().getString("MySQL_Enabled")));
        		InkSack.setItemMeta(InkSackMeta);
        		MainMenu2.setItem(30, Paper);
        		MainMenu2.setItem(40, RedstoneBlock);
        		MainMenu2.setItem(32, InkSack);
        		MainMenu2.setItem(4, Head);
        	}
        //for (Player a : Bukkit.getOnlinePlayers()) {
            //String name = a.getPlayer().getName();
            //ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
            //SkullMeta meta = (SkullMeta)item.getItemMeta();
            //meta.setDisplayName(name);
            //meta.setOwner(a.getName());
            //item.setItemMeta(meta);
            //PlayerReport.addItem(item);
          //}
        public static Inventory AnvilInput = Bukkit.createInventory(null, InventoryType.ANVIL, ChatColor.RED + "Enter Reason");
        	static {
        		//ItemStack Paper = new ItemStack(Material.PAPER);
        		//ItemMeta PaperMeta = Paper.getItemMeta();
        		//PaperMeta.setDisplayName("Paper");
        		//Paper.setItemMeta(PaperMeta);
        		//AnvilInput.setItem(2, Paper);
        		ItemStack Paper2 = new ItemStack(Material.PAPER);
        		ItemMeta PaperMeta2 = Paper2.getItemMeta();
        		PaperMeta2.setDisplayName(null);
        		Paper2.setItemMeta(PaperMeta2);
        		AnvilInput.setItem(0, Paper2);
        		//ItemStack Paper3 = new ItemStack(Material.PAPER);
        		//ItemMeta PaperMeta3 = Paper.getItemMeta();
        		//PaperMeta3.setDisplayName("Paper");
        		//Paper3.setItemMeta(PaperMeta3);
        		//AnvilInput.setItem(1, Paper3);
        	}
            Inventory PlayerReport = Bukkit.createInventory(null, 45, ChatColor.GREEN + "Reporter > Report Menu"); {
            
            }
           // Inventory ChooseReason = Bukkit.createInventory(null, 9, ChatColor.GREEN + "Choose Reason"); {
            	//Material s1 = Material.getMaterial(getConfig().getString("Reasons.Slot_2.Material"));
            	//Material s2 = Material.getMaterial(getConfig().getString("Reasons.Slot_3.Material"));
            	//Material s3 = Material.getMaterial(getConfig().getString("Reasons.Slot_4.Material"));
            	//Material s4 = Material.getMaterial(getConfig().getString("Reasons.Slot_5.Material"));
            	//Material s5 = Material.getMaterial(getConfig().getString("Reasons.Slot_6.Material"));
            	//Material s6 = Material.getMaterial(getConfig().getString("Reasons.Slot_7.Material"));
            	//Material s7 = Material.getMaterial(getConfig().getString("Reasons.Slot_8.Material"));
            	//ItemStack CustomReason = new ItemStack(Material.PAPER);
            	//ItemMeta CustomReasonMeta = CustomReason.getItemMeta();
            	//CustomReasonMeta.setDisplayName(ChatColor.RED + "Enter custom reason");
            	//CustomReasonMeta.setLore(Arrays.asList(ChatColor.GRAY + "[Left Click]"));
            	//CustomReason.setItemMeta(CustomReasonMeta);
            	//ChooseReason.addItem(CustomReason);
            	//ItemStack Slot0 = new ItemStack(Material.getMaterial(getConfig().getString("Reasons.Slot_1.Material")));
            	//ItemMeta Slot0Meta = Slot0.getItemMeta();
            	//Slot0Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Reasons.Slot_1.Name")));
            	//Slot0Meta.setLore(Arrays.asList(ChatColor.GRAY + "[Left Click]"));
            	//Slot0.setItemMeta(Slot0Meta);
            	//ChooseReason.addItem(Slot0);
            //}

	@Override
	public void onEnable() {
		
        host = getConfig().getString("Host");
        port = getConfig().getInt("Port");
        database = getConfig().getString("Database");
        username = getConfig().getString("Username");
        password = getConfig().getString("Password");
        if(getConfig().getBoolean("MySQL_Enabled")) {
        try {    
            openConnection();
			createReportTable();
			log.info("Opened connection to MySQL server and created Report Table!");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        	}
        }
        if(getConfig().getBoolean("Update_Check")) {
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
        }
		Bukkit.getPluginManager().registerEvents(this, this);
		log.info("[Report] The plugin has been enabled!");
			instance = this;
			createFiles();
			log.info("[Report] All set to go!");
		}
	@Override
	public void onDisable() {
		if(config.getBoolean("MySQL_Enabled")) {
		try {
			connection.close();
			log.info("[Report] Closed MySQL connection!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		log.info("Disabling plugin.");
		getServer().getPluginManager().disablePlugin(this);
	}
}
																		
	private void createFiles() {
		File dir = new File("plugins/Report");
		File dir2 = new File("plugins/Report/lang");
		File dir3 = new File("plugins/Report/players");
		log.info("[Report] Importing files");
		if (!dir.exists()){
			dir.mkdir();
			configf = new File(getDataFolder(), "config.yml");  				    	
	        saveDefaultConfig();
			log.info("[Report] Config file created!");
		}
		if (!dir2.exists()){
			dir2.mkdir();
		}
		if(!dir3.exists()) {
			dir3.mkdir();
		}
	}
	public void openConnection() throws SQLException, ClassNotFoundException {
	    if (connection != null && !connection.isClosed()) {
	        return;
	    }

	    synchronized (this) {
	        if (connection != null && !connection.isClosed()) {
	            return;
	        }
	        Class.forName("com.mysql.jdbc.Driver");
	        connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
	    }
	}
    public static void createReportTable() {
        try {
 
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ReportDatabase (ID INT(10) NOT NULL AUTO_INCREMENT, DATE DATETIME,REPORTER VARCHAR(16) NOT NULL,REPORTED VARCHAR(16) NOT NULL,REASON VARCHAR(100) NOT NULL, PRIMARY KEY (ID))");
            //statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,For) VALUES('TestDate','TestReporter','TestReported','TestFor'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void dropReportTable() {
    	try {
    		Statement statement = connection.createStatement();
    		statement.executeUpdate("DROP TABLE IF EXISTS ReportDatabase");
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    }
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(args.length == 2) {
			if(!args[0].equalsIgnoreCase(sender.getName()) && !cooldown.contains(sender.getName())) {
				cooldown.add(sender.getName());
	    		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
	                @Override
	                public void run() {
	                	cooldown.remove(sender.getName());
	            }
	        }, 160L);
	    			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("ReportEntry") + ChatColor.DARK_AQUA + sender.getName()));
				if(getConfig().getBoolean("MySQL_Enabled")) {
					try {
						Statement statement = connection.createStatement();
						statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,Reason) VALUES('" + LocalDateTime.now() + "','" + sender.getName() + "','" + args[0] + "','" + args[1] + "')");
						//statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,Reason) VALUES('2016-10-09','Player','Test','Testreason')");
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getDataFolder() + "/logger.txt", true)))) {
					out.println("[" + LocalDateTime.now() + "] " + "Reporter: " + sender.getName() + ", Reported: " + args[0] + ", For: " + args[1]);
                	if(getConfig().getBoolean("Bungeecord") != true) {
                		for(Player player:Bukkit.getOnlinePlayers()) {
                			if(player.hasPermission("reporter.reportview") && !off.contains(player.getName())) {
                				player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + sender.getName() + ChatColor.RED +  " has reported " + ChatColor.DARK_RED  + ChatColor.ITALIC + args[0] + ChatColor.RED + " for: " + ChatColor.AQUA + ChatColor.ITALIC + args[1]);
                				player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                				TitleManager.sendTitle(player, ChatColor.translateAlternateColorCodes('&', config.getString("ReportTitle")), ChatColor.translateAlternateColorCodes('&', config.getString("ReportSubtitle")), 60);
              				}
              			}
                	}              	
            	} catch (IOException e) {
            		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("LogFileError")));
            	}
			}
			if(args[0].equalsIgnoreCase(sender.getName())) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Invalid_Player")));
			}
			if(cooldown.contains(sender.getName())) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Cooldown")));
			}
		}
		else {
			if(args.length == 3) {
				if(!args[0].equalsIgnoreCase(sender.getName()) && !cooldown.contains(sender.getName())) {
					cooldown.add(sender.getName());
		    		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
		                @Override
		                public void run() {
		                	cooldown.remove(sender.getName());
		            }
		        }, 160L);
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("ReportEntry") + ChatColor.DARK_AQUA + sender.getName()));
					if(getConfig().getBoolean("MySQL_Enabled")) {
						try {
							Statement statement = connection.createStatement();
							statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,For) VALUES('" + LocalDateTime.now() + "','" + sender.getName() + "','" + args[0] + "','" + args[1] + "','" + args[2] + "'");
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}    
					try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getDataFolder() + "/logger.txt", true)))) {
						out.println("[" + LocalDateTime.now() + "] " + " Reporter: " + sender.getName() + ", Reported: " + args[0] + ", For: " + args[1] + " " + args[2]);
						for(Player player:Bukkit.getOnlinePlayers()) {
							if(player.hasPermission("reporter.reportview") && !off.contains(player.getName())) {
								player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + sender.getName() + ChatColor.RED +  " has reported " + ChatColor.DARK_RED  + ChatColor.ITALIC + args[0] + ChatColor.RED + " for: " + ChatColor.AQUA + ChatColor.ITALIC + args[1] + " " + args[2]);
								player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
								TitleManager.sendTitle(player, ChatColor.translateAlternateColorCodes('&', config.getString("ReportTitle")), ChatColor.translateAlternateColorCodes('&', config.getString("ReportSubtitle")), 60);
	              				}
	              			}
		            	}catch (IOException e) {
		            		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("LogFileError")));
		            	}
					}
				if(args[0].equalsIgnoreCase(sender.getName())) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Invalid_Player")));
				}
				if(cooldown.contains(sender.getName())) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Cooldown")));
				}
			}
			else {
				if(args.length == 4) {
					if(!args[0].equalsIgnoreCase(sender.getName()) && !cooldown.contains(sender.getName())) {
						cooldown.add(sender.getName());
			    		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			                @Override
			                public void run() {
			                	cooldown.remove(sender.getName());
			            }
			        }, 160L);
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("ReportEntry") + ChatColor.DARK_AQUA + sender.getName()));
						if(getConfig().getBoolean("MySQL_Enabled")) {
							try {
								Statement statement = connection.createStatement();
								statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,For) VALUES('" + LocalDateTime.now() + "','" + sender.getName() + "','" + args[0] + "','" + args[1] + "','" + args[2] + "','" + args[4] + "'");
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getDataFolder() + "/logger.txt", true)))) {
							out.println("[" + LocalDateTime.now() + "] " + "Reporter: " + sender.getName() + ", Reported: " + args[0] + ", For: " + args[1] + " " + args[2] + " " + args[3]);
							for(Player player:Bukkit.getOnlinePlayers()) {
								if(player.hasPermission("reporter.reportview") && !off.contains(player.getName())) {
									player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + sender.getName() + ChatColor.RED +  " has reported " + ChatColor.DARK_RED  + ChatColor.ITALIC + args[0] + ChatColor.RED + " for: " + ChatColor.AQUA + ChatColor.ITALIC + args[1] + " " + args[2]);
									player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
									TitleManager.sendTitle(player, ChatColor.translateAlternateColorCodes('&', config.getString("ReportTitle")), ChatColor.translateAlternateColorCodes('&', config.getString("ReportSubtitle")), 60);
		              				}
		              			}
							}catch (IOException e) {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("LogFileError")));
							}
						}
					if(args[0].equalsIgnoreCase(sender.getName())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Invalid_Player")));
					}
					if(cooldown.contains(sender.getName())) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Cooldown")));
					}
				}
				else {						
					if(args.length >= 5) {
						Player p = (Player)sender;
						p.sendMessage(ChatColor.RED + "" + ChatColor.ITALIC + "Too many args. Maximum: 3.");						
					}
					else {
					if(args.length == 0) {
						Player p = (Player)sender;
						if(p.hasPermission("Reporter.GUI.Staff")) {
							p.openInventory(MainMenu);
						}
						else if(p.hasPermission("Reporter.GUI.Player")) {
							p.openInventory(MainMenu2);
						}
					}
					else {
						if(args.length == 1) {
							if(args[0].equalsIgnoreCase("off") && !off.contains(sender.getName()) && sender.hasPermission("reporter.toggle")) {
								off.add(sender.getName());
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Toggled_Off")));
							}
							if(args[0].equalsIgnoreCase("off") && off.contains(sender.getName()) && sender.hasPermission("reporter.toggle")) {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Already_Toggled_Off")));
							}
							if(args[0].equalsIgnoreCase("on") && off.contains(sender.getName()) && sender.hasPermission("reporter.toggle")) {
								off.remove(sender.getName());
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Toggled_On")));
							}
							if(args[0].equalsIgnoreCase("on") && !off.contains(sender.getName()) && sender.hasPermission("reporter.toggle")) {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Already_Toggled_On")));
							}
							else if(args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off") && !sender.hasPermission("reporter.toggle")) {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("No_Permission")));
							}
							if(args[0].equalsIgnoreCase("list")) {
								if(sender.hasPermission("reporter.list")) {
									Player p = (Player)sender;
									p.sendMessage(ChatColor.WHITE + "" + ChatColor.STRIKETHROUGH + "--------------------------------------------");
									try {
										File fileToRead = new File("plugins/Report/logger.txt");
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
										File logger = new File("plugins/Report/logger.txt");
										File loggerbackup = new File("plugins/Report/loggerbackup.txt");
										if (loggerbackup != null) {
											p.sendMessage(ChatColor.GREEN + "Deleting existing loggerbackup.txt...");
											loggerbackup.delete();
										}
										p.sendMessage(ChatColor.GREEN + "Renaming current logger.txt to loggerbackup.txt...");
										logger.renameTo(loggerbackup);
										p.sendMessage(ChatColor.GREEN + "File 'logger.txt' deleted successfully. " + ChatColor.GRAY + "Please create a report to generate a fresh file.");
										if(getConfig().getBoolean("MySQL_Enabled")) {
											p.sendMessage(ChatColor.GREEN + "Deleting MySQL entries...");
											dropReportTable();
											p.sendMessage(ChatColor.GREEN + "MySQL entries deleted successfully.");
										}
									}
									else if(!sender.hasPermission("reporter.clear")) {
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
		return true;
}
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent c) {
	    final Player p = (Player) c.getWhoClicked();
	    ItemStack clicked = c.getCurrentItem();
	    Inventory inventory = c.getInventory();
	    if(clicked.getType() == null || inventory == null) {
	    	log.severe("InventoryClickEvent is null!");
	    }
	    if (clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && (inventory.getName().equals(MainMenu.getName()) || inventory.getName().equals(PlayerReport.getName()) || inventory.getName().equals(MainMenu2.getName()))) {
	    	c.setCancelled(true);
	    	p.playSound(p.getLocation(), Sound.BLOCK_TRIPWIRE_CLICK_ON, 1.0F, 1.0F);
	    }
    	if (clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && (inventory.getName().equals(MainMenu.getName()) || inventory.getName().equals(MainMenu2.getName())) && clicked.getType() == Material.REDSTONE_BLOCK) {
    		c.setCancelled(true);
    		p.closeInventory();
    	}
    	if (clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && inventory.getName().equals(MainMenu.getName()) && clicked.getType().equals(Material.BOOK)) {
    		c.setCancelled(true);
    		p.performCommand("report list");
    		p.closeInventory();
    	}
    	if (clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && inventory.getName().equals(MainMenu.getName()) && clicked.getType().equals(Material.GLASS)) {
    		c.setCancelled(true);
    		p.performCommand("report clear");
    		p.closeInventory();
    	}
    	if (clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && (inventory.getName().equals(MainMenu.getName()) || inventory.getName().equals(MainMenu2.getName())) && (clicked.getType() == Material.PAPER)) {
    		c.setCancelled(true);
    	}
    	if (clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && inventory.getName().equals(MainMenu.getName()) || inventory.getName().equals(MainMenu2.getName()) && clicked.getType() == Material.INK_SACK) {
    		c.setCancelled(true);
    	}
    	if (clicked.getType() != null && inventory != null && clicked.getType() != Material.AIR && (inventory.getName().equals(MainMenu.getName()) || inventory.getName().equals(MainMenu2.getName())) && (clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Report a player!") && clicked.getType() == Material.SKULL_ITEM)) {
    		p.openInventory(PlayerReport);
    		c.setCancelled(true);
    	}
    	if(clicked.getType() != null && clicked.getType() != Material.AIR && inventory.getName().equals(PlayerReport.getName()) && !clicked.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.RED + "Report a player!") && clicked.getType() == Material.SKULL_ITEM) {
    		if(clicked.getItemMeta().getDisplayName().equals(p.getName())) {
    			p.sendMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("Invalid_Player")));
    		}
    		if(!clicked.getItemMeta().getDisplayName().equals(p.getName())) {
    			final String reported = clicked.getItemMeta().getDisplayName();
    			AnvilGUI gui = new AnvilGUI(p, new AnvilGUI.AnvilClickEventHandler() {
    				@Override
    				public void onAnvilClick(AnvilGUI.AnvilClickEvent event) {
    					if (event.getSlot() == AnvilGUI.AnvilSlot.OUTPUT) {
    						event.setWillClose(true);
    						event.setWillDestroy(true);
    						p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("ReportEntry") + ChatColor.DARK_AQUA + p.getName()));
    						if(getConfig().getBoolean("MySQL_Enabled")) {
    							try {
    								Statement statement = connection.createStatement();
    								statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,Reason) VALUES('" + LocalDateTime.now() + "','" + p.getName() + "','" + reported + "','" + event.getName() + "')");
    								//statement.executeUpdate("INSERT INTO ReportDatabase(Date,Reporter,Reported,Reason) VALUES('2016-10-09','Player','Test','Testreason')");
    							} catch (SQLException e) {
    								e.printStackTrace();
    							}
    						}
    						try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(getDataFolder() + "/logger.txt", true)))) {
    							out.println("[" + LocalDateTime.now() + "] " + "Reporter: " + p.getName() + ", Reported: " + reported + ", For: " + event.getName());
    							for(Player player:Bukkit.getOnlinePlayers()) {
    								if(player.hasPermission("reporter.reportview") && !off.contains(player.getName())) {
    									player.sendMessage(ChatColor.DARK_RED + "" + ChatColor.ITALIC + p.getName() + ChatColor.RED +  " has reported " + ChatColor.DARK_RED  + ChatColor.ITALIC + reported + ChatColor.RED + " for: " + ChatColor.AQUA + ChatColor.ITALIC + event.getName());
    									player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
    									TitleManager.sendTitle(player, ChatColor.translateAlternateColorCodes('&', config.getString("ReportTitle")), ChatColor.translateAlternateColorCodes('&', config.getString("ReportSubtitle")), 60);
                          				}
                          			}
                        		} catch (IOException e) {
                        			p.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("LogFileError")));
                        		}
    					} else {
    						event.setWillClose(false);
    						event.setWillDestroy(false);
    					}
    				}
    			});
    			ItemStack paper = new ItemStack(Material.PAPER);
    			ItemMeta papermeta = paper.getItemMeta();
    			papermeta.setDisplayName("Enter reason!");
    			paper.setItemMeta(papermeta);
    			gui.setSlot(AnvilGUI.AnvilSlot.INPUT_LEFT, paper);
    			try {
    				gui.open();
    			} catch (IllegalAccessException e) {
    				e.printStackTrace();
    			} catch (InvocationTargetException e) {
    				e.printStackTrace();
    			} catch (InstantiationException e) {
    				e.printStackTrace();
    			}
    		}
    	}
	}
	
    public Boolean readUUIDfile (String strFileName, Player p)  {
    	Boolean bool_uuid_present = false;
    	try {
    		File fileToRead = new File(strFileName);
    		BufferedReader br = new BufferedReader(new FileReader(fileToRead));
    		String line = null;
    		line = br.readLine();
    		while ((line = br.readLine()) != null && !bool_uuid_present) {
    			bool_uuid_present=line.equals(p.getUniqueId().toString());
    		}
    		br.close();
				} catch (FileNotFoundException e1) {
					p.sendMessage("No file is available!");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
    	return bool_uuid_present;
    }		

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent s) {
		Player p = s.getPlayer();
        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
        skullMeta.setOwner(p.getName());
        skullMeta.setDisplayName(p.getName());
        skullMeta.setLore(Arrays.asList(ChatColor.GRAY + "[Left Click]", ChatColor.WHITE + "Report this player!"));
        skull.setItemMeta(skullMeta);
        PlayerReport.addItem(skull);
		if (p.getName().equalsIgnoreCase("SamB440")) {
			p.sendMessage("[Statistics] " + ChatColor.AQUA + "" + ChatColor.BOLD + "This server is using Reporter!");
		}
	}
    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
    	Player p = event.getPlayer();
    	if(p == null) {
    		log.severe("PlayerQuitEvent is null!");
    	}
    	if(p != null) {
    		for (ItemStack item : PlayerReport.getContents()) {
    			if (item.getType() == Material.SKULL_ITEM && item.hasItemMeta()) {
    				if (((SkullMeta) item.getItemMeta()).getOwner().equals(event.getPlayer().getName())) {
    					PlayerReport.remove(item);
    					return;
                	}
               	}
            }
        }
    }
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent ic) {
		return;
	}
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
