package com.zhyrapian.kitpvp;

import com.zhyrapian.kitpvp.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.List;

public class Scoreboard {

    private Scoreboard(){
        throw new IllegalStateException("Scoreboard class");
    }

    public static void getScoreboard(Player target){

        List<Object> playerInfo;
        playerInfo = PlayerManager.getPlayerInfo(target);

        ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
        assert scoreboardManager != null;
        org.bukkit.scoreboard.Scoreboard scoreboard = scoreboardManager.getNewScoreboard();

        Objective objective = scoreboard.registerNewObjective("kd", "dummy", Utils.textColor("&4&l") + "[DDG] " + Utils.textColor("&f&l") + "KitPVP");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        //Set variables
        Score displayName = objective.getScore(Utils.textColor("&f") + "Username: ");
        Score username = objective.getScore(Utils.textColor("&6") + "» " + target.getName());
        Score empty = objective.getScore("");
        Score kills = objective.getScore(Utils.textColor("&f") + "Kills: " + Utils.textColor("&6") + playerInfo.get(1));
        Score deaths = objective.getScore(Utils.textColor("&f") + "Deaths: " + Utils.textColor("&6") + playerInfo.get(2));
        Score ratio;
        if((int)playerInfo.get(1) >= 1 && (int)playerInfo.get(2) >= 1 ) {
            ratio = objective.getScore(Utils.textColor("&f") + "Ratio: " + Utils.textColor("&6") + playerInfo.get(3));
        }else {
            ratio = objective.getScore(Utils.textColor("&f") + "Ratio: " + Utils.textColor("&6") + "-");
        }
        Score money = objective.getScore(Utils.textColor("&f") + "Money: " + Utils.textColor("&6") + "$" + playerInfo.get(4).toString());

        //Set scores
        displayName.setScore(10);
        username.setScore(9);
        empty.setScore(8);
        kills.setScore(6);
        deaths.setScore(5);
        ratio.setScore(3);
        money.setScore(1);

        target.setScoreboard(scoreboard);
    }
}
