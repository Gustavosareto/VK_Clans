# 🔌 VKClans API - Documentação para Desenvolvedores

Esta documentação explica como integrar outros plugins com o VKClans usando a API pública.

## 📋 Índice

1. [Obtendo a API](#obtendo-a-api)
2. [Verificações de Clã](#verificações-de-clã)
3. [Informações do Clã](#informações-do-clã)
4. [Sistema de Membros](#sistema-de-membros)
5. [Sistema de Pontos](#sistema-de-pontos)
6. [Sistema de Economia](#sistema-de-economia)
7. [Sistema de Níveis](#sistema-de-níveis)
8. [Sistema de Guerra](#sistema-de-guerra)
9. [Sistema de Ranking](#sistema-de-ranking)
10. [Sistema de Missões](#sistema-de-missões)
11. [Sistema de Chat](#sistema-de-chat)
12. [Estatísticas](#estatísticas)
13. [Eventos Personalizados](#eventos-personalizados)
14. [Exemplos Completos](#exemplos-completos)

---

## Obtendo a API

### Maven Dependency

```xml
<dependency>
    <groupId>com.VKClans</groupId>
    <artifactId>VKClans</artifactId>
    <version>1.2.0</version>
    <scope>provided</scope>
</dependency>
```

### plugin.yml

```yaml
depend: [VKClans]
# ou
softdepend: [VKClans]
```

### Código Java

```java
import com.VKClans.api.VKClansAPI;

public class MeuPlugin extends JavaPlugin {
    
    private VKClansAPI VKClansAPI;
    
    @Override
    public void onEnable() {
        // Verificar se VKClans está presente
        if (getServer().getPluginManager().getPlugin("VKClans") != null) {
            VKClansAPI = VKClansAPI.getInstance();
            getLogger().info("VKClans API conectada!");
        } else {
            getLogger().warning("VKClans não encontrado!");
        }
    }
    
    public VKClansAPI getVKClansAPI() {
        return VKClansAPI;
    }
}
```

---

## Verificações de Clã

### Verificar se jogador tem clã

```java
UUID playerUUID = player.getUniqueId();
Clan clan = api.getPlayerClan(playerUUID);

if (clan != null) {
    // Jogador tem clã
    player.sendMessage("Você está no clã: " + clan.getName());
} else {
    // Jogador não tem clã
    player.sendMessage("Você não tem um clã!");
}
```

### Verificar se clã existe

```java
// Por nome
boolean existsByName = api.clanExists("MeuCla");

// Por tag
Clan clanByTag = api.getClanByTag("TAG");
boolean existsByTag = clanByTag != null;
```

### Verificar se jogadores estão no mesmo clã

```java
UUID player1 = player1.getUniqueId();
UUID player2 = player2.getUniqueId();

if (api.areInSameClan(player1, player2)) {
    // Estão no mesmo clã - talvez desabilitar PvP?
}
```

---

## Informações do Clã

### Obter informações básicas

```java
Clan clan = api.getPlayerClan(playerUUID);

// Informações básicas
String nome = clan.getName();           // "MeuCla"
String tag = clan.getTag();             // "TAG"
UUID lider = clan.getLeader();          // UUID do líder
int membros = clan.getMembers().size(); // Quantidade de membros

// Ou diretamente pelo UUID do jogador
String clanName = api.getClanName(playerUUID);
String clanTag = api.getClanTag(playerUUID);
```

### Listar todos os clãs

```java
List<Clan> todosClans = api.getAllClans();

for (Clan clan : todosClans) {
    System.out.println(clan.getName() + " - " + clan.getMembers().size() + " membros");
}
```

### Obter membros do clã

```java
List<UUID> membros = api.getClanMembers(clan);

for (UUID membroUUID : membros) {
    Player membro = Bukkit.getPlayer(membroUUID);
    if (membro != null && membro.isOnline()) {
        // Membro está online
    }
}
```

---

## Sistema de Membros

### Verificar cargo do membro

```java
ClanRole cargo = api.getMemberRole(playerUUID);

switch (cargo) {
    case DONO:
        // É o dono do clã
        break;
    case SUB_DONO:
        // É sub-dono
        break;
    case ADMINISTRADOR:
        // É administrador
        break;
    case MEMBRO:
        // É membro comum
        break;
}
```

### Verificar se é líder

```java
if (api.isClanLeader(playerUUID)) {
    // Jogador é líder do seu clã
}
```

### Verificar permissões do cargo

```java
ClanRole role = api.getMemberRole(playerUUID);

if (role.canInvite()) {
    // Pode convidar membros
}

if (role.canKick()) {
    // Pode expulsar membros
}

if (role.canSetBase()) {
    // Pode definir a base
}

if (role.canManageMembers()) {
    // Pode gerenciar membros (promover, rebaixar, etc)
}

if (role.canDelete()) {
    // Pode deletar o clã (apenas DONO)
}
```

---

## Sistema de Pontos

### Manipular pontos

```java
Clan clan = api.getPlayerClan(playerUUID);

// Obter pontos
int pontos = api.getPoints(clan);

// Adicionar pontos
api.addPoints(clan, 100);

// Remover pontos
api.removePoints(clan, 50);

// Definir pontos
api.setPoints(clan, 1000);
```

### Exemplo: Recompensar clã por evento

```java
@EventHandler
public void onCustomEvent(MeuEventoCustomizado event) {
    Player player = event.getPlayer();
    Clan clan = api.getPlayerClan(player.getUniqueId());
    
    if (clan != null) {
        api.addPoints(clan, 50);
        player.sendMessage("Seu clã ganhou 50 pontos!");
    }
}
```

---

## Sistema de Economia

### Verificar saldo do banco

```java
Clan clan = api.getPlayerClan(playerUUID);
double saldo = api.getClanBankBalance(clan);

player.sendMessage("Saldo do clã: $" + saldo);
```

### Manipular banco

```java
// Adicionar dinheiro ao banco
api.addToClanBank(clan, 1000.0);

// Remover dinheiro do banco
boolean sucesso = api.removeFromClanBank(clan, 500.0);
if (!sucesso) {
    player.sendMessage("Saldo insuficiente!");
}
```

### Verificar se banco está habilitado

```java
// Banco depende do Vault estar instalado
if (BankManager.getInstance().isEnabled()) {
    // Sistema de banco disponível
}
```

---

## Sistema de Níveis

### Obter informações de nível

```java
Clan clan = api.getPlayerClan(playerUUID);

int nivel = api.getClanLevel(clan);
int xp = api.getClanExperience(clan);
int maxMembros = api.getMemberLimit(clan);

player.sendMessage("Nível: " + nivel);
player.sendMessage("XP: " + xp);
player.sendMessage("Máximo de membros: " + maxMembros);
```

### Adicionar experiência

```java
// Adicionar XP ao clã
api.addExperience(clan, 500);

// O sistema automaticamente verifica level up
```

### Exemplo: XP por minério

```java
@EventHandler
public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Block block = event.getBlock();
    
    if (block.getType() == Material.DIAMOND_ORE) {
        Clan clan = api.getPlayerClan(player.getUniqueId());
        if (clan != null) {
            api.addExperience(clan, 50);
            api.registerBlocksMined(player.getUniqueId(), 1);
        }
    }
}
```

---

## Sistema de Guerra

### Verificar se clãs estão em guerra

```java
Clan clan1 = api.getClanByName("Clan1");
Clan clan2 = api.getClanByName("Clan2");

if (api.areAtWar(clan1, clan2)) {
    // Os clãs estão em guerra!
}
```

### Verificar se clã está em alguma guerra

```java
Clan clan = api.getPlayerClan(playerUUID);

if (api.isInAnyWar(clan)) {
    // Clã está em guerra
}
```

### Obter guerra ativa

```java
ClanWar guerra = api.getActiveWar(clan);

if (guerra != null) {
    String oponente = guerra.getOpponent(clan.getName());
    int nossasKills = guerra.getKills(clan.getName());
    int killsOponente = guerra.getKills(oponente);
    
    player.sendMessage("Guerra contra: " + oponente);
    player.sendMessage("Placar: " + nossasKills + " x " + killsOponente);
}
```

### Exemplo: Bônus de dano em guerra

```java
@EventHandler
public void onDamage(EntityDamageByEntityEvent event) {
    if (!(event.getDamager() instanceof Player)) return;
    if (!(event.getEntity() instanceof Player)) return;
    
    Player attacker = (Player) event.getDamager();
    Player victim = (Player) event.getEntity();
    
    Clan attackerClan = api.getPlayerClan(attacker.getUniqueId());
    Clan victimClan = api.getPlayerClan(victim.getUniqueId());
    
    if (attackerClan != null && victimClan != null) {
        if (api.areAtWar(attackerClan, victimClan)) {
            // 20% de dano extra em guerra
            event.setDamage(event.getDamage() * 1.2);
        }
    }
}
```

---

## Sistema de Ranking

### Obter top clãs

```java
// Top 10 por pontos
List<Clan> topClans = api.getTopClans(10);

int posicao = 1;
for (Clan clan : topClans) {
    player.sendMessage(posicao + ". " + clan.getName() + " - " + clan.getPoints() + " pontos");
    posicao++;
}
```

### Obter posição no ranking

```java
Clan clan = api.getPlayerClan(playerUUID);

int posicaoPontos = api.getClanRankingPosition(clan, "points");
int posicaoKills = api.getClanRankingPosition(clan, "kills");
int posicaoNivel = api.getClanRankingPosition(clan, "level");

player.sendMessage("Ranking por pontos: #" + posicaoPontos);
player.sendMessage("Ranking por kills: #" + posicaoKills);
player.sendMessage("Ranking por nível: #" + posicaoNivel);
```

### Tipos de ranking disponíveis

| Tipo | Descrição |
|------|-----------|
| `points` | Por pontos |
| `kills` | Por kills |
| `kdr` | Por K/D ratio |
| `level` | Por nível |
| `bank` | Por saldo do banco |
| `wins` | Por vitórias em guerras |

---

## Sistema de Missões

### Obter missões ativas

```java
Clan clan = api.getPlayerClan(playerUUID);
List<ClanMission> missoes = api.getActiveMissions(clan);

for (ClanMission missao : missoes) {
    String tipo = missao.getType().name();
    int progresso = missao.getCurrentProgress();
    int objetivo = missao.getTargetAmount();
    int recompensa = missao.getReward();
    
    player.sendMessage(tipo + ": " + progresso + "/" + objetivo + " (Recompensa: " + recompensa + " pontos)");
}
```

### Adicionar progresso em missão

```java
// Adicionar progresso manualmente
api.addMissionProgress(clan, MissionType.KILL_PLAYERS, 1);
api.addMissionProgress(clan, MissionType.MINE_BLOCKS, 64);
api.addMissionProgress(clan, MissionType.DEPOSIT_MONEY, 1000);
```

### Tipos de missões

```java
public enum MissionType {
    KILL_PLAYERS,    // Matar jogadores
    MINE_BLOCKS,     // Minerar blocos
    WIN_WARS,        // Vencer guerras
    DEPOSIT_MONEY    // Depositar dinheiro
}
```

---

## Sistema de Chat

### Verificar se chat do clã está ativo

```java
if (api.isClanChatEnabled(playerUUID)) {
    // Jogador está com chat do clã ativo
}
```

### Enviar mensagem para o clã

```java
Clan clan = api.getPlayerClan(playerUUID);

// Enviar mensagem como se fosse do jogador
api.sendClanMessage(clan, player, "Olá clã!");

// Enviar mensagem do sistema
for (UUID membro : clan.getMembers().keySet()) {
    Player p = Bukkit.getPlayer(membro);
    if (p != null) {
        p.sendMessage("§e[Sistema] §fMensagem para o clã!");
    }
}
```

---

## Estatísticas

### Obter estatísticas do clã

```java
Clan clan = api.getPlayerClan(playerUUID);

int kills = api.getClanKills(clan);
int deaths = api.getClanDeaths(clan);
double kdr = api.getClanKDR(clan);
int guerrasVencidas = api.getClanWarsWon(clan);
int guerrasPerdidas = api.getClanWarsLost(clan);

player.sendMessage("§eEstatísticas do Clã:");
player.sendMessage("§7Kills: §f" + kills);
player.sendMessage("§7Deaths: §f" + deaths);
player.sendMessage("§7KDR: §f" + String.format("%.2f", kdr));
player.sendMessage("§7Guerras Vencidas: §f" + guerrasVencidas);
player.sendMessage("§7Guerras Perdidas: §f" + guerrasPerdidas);
```

### Registrar eventos

```java
// Registrar kill (atualiza stats, missões, XP, guerra)
api.registerKill(killerUUID, victimUUID);

// Registrar morte
api.registerDeath(playerUUID);

// Registrar blocos minerados
api.registerBlocksMined(playerUUID, quantidade);
```

---

## Eventos Personalizados

### Listener de eventos do clã

```java
public class MeuClanListener implements Listener {
    
    // Quando um clã é criado (use PlayerJoinEvent como proxy)
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Clan clan = VKClansAPI.getInstance().getPlayerClan(player.getUniqueId());
        
        if (clan != null) {
            // Notificar membros online
        }
    }
    
    // Quando um jogador mata outro
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        
        if (killer != null) {
            VKClansAPI api = VKClansAPI.getInstance();
            Clan killerClan = api.getPlayerClan(killer.getUniqueId());
            
            if (killerClan != null) {
                // Kill já é registrada automaticamente pelo VKClans
                // Mas você pode adicionar lógica extra aqui
            }
        }
    }
}
```

---

## Exemplos Completos

### Exemplo 1: Plugin de Scoreboard com Info do Clã

```java
public class ClanScoreboard {
    
    private VKClansAPI api = VKClansAPI.getInstance();
    
    public void updateScoreboard(Player player) {
        Clan clan = api.getPlayerClan(player.getUniqueId());
        
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("clan", "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName("§6§lSeu Clã");
        
        if (clan != null) {
            obj.getScore("§eClã: §f" + clan.getName()).setScore(10);
            obj.getScore("§eTag: §f[" + clan.getTag() + "]").setScore(9);
            obj.getScore("§eNível: §f" + api.getClanLevel(clan)).setScore(8);
            obj.getScore("§ePontos: §f" + api.getPoints(clan)).setScore(7);
            obj.getScore("§eMembros: §f" + clan.getMembers().size()).setScore(6);
            obj.getScore("§eKills: §f" + api.getClanKills(clan)).setScore(5);
            obj.getScore("§eBanco: §a$" + api.getClanBankBalance(clan)).setScore(4);
            
            ClanRole role = api.getMemberRole(player.getUniqueId());
            obj.getScore("§eCargo: " + role.getColoredName()).setScore(3);
            
            if (api.isInAnyWar(clan)) {
                obj.getScore("§c⚔ EM GUERRA").setScore(2);
            }
        } else {
            obj.getScore("§7Sem clã").setScore(5);
            obj.getScore("§eUse §f/clan criar").setScore(4);
        }
        
        player.setScoreboard(board);
    }
}
```

### Exemplo 2: Proteção de Área por Clã

```java
public class ClanProtection implements Listener {
    
    private VKClansAPI api = VKClansAPI.getInstance();
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        
        // Verificar se a área pertence a algum clã
        String areaClan = getAreaOwner(block.getLocation());
        
        if (areaClan != null) {
            Clan playerClan = api.getPlayerClan(player.getUniqueId());
            
            if (playerClan == null || !playerClan.getName().equals(areaClan)) {
                event.setCancelled(true);
                player.sendMessage("§cEsta área pertence ao clã " + areaClan + "!");
            }
        }
    }
    
    @EventHandler
    public void onPvP(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        
        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        
        // Desabilitar PvP entre membros do mesmo clã
        if (api.areInSameClan(attacker.getUniqueId(), victim.getUniqueId())) {
            event.setCancelled(true);
            attacker.sendMessage("§cVocê não pode atacar membros do seu clã!");
        }
    }
    
    private String getAreaOwner(Location loc) {
        // Implementar lógica de área
        return null;
    }
}
```

### Exemplo 3: Recompensas por Nível do Clã

```java
public class ClanRewards {
    
    private VKClansAPI api = VKClansAPI.getInstance();
    
    public double getExpMultiplier(Player player) {
        Clan clan = api.getPlayerClan(player.getUniqueId());
        
        if (clan == null) return 1.0;
        
        int level = api.getClanLevel(clan);
        
        // 5% de bônus por nível
        return 1.0 + (level * 0.05);
    }
    
    public double getMoneyMultiplier(Player player) {
        Clan clan = api.getPlayerClan(player.getUniqueId());
        
        if (clan == null) return 1.0;
        
        int level = api.getClanLevel(clan);
        
        // 3% de bônus por nível
        return 1.0 + (level * 0.03);
    }
    
    public int getExtraHomes(Player player) {
        Clan clan = api.getPlayerClan(player.getUniqueId());
        
        if (clan == null) return 0;
        
        // 1 home extra por nível do clã
        return api.getClanLevel(clan);
    }
}
```

---

## 📝 Notas Importantes

1. **Sempre verifique null** ao obter clãs
2. **O API é thread-safe** para leitura, mas modificações devem ser feitas na main thread
3. **Eventos são processados automaticamente** - não registre kills/deaths manualmente se o VKClans já faz isso
4. **Salvar dados** é feito automaticamente pelo VKClans
5. **Soft-depend** é recomendado para plugins opcionais

---

## ❓ Suporte

- Issues: [GitHub](https://github.com/seu-usuario/VKClans/issues)
- Discord: [Servidor](https://discord.gg/seuservidor)
