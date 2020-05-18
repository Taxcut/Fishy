

package com.fishy.hcf.hooks;

import com.fishy.hcf.HCF;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook {

    @Getter
    private boolean hooked;
    private Chat chat;

    public VaultHook(HCF plugin){
        Plugin vaultPlugin;

        if((vaultPlugin = plugin.getServer().getPluginManager().getPlugin("Vault")) == null || !vaultPlugin.isEnabled()){
            return;
        }


        RegisteredServiceProvider<Chat> chatRegisteredServiceProvider = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        if(chatRegisteredServiceProvider == null){
            return;
        }

        chat = chatRegisteredServiceProvider.getProvider();

        hooked = true;
    }



    public Chat getChat(){
        if(!hooked){
            Plugin vaultPlugin;

            if((vaultPlugin = HCF.getPlugin().getServer().getPluginManager().getPlugin("Vault")) == null || !vaultPlugin.isEnabled()){
                throw new RuntimeException();
            }

            RegisteredServiceProvider<Chat> chatRegisteredServiceProvider = HCF.getPlugin().getServer().getServicesManager().getRegistration(Chat.class);
            if(chatRegisteredServiceProvider == null){
                throw new RuntimeException();
            }

            chat = chatRegisteredServiceProvider.getProvider();

            hooked = true;
        }

        return chat;
    }
}
