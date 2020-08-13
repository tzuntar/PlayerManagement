package com.redcreator37.playermanagement;

import com.redcreator37.playermanagement.DataModels.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.Arrays;
import java.util.List;

/**
 * Grants rewards to players on advancements
 */
public class AdvancementReward implements Listener {

    /**
     * Handles the advancement done event
     *
     * @param event the advancement done event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player p = event.getPlayer();
        ServerPlayer target = PlayerManagement.players.get(event.getPlayer()
                .getUniqueId().toString());
        if (target == null) return;

        Bukkit.getScheduler().runTask(PlayerManagement.getPlugin(PlayerManagement.class), () -> {
            // get the reward per rank
            double reward;
            String adv = event.getAdvancement().getKey().getKey();
            if (adv.contains("recipes/")) return;   // skip the recipes
            if (PlayerRoutines.checkIfContains(PlayerManagement.advRank1, adv))
                reward = PlayerManagement.rewardRank1;
            else if (PlayerRoutines.checkIfContains(PlayerManagement.advRank2, adv))
                reward = PlayerManagement.rewardRank2;
            else if (PlayerRoutines.checkIfContains(PlayerManagement.advRank3, adv))
                reward = PlayerManagement.rewardRank3;
            else return;

            PlayerManagement.eco.depositPlayer(p, reward);
            p.sendMessage(PlayerManagement.prefix + ChatColor.GOLD
                    + "Advancement made! " + ChatColor.GREEN + "$" + reward
                    + ChatColor.GOLD + " has been added to your account.");
        });
    }

    /**
     * Contains the default rank 1 advancements
     */
    private static final String defaultRank1 = "story/mine_stone;" +
            "story/upgrade_tools;" +
            "story/smelt_iron;" +
            "story/obtain_armor;" +
            "story/lava_bucket;" +
            "story/iron_tools;" +
            "story/deflect_arrow;" +
            "story/enter_the_end;" +
            "nether/return_to_sender;" +
            "nether/find_fortress;" +
            "nether/obtain_blaze_rod;" +
            "nether/summon_wither;" +
            "nether/brew_potion;" +
            "nether/create_beacon;" +
            "end/kill_dragon;" +
            "end/enter_end_gateway;" +
            "end/find_end_city;" +
            "adventure/voluntary_exile;" +
            "adventure/kill_a_mob;" +
            "adventure/trade;" +
            "adventure/ol_beatsy;" +
            "adventure/sleep_in_bed;" +
            "adventure/throw_trident;" +
            "adventure/shoot_arrow;" +
            "adventure/whos_the_pillager_now;" +
            "adventure/very_very_frightening;" +
            "husbandry/breed_an_animal;" +
            "husbandry/tame_an_animal;" +
            "husbandry/fishy_business;" +
            "husbandry/plant_seed;" +
            "husbandry/tactical_fishing";

    /**
     * Contains the default rank 2 advancements
     */
    private static final String defaultRank2 = "story/form_obsidian;" +
            "story/mine_diamond;" +
            "story/enter_the_nether;" +
            "story/enchant_item;" +
            "story/follow_ender_eye;" +
            "nether/get_wither_skull;" +
            "end/dragon_egg;" +
            "end/respawn_dragon;" +
            "end/dragon_breath;" +
            "end/elytra;" +
            "adventure/totem_of_undying;" +
            "adventure/summon_iron_golem;" +
            "adventure/sniper_duel";

    /**
     * Contains the default rank 3 advancements
     */
    private static final String defaultRank3 = "story/shiny_gear;" +
            "story/cure_zombie_villager;" +
            "nether/fast_travel;" +
            "nether/uneasy_alliance;" +
            "nether/all_potions;" +
            "nether/create_full_beacon;" +
            "nether/all_effects;" +
            "end/levitate;" +
            "adventure/hero_of_the_village;" +
            "adventure/kill_all_mobs;" +
            "adventure/two_birds_one_arrow;" +
            "adventure/arbalistic;" +
            "adventure/adventuring_time;" +
            "husbandry/bred_all_animals;" +
            "husbandry/complete_catalogue;" +
            "husbandry/balanced_diet;" +
            "husbandry/break_diamond_hoe";

    /**
     * Returns the list of default advancements for this rank
     *
     * @param rank the rank to use (1, 2 or 3)
     * @return the filled list of advancements
     */
    static List<String> getAdvancements(int rank) {
        String source = rank == 1 ? defaultRank1
                : (rank == 2 ? defaultRank2 : defaultRank3);
        return Arrays.asList(source.split(";"));
    }

}
