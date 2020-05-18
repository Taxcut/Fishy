package me.fishy.abilities.commands;

import me.fishy.abilities.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.fishy.abilities.utils.CC;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class fAbilitiesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {


        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!(player.hasPermission("fabilities.admin"))) {
                player.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Permission")));
                return true;
            }
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                Main.getInstance().reloadConfig();
                sender.sendMessage(CC.translate("&cYou have reloaded the config.yml."));
                return true;
            }
            if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(CC.MENU_BAR);
                sender.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Colors.Main-Color") + "Abilities List:"));
                sender.sendMessage("");
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " Snowport"));
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " Cocaine"));
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " PocketBard"));
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " Tazer"));
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " SwitchStick"));
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " InstantInvis"));
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " InstantCrapple"));
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " Rocket&7"));
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " SwapperAxe&7"));
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " PearlReset"));
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " FastPearl"));
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " GrapplingHook"));
                sender.sendMessage(CC.translate("&7-" + Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + " Anti-BuildEgg"));
                sender.sendMessage(CC.MENU_BAR);
                return true;
            }
        }


        if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(CC.translate("&cThat player is offline!"));
                return true;
            }
            if (args[0].equalsIgnoreCase("resetcooldown")) {
                switch (args[2].toLowerCase()) {
                    case "cocaine":
                        Main.getInstance().getCocaine().cooldownRemove(target);
                        sender.sendMessage(CC.translate("&cYou have reset %player%'s cocaine cooldown!").replace("%player%", target.getName()));
                        break;
                    case "snowport":
                        Main.getInstance().getSnowport().cooldownRemove(target);
                        sender.sendMessage(CC.translate("&cYou have reset %player%'s snowport cooldown!").replace("%player%", target.getName()));
                        break;
                    case "instantcrapple":
                        Main.getInstance().getInstantcrapple().cooldownRemove(target);
                        sender.sendMessage(CC.translate("&cYou have reset %player%'s instantcrapple cooldown!").replace("%player%", target.getName()));
                        break;
                    case "tazer":
                        Main.getInstance().getTazer().cooldownRemove(target);
                        sender.sendMessage(CC.translate("&cYou have reset %player%'s tazer cooldown!").replace("%player%", target.getName()));
                        break;
                    case "instantinvis":
                        Main.getInstance().getInstantinvis().cooldownRemove(target);
                        sender.sendMessage(CC.translate("&cYou have reset %player%'s instantinvis cooldown!").replace("%player%", target.getName()));
                        break;
                    case "switchstick":
                        Main.getInstance().getSwitchstick().cooldownRemove(target);
                        sender.sendMessage(CC.translate("&cYou have reset %player%'s switchstick cooldown!").replace("%player%", target.getName()));
                        break;
                    case "pocketbard":
                        Main.getInstance().getPocketbard().cooldownRemove(target);
                        sender.sendMessage(CC.translate("&cYou have reset %player%'s pocketbard cooldown!").replace("%player%", target.getName()));
                        break;
                    case "rocket":
                        Main.getInstance().getRocket().cooldownRemove(target);
                        sender.sendMessage(CC.translate("&cYou have reset %player%'s rocket cooldown!").replace("%player%", target.getName()));
                        break;
                    case "swapperaxe":
                        Main.getInstance().getSwapperaxe().cooldownRemove(target);
                        sender.sendMessage(CC.translate("&cYou have reset %player%'s swapperaxe cooldown!").replace("%player%", target.getName()));
                        break;
                    case "pearlreset":
                        Main.getInstance().getPearlreset().cooldownRemove(target);
                        sender.sendMessage(CC.translate("&cYou have reset %player%'s pearl reset cooldown!").replace("%player%", target.getName()));
                        break;
                    case "antibuildegg":
                        Main.getInstance().getAntibuildegg().cooldownRemove(target);
                        sender.sendMessage(CC.translate("&cYou have reset %player%'s anti-buildegg cooldown!").replace("%player%", target.getName()));
                        break;
                    //case "grapplinghook":
                       // Main.getInstance().getGrapplinghook().cooldownRemove(target);
                        //sender.sendMessage(CC.translate("&cYou have reset %player%'s grapplinghook cooldown!").replace("%player%", target.getName()));
                       // break;
                    default:
                        sender.sendMessage(CC.translate("&cThat's not a valid ability item."));
                }
                return true;
            }
            int amount;

            try {
                amount = Integer.parseInt(args[2]);
            } catch (NumberFormatException ex) {
                sender.sendMessage(CC.translate("&cThat's not a valid number!"));
                return true;
            }

            if (args[0].equalsIgnoreCase("snowport")) {
                ItemStack snowport = new ItemStack(Material.SNOW_BALL, amount);
                ItemMeta snowportMeta = snowport.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("Snowport.Lore")));
                snowportMeta.setLore(lore);
                snowportMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("Snowport.Name")));
                snowport.setItemMeta(snowportMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", snowportMeta.getDisplayName())));
                target.getInventory().addItem(snowport);
                return true;
            }


            if (args[0].equalsIgnoreCase("pocketbard")) {
                ItemStack pocketbard = new ItemStack(Material.GOLD_NUGGET, amount);
                ItemMeta pocketbardMeta = pocketbard.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("PocketBard.Lore")));
                pocketbardMeta.setLore(lore);
                pocketbardMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("PocketBard.Name")));
                pocketbard.setItemMeta(pocketbardMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", pocketbardMeta.getDisplayName())));
                target.getInventory().addItem(pocketbard);
                return true;
            }


            if (args[0].equalsIgnoreCase("cocaine")) {
                ItemStack cocaine = new ItemStack(Material.SUGAR, amount);
                ItemMeta cocaineMeta = cocaine.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("Cocaine.Lore")));
                cocaineMeta.setLore(lore);
                cocaineMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("Cocaine.Name")));
                cocaine.setItemMeta(cocaineMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", cocaineMeta.getDisplayName())));
                target.getInventory().addItem(cocaine);
                return true;
            }


            if (args[0].equalsIgnoreCase("tazer")) {
                ItemStack tazer = new ItemStack(Material.BLAZE_ROD, amount);
                ItemMeta tazerMeta = tazer.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("Tazer.Lore")));
                tazerMeta.setLore(lore);
                tazerMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("Tazer.Name")));
                tazer.setItemMeta(tazerMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", tazerMeta.getDisplayName())));
                target.getInventory().addItem(tazer);
                return true;
            }


            if (args[0].equalsIgnoreCase("switchstick")) {
                ItemStack switchstick = new ItemStack(Material.STICK, amount);
                ItemMeta switchstickMeta = switchstick.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("SwitchStick.Lore")));
                switchstickMeta.setLore(lore);
                switchstickMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("SwitchStick.Name")));
                switchstick.setItemMeta(switchstickMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", switchstickMeta.getDisplayName())));
                target.getInventory().addItem(switchstick);
                return true;
            }
            if (args[0].equalsIgnoreCase("instantinvis")) {
                ItemStack instantinvis = new ItemStack(Material.INK_SACK, amount);
                ItemMeta instantinvisMeta = instantinvis.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("InstantInvis.Lore")));
                instantinvisMeta.setLore(lore);
                instantinvisMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("InstantInvis.Name")));
                instantinvis.setItemMeta(instantinvisMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", instantinvisMeta.getDisplayName())));
                target.getInventory().addItem(instantinvis);
                return true;
            }
            if (args[0].equalsIgnoreCase("instantcrapple")) {
                ItemStack instantcrapple = new ItemStack(Material.GOLDEN_APPLE, amount);
                ItemMeta instantcrappleMeta = instantcrapple.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("InstantCrapple.Lore")));
                instantcrappleMeta.setLore(lore);
                instantcrappleMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("InstantCrapple.Name")));
                instantcrapple.setItemMeta(instantcrappleMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", instantcrappleMeta.getDisplayName())));
                target.getInventory().addItem(instantcrapple);
                return true;
            }
            if (args[0].equalsIgnoreCase("rocket")) {
                ItemStack rocket = new ItemStack(Material.FIREWORK, amount);
                ItemMeta rocketMeta = rocket.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("Rocket.Lore")));
                rocketMeta.setLore(lore);
                rocketMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("Rocket.Name")));
                rocket.setItemMeta(rocketMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", rocketMeta.getDisplayName())));
                target.getInventory().addItem(rocket);
                return true;
            }
            if (args[0].equalsIgnoreCase("swapperaxe")) {
                ItemStack swapperaxe = new ItemStack(Material.GOLD_AXE, amount);
                ItemMeta swapperaxeMeta = swapperaxe.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("SwapperAxe.Lore")));
                swapperaxeMeta.setLore(lore);
                swapperaxeMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("SwapperAxe.Name")));
                swapperaxe.setItemMeta(swapperaxeMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", swapperaxeMeta.getDisplayName())));
                target.getInventory().addItem(swapperaxe);
                return true;
            }
            if (args[0].equalsIgnoreCase("pearlreset")) {
                ItemStack pearlreset = new ItemStack(Material.FEATHER, amount);
                ItemMeta pearlresetMeta = pearlreset.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("PearlReset.Lore")));
                pearlresetMeta.setLore(lore);
                pearlresetMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("PearlReset.Name")));
                pearlreset.setItemMeta(pearlresetMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", pearlresetMeta.getDisplayName())));
                target.getInventory().addItem(pearlreset);
                return true;
            }
            if (args[0].equalsIgnoreCase("fastpearl")) {
                ItemStack fastpearl = new ItemStack(Material.ENDER_PEARL, amount);
                ItemMeta fastpearlMeta = fastpearl.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("FastPearl.Lore")));
                fastpearlMeta.setLore(lore);
                fastpearlMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("FastPearl.Name")));
                fastpearl.setItemMeta(fastpearlMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", fastpearlMeta.getDisplayName())));
                target.getInventory().addItem(fastpearl);
                return true;
            }
            if (args[0].equalsIgnoreCase("antibuildegg")) {
                ItemStack antibuildegg = new ItemStack(Material.EGG, amount);
                ItemMeta antibuildeggMeta = antibuildegg.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("Anti-BuildEgg.Lore")));
                antibuildeggMeta.setLore(lore);
                antibuildeggMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("Anti-BuildEgg.Name")));
                antibuildegg.setItemMeta(antibuildeggMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", antibuildeggMeta.getDisplayName())));
                target.getInventory().addItem(antibuildegg);
                return true;
            }
            /*if (args[0].equalsIgnoreCase("grapplinghook")) {
                ItemStack grapplinghook = new ItemStack(Material.FISHING_ROD, amount);
                ItemMeta grapplinghookMeta = grapplinghook.getItemMeta();
                ArrayList<String> lore = new ArrayList<>();
                lore.add(CC.translate(Main.getInstance().getConfig().getString("GrapplingHook.Lore")));
                grapplinghookMeta.setLore(lore);
                grapplinghookMeta.setDisplayName(CC.translate(Main.getInstance().getConfig().getString("GrapplingHook.Name")));
                grapplinghook.setItemMeta(grapplinghookMeta);
                target.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.GiveMessage").replace("%ability%", grapplinghookMeta.getDisplayName())));
                target.getInventory().addItem(grapplinghook);
                return true;
            }

             */

            sendUsage(sender);
            return true;
        }
        sendUsage(sender);
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(CC.MENU_BAR);
        sender.sendMessage("");
        sender.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Colors.Main-Color") + "fAblities"));
        sender.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + "Version: 0.1"));
        sender.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Colors.Secondary-Color") + "Author: FishyDevelopment"));
        sender.sendMessage("");
        sender.sendMessage(CC.translate(Main.getInstance().getConfig().getString("Main.Colors.Main-Color") + "Commands:"));
        sender.sendMessage(CC.translate("&c/fAblities - Displays this message."));
        sender.sendMessage(CC.translate("&c/fAbilities <ability> <player> <amount> - Gives a player an ability item."));
        sender.sendMessage(CC.translate("&c/fAbilities resetcooldown <player> <ability> - Resets a player's ability cooldown."));
        sender.sendMessage(CC.translate("&c/fAbilities list - Lists all of the ability items."));
        sender.sendMessage(CC.translate("&c/fAbilities reload - Reloads the config.yml."));
        sender.sendMessage("");
        sender.sendMessage(CC.MENU_BAR);
    }

}

