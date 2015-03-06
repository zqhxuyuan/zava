package com.interview.flag.a;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created_By: stefanie
 * Date: 15-1-13
 * Time: 下午6:33
 */
class Player {
    String name;
    boolean play;
    Player preference;
}
public class A19_PlayerTeaming {
    class Group{
        int id;
        int size = 1;
        boolean play = true;
        public Group(int id){
            this.id = id;
        }
    }
    List<Player> players;
    HashMap<Player, Group> index;
    public List<List<Player>> createTeam(List<Player> players){

        this.players = players;
        index = new HashMap();
        for(int i = 0; i < players.size(); i++) index.put(players.get(i), new Group(i));

        for(int i = 0; i < players.size(); i++){
            Player player = players.get(i);
            if(player.play){
                if(player.preference != null) union(player, player.preference);
            } else {
                Group group = find(player);
                group.play = false;
            }
        }

        HashMap<Integer, List<Player>> groups = new HashMap();
        for(int i = 0; i < players.size(); i++){
            Group group = find(players.get(i));
            if(group.play){
                if(groups.containsKey(group.id)){
                    groups.get(group.id).add(players.get(i));
                } else {
                    List<Player> groupPlayers = new ArrayList();
                    groupPlayers.add(players.get(i));
                    groups.put(group.id, groupPlayers);
                }
            }
        }


        return null;
    }

    public Group find(Player player){
        Group group = index.get(player);
        while(!players.get(group.id).equals(player)){
            player = players.get(group.id);
            group = index.get(player);
        }
        return group;
    }

    public void union(Player p1, Player p2){
        Group group1 = find(p1);
        Group group2 = find(p2);
        if(group1.id == group2.id) return;
        if(group1.size < group2.size){
            group1.id = group2.id;
            group2.size += group1.size;
            group2.play = group1.play && group2.play;
        } else {
            group2.id = group1.id;
            group1.size += group2.size;
            group1.play = group1.play && group2.play;
        }
    }
}
