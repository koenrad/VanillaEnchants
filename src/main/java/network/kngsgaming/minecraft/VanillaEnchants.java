package network.kngsgaming.minecraft;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.logging.Level;

public class VanillaEnchants extends JavaPlugin {

    private File configFile = new File(getDataFolder(), "config.yml");

    public YamlConfiguration config = new YamlConfiguration();

    @Override
    public void onEnable() {
        ConsoleCommandSender console = this.getServer().getConsoleSender();
        initConfigFile();
        try {
            config.load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {

            console.sendMessage(chatPrepend() + ChatColor.RED + "[VanillaEnchants] Invalid Configuration File");
            //getLogger().info(ChatColor.RED + "Invalid Configuration File");
            //e.printStackTrace();
        }

        if ( config.getString("enable_vanilla_enchants").toLowerCase().equals("true")) {
            console.sendMessage(chatPrepend() + ChatColor.GREEN + "Enabled");
            EnchantEvents eventHandler = new EnchantEvents(this);
        } else {
            console.sendMessage(chatPrepend() + ChatColor.RED + "Disabled");
        }

    }
    @Override
    public void onDisable() {
    }

    private void initConfigFile() {
        if(!configFile.exists()){
            configFile.getParentFile().mkdirs();
            copyFile(getResource("default.config.yml"), configFile);
        }
    }

    private void copyFile(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String chatPrepend(){
        return ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "VanillaEnchants" + ChatColor.DARK_GRAY + "]" + ChatColor.RESET + " ";
    }

    public void printToConsole(String msg) {
        this.getServer().getConsoleSender().sendMessage( chatPrepend() + msg );
    }
}
