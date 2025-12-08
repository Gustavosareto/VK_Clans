# ⚙️ VKClans - Guia de Configuração

Este guia explica todas as opções de configuração disponíveis no VKClans.

---

## 📁 Arquivos de Configuração

| Arquivo | Descrição |
|---------|-----------|
| `config.yml` | Configurações gerais do plugin |
| `messages.yml` | Todas as mensagens customizáveis |
| `clans.yml` | Dados dos clãs (auto-gerado) |
| `clanlogs.yml` | Logs de ações (auto-gerado) |
| `clancontents.yml` | Itens dos baús (auto-gerado) |

---

## 📄 config.yml

### Configurações Gerais

```yaml
geral:
  # Prefixo usado em todas as mensagens
  prefixo: "&6[Clans] &r"
  
  # Tamanho mínimo e máximo do nome do clã
  nome-minimo: 3
  nome-maximo: 16
  
  # Tamanho mínimo e máximo da tag
  tag-minimo: 2
  tag-maximo: 5
  
  # Máximo de membros inicial (aumenta com nível)
  membros-maximo: 10
  
  # Pontos iniciais ao criar clã
  pontos-iniciais: 0
  
  # Cooldown entre convites (segundos)
  cooldown-convite: 60
  
  # Tempo de expiração do convite (segundos)
  tempo-expiracao-convite: 120
  
  # Pontos ganhos por kill
  pontos-por-kill: 10
  
  # Pontos perdidos por morte
  pontos-por-morte: 5
```

### Sistema de Base

```yaml
base:
  # Tempo de espera antes de teleportar (segundos)
  tempo-espera: 5
  
  # Cancelar teleporte se o jogador se mover
  cancelar-ao-mover: true
  
  # Cooldown entre teleportes (segundos)
  cooldown: 60
```

### Sistema de Guerra

```yaml
guerra:
  # Habilitar/desabilitar sistema de guerra
  habilitado: true
  
  # Duração da guerra (horas)
  duracao-horas: 24
  
  # Kills necessárias para vencer (0 = apenas por tempo)
  kills-para-vencer: 10
  
  # Cooldown após guerra (horas)
  cooldown-horas: 48
  
  # Recompensa em pontos para o vencedor
  recompensa-pontos: 100
  
  # Recompensa em dinheiro para o vencedor
  recompensa-dinheiro: 1000.0
```

### Sistema de Banco

```yaml
banco:
  # Habilitar/desabilitar banco (requer Vault)
  habilitado: true
  
  # Valor mínimo para depósito
  deposito-minimo: 100.0
  
  # Valor mínimo para saque
  saque-minimo: 100.0
  
  # Taxa de depósito (0.05 = 5%)
  taxa-deposito: 0.0
  
  # Taxa de saque (0.05 = 5%)
  taxa-saque: 0.05
```

### Sistema de Níveis

```yaml
niveis:
  # Habilitar/desabilitar sistema de níveis
  habilitado: true
  
  # XP ganho por kill
  xp-por-kill: 10
  
  # XP ganho por bloco minerado
  xp-por-bloco: 1
  
  # XP ganho por guerra vencida
  xp-por-guerra-vencida: 500
  
  # Configuração de cada nível
  lista:
    1:
      xp-necessario: 0
      max-membros: 10
      custo-upgrade: 0
    2:
      xp-necessario: 1000
      max-membros: 15
      custo-upgrade: 5000
    3:
      xp-necessario: 5000
      max-membros: 20
      custo-upgrade: 15000
    4:
      xp-necessario: 15000
      max-membros: 25
      custo-upgrade: 30000
    5:
      xp-necessario: 30000
      max-membros: 30
      custo-upgrade: 50000
```

### Sistema de Baú

```yaml
bau:
  # Habilitar/desabilitar baú compartilhado
  habilitado: true
  
  # Linhas de inventário por nível do clã
  linhas-por-nivel: 1
  # Nível 1 = 1 linha (9 slots)
  # Nível 2 = 2 linhas (18 slots)
  # etc...
```

### Sistema de Missões

```yaml
missoes:
  # Habilitar/desabilitar missões
  habilitadas: true
  
  # Quantidade de missões geradas por dia
  quantidade-diaria: 3
  
  # Duração de cada missão (horas)
  duracao-horas: 24
  
  # Configuração de cada tipo de missão
  tipos:
    kill_players:
      habilitado: true
      min: 5
      max: 20
      recompensa-min: 50
      recompensa-max: 200
    mine_blocks:
      habilitado: true
      min: 100
      max: 500
      recompensa-min: 30
      recompensa-max: 150
    win_wars:
      habilitado: true
      min: 1
      max: 3
      recompensa-min: 200
      recompensa-max: 500
    deposit_money:
      habilitado: true
      min: 1000
      max: 10000
      recompensa-min: 50
      recompensa-max: 200
```

### Sistema de Chat

```yaml
chat:
  # Formato do chat do clã
  # Placeholders: {clan}, {tag}, {cargo}, {player}, {message}
  formato: "&7[&6{clan}&7] &e{cargo} &f{player}&7: &f{message}"
  
  # Formato para espiões (admins)
  formato-spy: "&8[SPY] &7[&6{clan}&7] &f{player}&7: &f{message}"
```

---

## 📄 messages.yml

### Estrutura

Todas as mensagens suportam:
- Códigos de cor (`&a`, `&b`, `&c`, etc.)
- Placeholders específicos de cada mensagem

### Mensagens Principais

```yaml
# Prefixo global
prefix: "&6[Clans] &r"

# Sem permissão
no-permission: "&cVocê não tem permissão para isso."

# Sem clã
no-clan: "&cVocê não está em um clã."

# Já tem clã
already-in-clan: "&cVocê já está em um clã."

# Clã não encontrado
clan-not-found: "&cClã não encontrado."

# Jogador não encontrado
player-not-found: "&cJogador não encontrado."

# Jogador offline
player-offline: "&cEste jogador não está online."
```

### Mensagens de Criação

```yaml
# Clã criado com sucesso
# Placeholders: {clan}, {tag}
clan-created: "&aClã &e{clan} &7[&e{tag}&7] &acriado com sucesso!"

# Nome muito curto
# Placeholders: {min}
clan-name-too-short: "&cO nome deve ter no mínimo {min} caracteres."

# Nome muito longo
# Placeholders: {max}
clan-name-too-long: "&cO nome deve ter no máximo {max} caracteres."

# Tag muito curta
# Placeholders: {min}
clan-tag-too-short: "&cA tag deve ter no mínimo {min} caracteres."

# Tag muito longa
# Placeholders: {max}
clan-tag-too-long: "&cA tag deve ter no máximo {max} caracteres."

# Nome já existe
clan-name-exists: "&cJá existe um clã com este nome."

# Tag já existe
clan-tag-exists: "&cJá existe um clã com esta tag."
```

### Mensagens de Convite

```yaml
# Convite enviado
# Placeholders: {player}
invite-sent: "&aConvite enviado para &e{player}&a."

# Convite recebido
# Placeholders: {clan}, {player}
invite-received: "&aVocê foi convidado para o clã &e{clan} &apor &e{player}&a."

# Convite expirado
invite-expired: "&cO convite expirou."

# Sem convite pendente
no-pending-invite: "&cVocê não tem convites pendentes."

# Convite aceito (para quem aceitou)
# Placeholders: {clan}
invite-accepted: "&aVocê entrou no clã &e{clan}&a!"

# Membro entrou (para o clã)
# Placeholders: {player}
member-joined: "&e{player} &aentrou no clã!"

# Convite recusado
invite-declined: "&cVocê recusou o convite."
```

### Mensagens de Gerenciamento

```yaml
# Membro expulso (para o clã)
# Placeholders: {player}
member-kicked: "&e{player} &cfoi expulso do clã."

# Você foi expulso
# Placeholders: {clan}
you-kicked: "&cVocê foi expulso do clã &e{clan}&c."

# Membro promovido
# Placeholders: {player}, {role}
member-promoted: "&e{player} &afoi promovido para &e{role}&a."

# Membro rebaixado
# Placeholders: {player}, {role}
member-demoted: "&e{player} &cfoi rebaixado para &e{role}&c."

# Liderança transferida
# Placeholders: {player}
leadership-transferred: "&aA liderança foi transferida para &e{player}&a."

# Você saiu do clã
clan-left: "&cVocê saiu do clã."

# Clã deletado
# Placeholders: {clan}
clan-deleted: "&cO clã &e{clan} &cfoi deletado."
```

### Mensagens de Base

```yaml
# Teleportando
# Placeholders: {time}
clan-base-teleporting: "&eTeleportando em &a{time}&e segundos..."

# Teleportado com sucesso
clan-base-tp: "&aTeleportado para a base do clã!"

# Teleporte cancelado
clan-base-cancelled: "&cTeleporte cancelado! Você se moveu."

# Em cooldown
# Placeholders: {seconds}
clan-base-cooldown: "&cAguarde &e{seconds}&c segundos para teleportar novamente."

# Base definida
clan-base-set: "&aBase do clã definida!"

# Sem base
clan-no-base: "&cO clã não possui uma base definida."
```

### Mensagens de Guerra

```yaml
# Guerra declarada
# Placeholders: {clan}
war-declared: "&c⚔ &4Guerra declarada contra &e{clan}&4!"

# Já em guerra
war-already-active: "&cSeu clã já está em guerra!"

# Clã alvo em guerra
war-target-in-war: "&cEste clã já está em uma guerra."

# Guerra vencida
# Placeholders: {clan}, {points}, {money}
war-won: "&a⚔ Seu clã venceu a guerra contra &e{clan}&a! +{points} pontos, +${money}"

# Guerra perdida
# Placeholders: {clan}
war-lost: "&c⚔ Seu clã perdeu a guerra contra &e{clan}&c."

# Kill em guerra
# Placeholders: {player}, {kills}, {max}
war-kill: "&c⚔ {player} foi eliminado! Kills: {kills}/{max}"
```

### Mensagens de Banco

```yaml
# Depósito realizado
# Placeholders: {amount}, {balance}
bank-deposited: "&aVocê depositou &e{amount}&a. Saldo: &e{balance}"

# Saque realizado
# Placeholders: {amount}, {balance}
bank-withdrawn: "&aVocê sacou &e{amount}&a. Saldo: &e{balance}"

# Saldo insuficiente
bank-insufficient: "&cSaldo insuficiente no banco do clã."

# Dinheiro insuficiente (jogador)
bank-no-money: "&cVocê não tem dinheiro suficiente."

# Valor mínimo de depósito
# Placeholders: {amount}
bank-minimum-deposit: "&cO valor mínimo para depósito é &e{amount}&c."

# Valor mínimo de saque
# Placeholders: {amount}
bank-minimum-withdraw: "&cO valor mínimo para saque é &e{amount}&c."
```

### Mensagens de Nível

```yaml
# Level up!
# Placeholders: {level}
level-up: "&a&l⬆ &aSeu clã subiu para o nível &e{level}&a!"

# XP recebido
# Placeholders: {xp}, {total}
xp-gained: "&a+{xp} XP &7(Total: {total})"

# Upgrade comprado
# Placeholders: {level}
level-upgraded: "&aClã atualizado para nível &e{level}&a!"

# Nível máximo
level-max: "&cSeu clã já está no nível máximo!"

# Sem dinheiro para upgrade
# Placeholders: {cost}
level-no-money: "&cO custo do upgrade é &e{cost}&c. Saldo insuficiente."
```

### Mensagens de Missões

```yaml
# Missão completa
# Placeholders: {mission}, {reward}
mission-completed: "&a✔ Missão &e{mission} &acompleta! +{reward} pontos"

# Progresso da missão
# Placeholders: {mission}, {progress}, {target}
mission-progress: "&7Missão: &e{mission} &7- &a{progress}/{target}"

# Novas missões geradas
missions-generated: "&aNovas missões do clã geradas!"
```

---

## 🎨 Códigos de Cor

| Código | Cor |
|--------|-----|
| `&0` | Preto |
| `&1` | Azul Escuro |
| `&2` | Verde Escuro |
| `&3` | Ciano Escuro |
| `&4` | Vermelho Escuro |
| `&5` | Roxo |
| `&6` | Dourado |
| `&7` | Cinza |
| `&8` | Cinza Escuro |
| `&9` | Azul |
| `&a` | Verde |
| `&b` | Ciano |
| `&c` | Vermelho |
| `&d` | Rosa |
| `&e` | Amarelo |
| `&f` | Branco |

### Formatação

| Código | Efeito |
|--------|--------|
| `&l` | **Negrito** |
| `&o` | *Itálico* |
| `&n` | Sublinhado |
| `&m` | ~~Riscado~~ |
| `&k` | Ofuscado |
| `&r` | Reset |

---

## 📝 Placeholders Disponíveis

### Gerais

| Placeholder | Descrição |
|-------------|-----------|
| `{clan}` | Nome do clã |
| `{tag}` | Tag do clã |
| `{player}` | Nome do jogador |
| `{role}` | Cargo do jogador |

### Números

| Placeholder | Descrição |
|-------------|-----------|
| `{level}` | Nível do clã |
| `{points}` | Pontos do clã |
| `{kills}` | Kills do clã |
| `{deaths}` | Mortes do clã |
| `{members}` | Quantidade de membros |
| `{max_members}` | Máximo de membros |

### Economia

| Placeholder | Descrição |
|-------------|-----------|
| `{amount}` | Valor da transação |
| `{balance}` | Saldo do banco |
| `{cost}` | Custo do upgrade |
| `{money}` | Dinheiro |

### Tempo

| Placeholder | Descrição |
|-------------|-----------|
| `{time}` | Tempo restante |
| `{seconds}` | Segundos |
| `{hours}` | Horas |

---

## 🔄 Recarregar Configurações

Após editar os arquivos, use:

```
/clan reload
```

**Nota:** Alguns valores só são aplicados após reiniciar o servidor.
