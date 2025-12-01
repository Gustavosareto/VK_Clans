# 🏆 VKClans - Sistema de Clãs Completo para Minecraft

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.8.8-green?style=for-the-badge&logo=minecraft" alt="Minecraft">
  <img src="https://img.shields.io/badge/Spigot-Compatible-orange?style=for-the-badge" alt="Spigot">
  <img src="https://img.shields.io/badge/Java-8+-red?style=for-the-badge&logo=java" alt="Java">
  <img src="https://img.shields.io/badge/Version-1.1.0-blue?style=for-the-badge" alt="Version">
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" alt="License">
</p>

<p align="center">
  <b>O plugin de clãs mais completo e profissional para servidores Minecraft</b>
</p>

---

## 📋 Índice

- [Sobre](#-sobre)
- [Funcionalidades](#-funcionalidades)
- [Requisitos](#-requisitos)
- [Instalação](#-instalação)
- [Comandos](#-comandos)
- [Permissões](#-permissões)
- [Configuração](#-configuração)
- [PlaceholderAPI](#-placeholderapi)
- [API para Desenvolvedores](#-api-para-desenvolvedores)
- [FAQ](#-faq)
- [Changelog](#-changelog)
- [Licença](#-licença)

---

## 📖 Sobre

**VKClans** é um sistema de clãs completo e otimizado para servidores Minecraft 1.8.8+. O plugin oferece uma experiência rica em funcionalidades, incluindo sistema de guerra, banco do clã, níveis, missões, alianças, conquistas, ranking semanal e muito mais.

### ✨ Destaques

| Feature | Descrição |
|---------|-----------|
| 🎮 **Interface Gráfica (GUI)** | Menus intuitivos para fácil gerenciamento |
| ⚔️ **Sistema de Guerra** | Batalhe contra outros clãs por supremacia |
| 🏆 **Ranking Competitivo** | 6 tipos de rankings + ranking semanal |
| 💰 **Economia Integrada** | Banco do clã com Vault (suporte a 1k, 1m, 1b) |
| 📈 **Progressão** | Sistema de níveis com benefícios |
| 📦 **Baú Compartilhado** | Armazenamento comum entre membros |
| 💬 **Chat Privado** | Comunicação exclusiva do clã |
| 🎯 **Missões** | Desafios diários com recompensas |
| 🤝 **Alianças** | Sistema de clãs aliados |
| 🏅 **Conquistas** | Sistema de achievements do clã |
| 🛡️ **Friendly Fire** | Proteção entre membros configurável |
| 🔒 **Limite de IP** | Evita múltiplas contas no mesmo clã |
| 🏷️ **PlaceholderAPI** | Integração completa com placeholders |
| 📊 **API Completa** | Integração fácil com outros plugins |

---

## 🚀 Funcionalidades

### 📌 Sistema de Clãs
- ✅ Criar, editar e deletar clãs
- ✅ Tags coloridas personalizáveis (ex: `&c&lTAG`)
- ✅ Sistema de convites com expiração
- ✅ Limite de membros configurável
- ✅ Base do clã com teleporte
- ✅ Blacklist de nomes e tags proibidos
- ✅ Restrição de mundos para base

### 👑 Hierarquia de Cargos

| Cargo | Nível | Permissões |
|-------|-------|------------|
| 👑 **Dono** | 4 | Todas as permissões |
| ⭐ **Sub-Dono** | 3 | Gerenciar membros, base, banco, guerra |
| 🛡️ **Administrador** | 2 | Convidar, expulsar membros |
| 👤 **Membro** | 1 | Comandos básicos |

### ⚔️ Sistema de Guerra
- Declarar guerra contra outros clãs
- Duração e kills configuráveis
- Recompensas para o vencedor (pontos, XP, dinheiro)
- Histórico de guerras
- Proteção contra ataques durante guerra

### 🏆 Sistema de Ranking
- **Por Pontos** - Ranking geral
- **Por Kills** - Clãs mais mortais
- **Por KDR** - Melhor K/D ratio
- **Por Nível** - Clãs mais avançados
- **Por Banco** - Clãs mais ricos
- **Por Vitórias** - Mais guerras vencidas
- **Semanal** - Ranking que reseta toda semana

### 💰 Banco do Clã
- Depósitos e saques
- Suporte a formatação abreviada (1k, 1m, 1b, 1t)
- Valores mínimos configuráveis
- Taxas de transação
- Integração com Vault

### 📈 Sistema de Níveis

| Nível | XP Necessário | Bônus Membros | Redução Cooldown |
|-------|---------------|---------------|------------------|
| 1 | 0 | +0 | 0s |
| 2 | 100 | +5 | -2s |
| 3 | 150 | +10 | -4s |
| 4 | 225 | +15 | -6s |
| 5 | 337 | +20 | -8s |
| ... | ... | ... | ... |
| 10 | Max | +45 | -18s |

### 📦 Baú Compartilhado
- Inventário compartilhado entre membros
- Tamanho aumenta com nível do clã
- Log de todas as transações
- Proteção contra roubo

### 💬 Chat Privado
- Chat exclusivo do clã
- Toggle para ativar/desativar
- Modo spy para administradores
- Formatação personalizável

### 🎯 Sistema de Missões
- **Matar Jogadores** - Kill X players
- **Minerar Blocos** - Mine X blocks
- **Vencer Guerras** - Win X wars
- **Depositar Dinheiro** - Deposit $X
- Recompensas em XP e pontos

### 🤝 Sistema de Alianças
- Convide outros clãs para serem aliados
- Proteção contra friendly fire entre aliados
- Chat entre aliados
- Máximo de aliados configurável

### 🏅 Sistema de Conquistas
- **Primeiros Passos** - Criar um clã
- **Construindo o Exército** - 10 membros
- **Guerreiro** - Primeira guerra vencida
- **Dominador** - 10 guerras vencidas
- **Milionário** - 1M no banco
- **Veterano** - Nível 5
- **Mestre** - Nível 10
- E mais...

### 🛡️ Friendly Fire
- Proteção contra dano entre membros do clã
- Proteção entre clãs aliados
- Configuração por guerra (permitir FF durante guerra)

### 🔒 Limite de IP
- Evita múltiplas contas no mesmo clã
- Configurável por IP
- Bypass para staff

### 📋 Log de Ações
Todas as ações são registradas:
- Entrada/saída de membros
- Promoções/rebaixamentos
- Depósitos/saques
- Declarações de guerra
- Definição de base
- E muito mais...

---

## 📋 Requisitos

| Requisito | Versão | Obrigatório |
|-----------|--------|-------------|
| Minecraft Server | 1.8.8+ | ✅ |
| Java | 8+ | ✅ |
| Spigot/Paper | Compatível | ✅ |
| Vault | Qualquer | ❌ (para banco) |
| PlaceholderAPI | 2.10+ | ❌ (para placeholders) |

---

## 📥 Instalação

1. **Download** - Baixe o arquivo `VKClans-1.1.0.jar`
2. **Plugins** - Coloque na pasta `plugins` do servidor
3. **Reinicie** - Reinicie o servidor
4. **Configure** - Edite os arquivos em `plugins/VKClans/`

```bash
plugins/
└── VKClans/
    ├── config.yml        # Configurações gerais
    ├── messages.yml      # Mensagens customizáveis
    ├── clans.yml         # Dados dos clãs (auto-gerado)
    ├── wars.yml          # Guerras ativas (auto-gerado)
    ├── missions.yml      # Missões (auto-gerado)
    ├── clanlogs.yml      # Logs de ações (auto-gerado)
    └── clancontents.yml  # Baús dos clãs (auto-gerado)
```

---

## 💻 Comandos

### Comandos Principais

| Comando | Descrição |
|---------|-----------|
| `/clan criar <nome> <tag>` | Cria um novo clã |
| `/clan menu` | Abre o menu principal |
| `/clan info [clan]` | Informações do clã |
| `/clan membros` | Lista de membros |
| `/clan convidar <jogador>` | Convida um jogador |
| `/clan aceitar` | Aceita convite |
| `/clan recusar` | Recusa convite |
| `/clan sair` | Sai do clã |
| `/clan kick <jogador>` | Expulsa membro |
| `/clan promover <jogador>` | Promove membro |
| `/clan rebaixar <jogador>` | Rebaixa membro |
| `/clan transferir <jogador>` | Transfere liderança |
| `/clan deletar` | Deleta o clã |
| `/clan ajuda` | Lista de comandos |

### Comandos de Base

| Comando | Descrição |
|---------|-----------|
| `/clan base` | Teleporta para a base |
| `/clan setbase` | Define a base |

### Comandos Avançados

| Comando | Descrição |
|---------|-----------|
| `/clan guerra <clan>` | Declara guerra |
| `/clan top [tipo]` | Rankings (pontos/kills/kdr/nivel/banco/wins) |
| `/clan banco depositar <valor>` | Deposita no banco (aceita 1k, 1m, 1b) |
| `/clan banco sacar <valor>` | Saca do banco (aceita 1k, 1m, 1b) |
| `/clan banco saldo` | Ver saldo do banco |
| `/clan nivel` | Ver nível atual |
| `/clan nivel upgrade` | Comprar upgrade de nível |
| `/clan bau` | Abre o baú compartilhado |
| `/clan log` | Histórico de ações |
| `/clan chat` | Toggle do chat privado |
| `/clan missoes` | Ver missões ativas |

### Comandos de Aliança

| Comando | Descrição |
|---------|-----------|
| `/clan alianca convidar <clan>` | Convida clã para aliança |
| `/clan alianca aceitar` | Aceita convite de aliança |
| `/clan alianca recusar` | Recusa convite de aliança |
| `/clan alianca remover <clan>` | Remove aliança |
| `/clan alianca lista` | Lista aliados |

### Comandos Admin

| Comando | Descrição |
|---------|-----------|
| `/clan spy` | Espionar chats dos clãs |
| `/clan reload` | Recarrega configurações |
| `/clanadmin` | Comandos administrativos |

---

## 🔐 Permissões

### Permissões Básicas

| Permissão | Descrição | Padrão |
|-----------|-----------|--------|
| `vkclans.use` | Usar comandos básicos | true |
| `vkclans.create` | Criar clãs | true |
| `vkclans.chat` | Usar chat do clã | true |

### Permissões Avançadas

| Permissão | Descrição | Padrão |
|-----------|-----------|--------|
| `vkclans.guerra` | Declarar guerra | op |
| `vkclans.banco` | Usar banco do clã | true |
| `vkclans.bau` | Usar baú do clã | true |
| `vkclans.missoes` | Ver missões | true |
| `vkclans.alianca` | Gerenciar alianças | true |

### Permissões Admin

| Permissão | Descrição | Padrão |
|-----------|-----------|--------|
| `vkclans.admin` | Comandos admin | op |
| `vkclans.spy` | Espionar chats | op |
| `vkclans.reload` | Recarregar plugin | op |
| `vkclans.bypass.*` | Bypass de restrições | op |
| `vkclans.bypass.iplimit` | Bypass limite de IP | op |

---

## ⚙️ Configuração

### config.yml (Resumo)

```yaml
# Configurações Gerais
geral:
  nome-min: 3
  nome-max: 16
  tag-min: 2
  tag-max: 4
  permitir-cores-nome: false
  max-membros: 50

# Blacklist de nomes/tags
blacklist:
  nomes: [admin, staff, mod, dono, owner]
  tags: [ADM, MOD, VIP, DONO, STAFF]

# Sistema de Base
base:
  cooldown: 30
  tempo-espera: 3
  cancelar-ao-mover: false
  bloquear-em-combate: true
  mundos-permitidos: [world, plotworld]
  mundos-bloqueados: [world_nether, world_the_end]

# Sistema de Guerra
guerra:
  duracao: 30
  cooldown: 60
  recompensa-pontos: 100
  recompensa-exp: 50
  recompensa-dinheiro: 1000

# Sistema de Nível
nivel:
  maximo: 10
  exp-base: 100
  exp-multiplier: 1.5
  bonus-membros-por-nivel: 5

# Sistema de Alianças
aliancas:
  ativado: true
  max-aliados: 3
  tempo-convite: 60
  friendly-fire-aliados: false

# Sistema de Conquistas
conquistas:
  ativado: true

# Ranking Semanal
ranking-semanal:
  ativado: true

# Limite de IP
limite-ip:
  ativado: true
  max-por-ip: 2

# Friendly Fire
friendly-fire:
  permitido: false
  permitido-em-guerra: true
```

---

## 🏷️ PlaceholderAPI

O plugin possui integração completa com PlaceholderAPI. Basta ter o PlaceholderAPI instalado no servidor.

### Placeholders Disponíveis

| Placeholder | Descrição |
|-------------|-----------|
| `%vkclans_clan%` | Nome do clã |
| `%vkclans_tag%` | Tag do clã **(colorida!)** |
| `%vkclans_tag_raw%` | Tag do clã (sem cores) |
| `%vkclans_role%` | Cargo do jogador |
| `%vkclans_role_colored%` | Cargo colorido |
| `%vkclans_level%` | Nível do clã |
| `%vkclans_members%` | Quantidade de membros |
| `%vkclans_max_members%` | Máximo de membros |
| `%vkclans_points%` | Pontos do clã |
| `%vkclans_kills%` | Kills do clã |
| `%vkclans_deaths%` | Deaths do clã |
| `%vkclans_kdr%` | KDR do clã |
| `%vkclans_bank%` | Saldo do banco |
| `%vkclans_wars_won%` | Guerras vencidas |
| `%vkclans_wars_lost%` | Guerras perdidas |
| `%vkclans_weekly_kills%` | Kills semanais |
| `%vkclans_has_clan%` | Se tem clã (true/false) |
| `%vkclans_in_war%` | Se está em guerra (true/false) |
| `%vkclans_allies%` | Quantidade de aliados |

### Exemplo de Uso no Chat

```
[%vkclans_tag%] %player_name%: %message%
```

Resultado: `[§c§lVK] Player: Olá!`

---

## 🔌 API para Desenvolvedores

O VKClans oferece uma API completa para integração com outros plugins.

### Obtendo a API

```java
import com.vkclans.api.VKClansAPI;

VKClansAPI api = VKClansAPI.getInstance();
```

### Exemplos de Uso

```java
// Verificar se jogador tem clã
Clan clan = api.getPlayerClan(player.getUniqueId());

// Obter informações do clã
String clanName = api.getClanName(player.getUniqueId());
String clanTag = api.getClanTag(player.getUniqueId());
int clanLevel = api.getClanLevel(player.getUniqueId());

// Verificar cargo
ClanRole role = api.getMemberRole(player.getUniqueId());
boolean isLeader = api.isClanLeader(player.getUniqueId());

// Sistema de guerra
boolean atWar = api.areAtWar(clan1, clan2);

// Banco
double balance = api.getClanBankBalance(clan);

// Alianças
boolean areAllies = api.areAllies(clan1, clan2);
```

### Métodos Disponíveis

| Categoria | Métodos |
|-----------|---------|
| **Clãs** | `getPlayerClan`, `getClanByName`, `getClanByTag`, `getAllClans` |
| **Membros** | `getMemberRole`, `isClanLeader`, `areInSameClan`, `getClanMembers` |
| **Pontos** | `addPoints`, `removePoints`, `setPoints`, `getPoints` |
| **Banco** | `getClanBankBalance`, `addToClanBank`, `removeFromClanBank` |
| **Níveis** | `getClanLevel`, `addExperience`, `getMemberLimit` |
| **Guerra** | `areAtWar`, `getActiveWar`, `isInAnyWar` |
| **Alianças** | `areAllies`, `getAllies`, `addAlly`, `removeAlly` |
| **Ranking** | `getTopClans`, `getClanRankingPosition` |
| **Stats** | `getClanKills`, `getClanDeaths`, `getClanKDR` |

---

## ❓ FAQ

<details>
<summary><b>O plugin funciona sem Vault?</b></summary>
Sim! O sistema de banco do clã será desativado, mas todas as outras funcionalidades funcionam normalmente.
</details>

<details>
<summary><b>Posso usar em versões acima do 1.8.8?</b></summary>
Sim! O plugin é compatível com versões 1.8.8 até 1.20+
</details>

<details>
<summary><b>Como uso tags coloridas?</b></summary>
Ao criar o clã, use códigos de cor na tag: <code>/clan criar MeuClan &c&lTAG</code>
</details>

<details>
<summary><b>Como deposito 1 milhão no banco?</b></summary>
Use: <code>/clan banco depositar 1m</code> (aceita k, m, b, t)
</details>

<details>
<summary><b>Os placeholders mostram a tag colorida?</b></summary>
Sim! O placeholder <code>%vkclans_tag%</code> mostra a tag com as cores escolhidas pelo clã.
</details>

---

## 📜 Changelog

### v1.1.0 (2024)
- ✨ **PlaceholderAPI** integrado com 19 placeholders
- ✨ **Alianças** entre clãs
- ✨ **Conquistas** (achievements)
- ✨ **Ranking Semanal** que reseta automaticamente
- ✨ **Limite de IP** para evitar multi-contas
- ✨ **Friendly Fire** configurável
- ✨ **Tags coloridas** nos placeholders
- ✨ **Suporte a 1k/1m/1b** em comandos de banco
- 🐛 Fix NullPointerException em LocationUtil
- 🐛 Fix NullPointerException em ClanLog
- 🐛 Fix teleporte para base com mundo inválido

### v1.0.9 (2024)
- ✨ Renomeado de FoxClans para VKClans
- ✨ Blacklist de nomes e tags
- ✨ Restrição de mundos para base
- 🐛 Várias correções de bugs

### v1.0.0 (2024)
- 🎉 Lançamento inicial
- ✅ Sistema completo de clãs
- ✅ Sistema de guerra
- ✅ Ranking com 6 categorias
- ✅ Banco do clã com Vault
- ✅ Sistema de níveis
- ✅ Baú compartilhado
- ✅ Chat privado
- ✅ Sistema de missões
- ✅ API completa
- ✅ Log de ações

---

## 🛠️ Compilando do Código-Fonte

```bash
# Clone o repositório
git clone https://github.com/Gustavosareto/VK_Clans.git

# Entre na pasta
cd VK_Clans

# Compile com Maven
mvn clean package

# O JAR estará em target/VKClans-1.1.0.jar
```

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor, leia o [CONTRIBUTING.md](CONTRIBUTING.md) antes de enviar pull requests.

---

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

<p align="center">
  Feito com ❤️ para a comunidade Minecraft
</p>

<p align="center">
  ⭐ Se este projeto te ajudou, considere dar uma estrela!
</p>
