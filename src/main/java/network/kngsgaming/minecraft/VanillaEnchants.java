package network.kngsgaming.minecraft;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class VanillaEnchants extends JavaPlugin {

    private File configFile = new File(getDataFolder(), "config.yml");

    public YamlConfiguration config = new YamlConfiguration();
    private boolean enabled;

    @Override
    public void onEnable() {
        initConfigFile();
        try {
            config.load(configFile);
        } catch (IOException e) {
            printToConsole(ChatColor.RED + "IOException when loading config");
        } catch (InvalidConfigurationException e) {
            printToConsole(ChatColor.RED + "Invalid Configuration File");
        }

        try {
            enabled = config.getString("enable_vanilla_enchants").toLowerCase().equals("true");
        } catch (Error e) {
            printToConsole(ChatColor.RED + "Could not get config, disabling VanillaEnchants");
            enabled = false;
        }

        if (enabled) {
            EnchantEvents eventHandler = new EnchantEvents(this);
            printToConsole(ChatColor.GREEN + "Enabled");
        } else {
            printToConsole(ChatColor.RED + "Disabled");
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
