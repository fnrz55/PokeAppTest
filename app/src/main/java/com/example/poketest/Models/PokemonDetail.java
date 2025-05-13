package com.example.poketest.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PokemonDetail {
    private int id;
    private String name;
    private int height;
    private int weight;
    private Sprites sprites;
    private List<AbilityEntry> abilities;
    private List<StatEntry> stats;

    public int getId() { return id; }
    public String getName() { return name; }
    public int getHeight() { return height; }
    public int getWeight() { return weight; }
    public Sprites getSprites() { return sprites; }
    public List<AbilityEntry> getAbilities() { return abilities; }
    public List<StatEntry> getStats() { return stats; }

    public static class Sprites {
        @SerializedName("front_default")
        private String frontDefault;

        public String getFrontDefault() { return frontDefault; }
    }

    public static class AbilityEntry {
        private Ability ability;

        public Ability getAbility() { return ability; }
    }

    public static class Ability {
        private String name;
        public String getName() { return name; }
    }

    public static class StatEntry {
        @SerializedName("base_stat")
        private int baseStat;
        private Stat stat;

        public int getBaseStat() { return baseStat; }
        public Stat getStat() { return stat; }
    }

    public static class Stat {
        private String name;
        public String getName() { return name; }
    }
}
