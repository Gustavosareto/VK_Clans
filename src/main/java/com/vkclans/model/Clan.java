package com.vkclans.model;

import com.vkclans.util.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import java.util.*;

/**
 * Representa um clã completo com todos os sistemas avançados
 */
public class Clan {
    private final String name;
    private final String tag;  // Tag sem cores para comparações
    private String coloredTag; // Tag com cores para exibição
    private UUID leader;
    private final Map<UUID, ClanRole> members = new HashMap<>();
    private final Set<UUID> invites = new HashSet<>();
    private Location base;
    
    // Estatísticas
    private int kills;
    private int deaths;
    private int points;
    private int wins;
    private int losses;
    
    // Sistema de nível
    private int level;
    private int experience;
    
    // Sistema de banco
    private double bankBalance;
    
    // Metadata
    private long creationDate;

    public Clan(String name, String tag, UUID leader) {
        this.name = name;
        // Remove cores para comparações mas guarda original
        this.coloredTag = tag;
        this.tag = stripColors(tag).toUpperCase();
        this.leader = leader;
        this.members.put(leader, ClanRole.DONO);
        this.creationDate = System.currentTimeMillis();
        this.level = 1;
        this.experience = 0;
        this.bankBalance = 0;
        this.wins = 0;
        this.losses = 0;
    }
    
    /**
     * Remove códigos de cor de uma string
     */
    private static String stripColors(String input) {
        if (input == null) return "";
        return input.replaceAll("&[0-9a-fA-Fk-oK-OrR]", "").replaceAll("§[0-9a-fA-Fk-oK-OrR]", "");
    }
    
    /**
     * Converte & para § nas cores
     */
    public static String translateColors(String input) {
        if (input == null) return "";
        return input.replace('&', '§');
    }

    // ==================== GETTERS BÁSICOS ====================
    public String getName() { return name; }
    public String getTag() { return tag; }
    
    /**
     * Retorna a tag com cores formatadas (§ em vez de &)
     */
    public String getColoredTag() { 
        return translateColors(coloredTag != null ? coloredTag : tag); 
    }
    
    /**
     * Retorna a tag original como foi digitada (com &)
     */
    public String getRawColoredTag() { 
        return coloredTag != null ? coloredTag : tag; 
    }
    
    /**
     * Define a tag colorida
     */
    public void setColoredTag(String coloredTag) { 
        this.coloredTag = coloredTag; 
    }
    public UUID getLeader() { return leader; }
    public Location getBase() { return base; }
    public int getKills() { return kills; }
    public int getDeaths() { return deaths; }
    public int getPoints() { return points; }
    public int getWins() { return wins; }
    public int getLosses() { return losses; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public double getBankBalance() { return bankBalance; }
    public long getCreationDate() { return creationDate; }
    public Set<UUID> getInvites() { return invites; }

    // ==================== SETTERS ====================
    public void setBase(Location base) { this.base = base; }
    public void setKills(int kills) { this.kills = kills; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void setPoints(int points) { this.points = points; }
    public void setWins(int wins) { this.wins = wins; }
    public void setLosses(int losses) { this.losses = losses; }
    public void setLevel(int level) { this.level = level; }
    public void setExperience(int experience) { this.experience = experience; }
    public void setBankBalance(double bankBalance) { this.bankBalance = bankBalance; }
    public void setCreationDate(long creationDate) { this.creationDate = creationDate; }

    // ==================== MEMBROS ====================
    
    public Set<UUID> getMembers() {
        return members.keySet();
    }

    public Map<UUID, ClanRole> getMemberRoles() {
        return members;
    }

    public ClanRole getMemberRole(UUID uuid) {
        return members.get(uuid);
    }

    public boolean isMember(UUID uuid) {
        return members.containsKey(uuid);
    }

    public void addMember(UUID uuid) {
        members.put(uuid, ClanRole.MEMBRO);
    }

    public void removeMember(UUID uuid) {
        members.remove(uuid);
    }

    public void setMemberRole(UUID uuid, ClanRole role) {
        if (members.containsKey(uuid)) {
            members.put(uuid, role);
            if (role == ClanRole.DONO) {
                if (leader != null && !leader.equals(uuid)) {
                    members.put(leader, ClanRole.SUB_DONO);
                }
                leader = uuid;
            }
        }
    }

    public boolean promoteMember(UUID uuid) {
        ClanRole currentRole = members.get(uuid);
        if (currentRole == null || currentRole == ClanRole.DONO) return false;
        ClanRole nextRole = currentRole.getNextRole();
        if (nextRole != null && nextRole != ClanRole.DONO) {
            members.put(uuid, nextRole);
            return true;
        }
        return false;
    }

    public boolean demoteMember(UUID uuid) {
        ClanRole currentRole = members.get(uuid);
        if (currentRole == null || currentRole == ClanRole.MEMBRO || currentRole == ClanRole.DONO) return false;
        ClanRole prevRole = currentRole.getPreviousRole();
        if (prevRole != null) {
            members.put(uuid, prevRole);
            return true;
        }
        return false;
    }

    public List<UUID> getMembersByRole(ClanRole role) {
        List<UUID> result = new ArrayList<>();
        for (Map.Entry<UUID, ClanRole> entry : members.entrySet()) {
            if (entry.getValue() == role) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public List<UUID> getSubDonos() {
        return getMembersByRole(ClanRole.SUB_DONO);
    }

    public List<UUID> getAdmins() {
        return getMembersByRole(ClanRole.ADMINISTRADOR);
    }

    // ==================== PONTOS E ESTATÍSTICAS ====================

    public void addPoints(int amount) {
        this.points += amount;
    }

    public void removePoints(int amount) {
        this.points = Math.max(0, this.points - amount);
    }

    public void addKill() {
        this.kills++;
    }

    public void addDeath() {
        this.deaths++;
    }

    public void addWin() {
        this.wins++;
    }

    public void addLoss() {
        this.losses++;
    }

    public double getKDR() {
        return deaths == 0 ? kills : (double) kills / deaths;
    }

    public double getWinRate() {
        int total = wins + losses;
        return total == 0 ? 0 : (double) wins / total * 100;
    }

    // ==================== NÍVEL E EXPERIÊNCIA ====================

    public void addLevel(int amount) {
        this.experience += amount;
    }

    // ==================== BANCO ====================

    public void addBankBalance(double amount) {
        this.bankBalance += amount;
    }

    public void removeBankBalance(double amount) {
        this.bankBalance = Math.max(0, this.bankBalance - amount);
    }

    // ==================== LIDERANÇA ====================

    public void transferLeadership(UUID newLeader) {
        if (!members.containsKey(newLeader)) return;
        members.put(leader, ClanRole.SUB_DONO);
        members.put(newLeader, ClanRole.DONO);
        leader = newLeader;
    }

    // ==================== SERIALIZAÇÃO ====================

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("tag", tag);
        map.put("coloredTag", coloredTag != null ? coloredTag : tag);
        map.put("leader", leader.toString());
        map.put("creationDate", creationDate);
        
        // Estatísticas
        map.put("kills", kills);
        map.put("deaths", deaths);
        map.put("points", points);
        map.put("wins", wins);
        map.put("losses", losses);
        
        // Nível
        map.put("level", level);
        map.put("experience", experience);
        
        // Banco
        map.put("bankBalance", bankBalance);
        
        // Membros com cargos
        Map<String, String> memberMap = new HashMap<>();
        for (Map.Entry<UUID, ClanRole> entry : members.entrySet()) {
            memberMap.put(entry.getKey().toString(), entry.getValue().name());
        }
        map.put("members", memberMap);
        
        // Convites
        List<String> inviteList = new ArrayList<>();
        for (UUID uuid : invites) inviteList.add(uuid.toString());
        map.put("invites", inviteList);
        
        // Base - só salva se não for null
        if (base != null) {
            Map<String, Object> baseData = LocationUtil.serialize(base);
            if (baseData != null) {
                map.put("base", baseData);
            }
        }
        
        return map;
    }

    public static Clan deserialize(ConfigurationSection section) {
        if (section == null) return null;
        
        String name = section.getString("name");
        String tag = section.getString("tag");
        UUID leader = UUID.fromString(section.getString("leader"));
        
        String coloredTag = section.getString("coloredTag", tag);
        Clan clan = new Clan(name, coloredTag, leader);
        clan.members.clear();
        
        // Membros com cargos
        ConfigurationSection membersSection = section.getConfigurationSection("members");
        if (membersSection != null) {
            for (String uuidStr : membersSection.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                String roleName = membersSection.getString(uuidStr);
                ClanRole role = ClanRole.valueOf(roleName);
                clan.members.put(uuid, role);
            }
        } else {
            // Compatibilidade com formato antigo
            List<String> memberList = section.getStringList("members");
            for (String s : memberList) {
                UUID uuid = UUID.fromString(s);
                if (uuid.equals(leader)) {
                    clan.members.put(uuid, ClanRole.DONO);
                } else {
                    clan.members.put(uuid, ClanRole.MEMBRO);
                }
            }
        }
        
        // Convites
        List<String> inviteList = section.getStringList("invites");
        for (String s : inviteList) clan.invites.add(UUID.fromString(s));
        
        // Estatísticas
        clan.kills = section.getInt("kills", 0);
        clan.deaths = section.getInt("deaths", 0);
        clan.points = section.getInt("points", 0);
        clan.wins = section.getInt("wins", 0);
        clan.losses = section.getInt("losses", 0);
        
        // Nível
        clan.level = section.getInt("level", 1);
        clan.experience = section.getInt("experience", 0);
        
        // Banco
        clan.bankBalance = section.getDouble("bankBalance", 0);
        
        // Metadata
        clan.creationDate = section.getLong("creationDate", System.currentTimeMillis());
        
        // Base
        if (section.contains("base")) {
            clan.base = LocationUtil.deserialize(section.getConfigurationSection("base"));
        }
        
        return clan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clan clan = (Clan) o;
        return name.equals(clan.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
