# 🦊 VKClans - Sistema de Clãs para Minecraft

<p align="center">
  <img src="https://img.shields.io/badge/Minecraft-1.8.8-green?style=for-the-badge&logo=minecraft" alt="Minecraft">
  <img src="https://img.shields.io/badge/Spigot-Compatible-orange?style=for-the-badge" alt="Spigot">
  <img src="https://img.shields.io/badge/Java-8+-red?style=for-the-badge&logo=java" alt="Java">
  <img src="https://img.shields.io/badge/Version-1.0.1-blue?style=for-the-badge" alt="Version">
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge" alt="License">
</p>

<p align="center">
  <b>Um plugin completo e profissional de clãs para servidores Minecraft</b>
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
- [API para Desenvolvedores](#-api-para-desenvolvedores)
- [Placeholders](#-placeholders)
- [Screenshots](#-screenshots)
- [FAQ](#-faq)
- [Suporte](#-suporte)
- [Licença](#-licença)

---

## 📖 Sobre

**VKClans** é um sistema de clãs completo e otimizado para servidores Minecraft 1.8.8+. O plugin oferece uma experiência rica em funcionalidades, incluindo sistema de guerra, banco do clã, níveis, missões, chat privado e muito mais.

### ✨ Destaques

- 🎮 **Interface Gráfica (GUI)** - Menus intuitivos para fácil gerenciamento
- ⚔️ **Sistema de Guerra** - Batalhe contra outros clãs por supremacia
- 🏆 **Ranking Competitivo** - 6 tipos diferentes de rankings
- 💰 **Economia Integrada** - Banco do clã com Vault
- 📈 **Progressão** - Sistema de níveis com benefícios
- 📦 **Baú Compartilhado** - Armazenamento comum entre membros
- 💬 **Chat Privado** - Comunicação exclusiva do clã
- 🎯 **Missões** - Desafios diários com recompensas
- 📊 **API Completa** - Integração fácil com outros plugins

---

## 🚀 Funcionalidades

### Sistema de Clãs
- ✅ Criar, editar e deletar clãs
- ✅ Tags personalizadas no chat
- ✅ Sistema de convites com expiração
- ✅ Limite de membros configurável
- ✅ Base do clã com teleporte

### Hierarquia de Cargos
| Cargo | Nível | Permissões |
|-------|-------|------------|
| 👑 Dono | 4 | Todas as permissões |
| ⭐ Sub-Dono | 3 | Gerenciar membros, base, banco, guerra |
| 🛡️ Administrador | 2 | Convidar, expulsar membros |
| 👤 Membro | 1 | Comandos básicos |

### Sistema de Guerra ⚔️
- Declarar guerra contra outros clãs
- Duração e kills configuráveis
- Recompensas para o vencedor
- Histórico de guerras

### Sistema de Ranking 🏆
- **Por Pontos** - Ranking geral
- **Por Kills** - Clãs mais mortais
- **Por KDR** - Melhor K/D ratio
- **Por Nível** - Clãs mais avançados
- **Por Banco** - Clãs mais ricos
- **Por Vitórias** - Mais guerras vencidas

### Banco do Clã 💰
- Depósitos e saques
- Valores mínimos configuráveis
- Taxas de transação
- Integração com Vault

### Sistema de Níveis 📈
| Nível | XP Necessário | Max Membros |
|-------|---------------|-------------|
| 1 | 0 | 10 |
| 2 | 1.000 | 15 |
| 3 | 5.000 | 20 |
| 4 | 15.000 | 25 |
| 5 | 30.000 | 30 |

### Baú Compartilhado 📦
- Inventário compartilhado entre membros
- Tamanho aumenta com nível do clã
- Log de todas as transações
- Proteção contra roubo

### Chat Privado 💬
- Chat exclusivo do clã
- Toggle para ativar/desativar
- Modo spy para administradores
- Formatação personalizável

### Sistema de Missões 🎯
- **Matar Jogadores** - Kill X players
- **Minerar Blocos** - Mine X blocks
- **Vencer Guerras** - Win X wars
- **Depositar Dinheiro** - Deposit $X

### Log de Ações 📋
Todas as ações são registradas:
- Entrada/saída de membros
- Promoções/rebaixamentos
- Depósitos/saques
- Declarações de guerra
- E muito mais...

---

## 📋 Requisitos

| Requisito | Versão |
|-----------|--------|
| Minecraft Server | 1.8.8+ |
| Java | 8+ |
| Spigot/Paper | Compatível |
| Vault | Opcional (para banco) |

---

## 📥 Instalação

1. **Download** - Baixe o arquivo `VKClans-1.0.1.jar`
2. **Plugins** - Coloque na pasta `plugins` do servidor
3. **Reinicie** - Reinicie o servidor
4. **Configure** - Edite os arquivos em `plugins/VKClans/`

```bash
plugins/
└── VKClans/
    ├── config.yml      # Configurações gerais
    ├── messages.yml    # Mensagens customizáveis
    ├── clans.yml       # Dados dos clãs (auto-gerado)
    ├── clanlogs.yml    # Logs de ações (auto-gerado)
    └── clancontents.yml # Baús dos clãs (auto-gerado)
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
| `/clan banco <depositar/sacar> <valor>` | Banco do clã |
| `/clan nivel [upgrade]` | Ver/comprar níveis |
| `/clan bau` | Abre o baú compartilhado |
| `/clan log` | Histórico de ações |
| `/clan chat` | Toggle do chat privado |
| `/clan missoes` | Ver missões ativas |

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
| `VKClans.use` | Usar comandos básicos | true |
| `VKClans.create` | Criar clãs | true |
| `VKClans.chat` | Usar chat do clã | true |

### Permissões Avançadas

| Permissão | Descrição | Padrão |
|-----------|-----------|--------|
| `VKClans.guerra` | Declarar guerra | op |
| `VKClans.banco` | Usar banco do clã | true |
| `VKClans.bau` | Usar baú do clã | true |
| `VKClans.missoes` | Ver missões | true |

### Permissões Admin

| Permissão | Descrição | Padrão |
|-----------|-----------|--------|
| `VKClans.admin` | Comandos admin | op |
| `VKClans.spy` | Espionar chats | op |
| `VKClans.reload` | Recarregar plugin | op |
| `VKClans.bypass.*` | Bypass de restrições | op |

---

## ⚙️ Configuração

### config.yml

```yaml
# ============================================
#           VKClans - CONFIGURAÇÃO
# ============================================

# Configurações Gerais
geral:
  prefixo: "&6[Clans] &r"
  nome-minimo: 3
  nome-maximo: 16
  tag-minimo: 2
  tag-maximo: 5
  membros-maximo: 10
  pontos-iniciais: 0
  cooldown-convite: 60

# Sistema de Base
base:
  tempo-espera: 5
  cancelar-ao-mover: true
  cooldown: 60

# Sistema de Guerra
guerra:
  habilitado: true
  duracao-horas: 24
  kills-para-vencer: 10
  cooldown-horas: 48
  recompensa-pontos: 100
  recompensa-dinheiro: 1000.0

# Sistema de Banco
banco:
  habilitado: true
  deposito-minimo: 100.0
  saque-minimo: 100.0
  taxa-deposito: 0.0
  taxa-saque: 0.05

# Sistema de Níveis
niveis:
  habilitado: true
  xp-por-kill: 10
  xp-por-bloco: 1
  xp-por-guerra-vencida: 500

# Sistema de Baú
bau:
  habilitado: true
  linhas-por-nivel: 1

# Sistema de Missões
missoes:
  habilitadas: true
  quantidade-diaria: 3
  duracao-horas: 24

# Sistema de Chat
chat:
  formato: "&7[&6{clan}&7] &e{cargo} &f{player}&7: &f{message}"
```

### messages.yml

Todas as mensagens são customizáveis! Veja o arquivo completo em `plugins/VKClans/messages.yml`

---

## 🔌 API para Desenvolvedores

O VKClans oferece uma API completa para integração com outros plugins.

### Obtendo a API

```java
import com.VKClans.api.VKClansAPI;

VKClansAPI api = VKClansAPI.getInstance();
```

### Exemplos de Uso

```java
// Verificar se jogador tem clã
Clan clan = api.getPlayerClan(player.getUniqueId());
if (clan != null) {
    // Jogador tem clã
}

// Obter informações do clã
String clanName = api.getClanName(player.getUniqueId());
String clanTag = api.getClanTag(player.getUniqueId());
int clanLevel = api.getClanLevel(player.getUniqueId());

// Verificar cargo
ClanRole role = api.getMemberRole(player.getUniqueId());
boolean isLeader = api.isClanLeader(player.getUniqueId());

// Verificar se estão no mesmo clã
boolean sameClan = api.areInSameClan(player1.getUniqueId(), player2.getUniqueId());

// Sistema de pontos
api.addPoints(clan, 100);
api.removePoints(clan, 50);

// Sistema de guerra
boolean atWar = api.areAtWar(clan1, clan2);
ClanWar war = api.getActiveWar(clan);

// Ranking
List<Clan> topClans = api.getTopClans(10);
int position = api.getClanRankingPosition(clan, "points");

// Banco
double balance = api.getClanBankBalance(clan);

// Experiência
api.addExperience(clan, 500);

// Chat do clã
api.sendClanMessage(clan, player, "Mensagem para o clã!");

// Missões
api.addMissionProgress(clan, MissionType.KILL_PLAYERS, 1);

// Eventos
api.registerKill(killer.getUniqueId(), victim.getUniqueId());
api.registerBlocksMined(player.getUniqueId(), 64);
```

### Métodos Disponíveis

| Categoria | Métodos |
|-----------|---------|
| **Clãs** | `getPlayerClan`, `getClanByName`, `getClanByTag`, `getAllClans`, `clanExists` |
| **Membros** | `getMemberRole`, `isClanLeader`, `areInSameClan`, `getClanMembers` |
| **Pontos** | `addPoints`, `removePoints`, `setPoints`, `getPoints` |
| **Banco** | `getClanBankBalance`, `addToClanBank`, `removeFromClanBank` |
| **Níveis** | `getClanLevel`, `addExperience`, `getClanExperience`, `getMemberLimit` |
| **Guerra** | `areAtWar`, `getActiveWar`, `isInAnyWar` |
| **Ranking** | `getTopClans`, `getClanRankingPosition` |
| **Missões** | `getActiveMissions`, `addMissionProgress` |
| **Chat** | `isClanChatEnabled`, `sendClanMessage` |
| **Stats** | `getClanKills`, `getClanDeaths`, `getClanKDR`, `getClanWarsWon` |
| **Eventos** | `registerKill`, `registerDeath`, `registerBlocksMined` |

---

## 🏷️ Placeholders

### PlaceholderAPI (Em breve)

| Placeholder | Descrição |
|-------------|-----------|
| `%VKClans_clan_name%` | Nome do clã |
| `%VKClans_clan_tag%` | Tag do clã |
| `%VKClans_clan_level%` | Nível do clã |
| `%VKClans_clan_members%` | Quantidade de membros |
| `%VKClans_clan_points%` | Pontos do clã |
| `%VKClans_clan_kills%` | Kills do clã |
| `%VKClans_clan_deaths%` | Deaths do clã |
| `%VKClans_clan_kdr%` | KDR do clã |
| `%VKClans_player_role%` | Cargo do jogador |
| `%VKClans_player_role_color%` | Cor do cargo |

---

## 📸 Screenshots

### Menu Principal
```
╔══════════════════════════════════════╗
║         🦊 Menu do Clã              ║
╠══════════════════════════════════════╣
║  [📋 Info]  [👥 Membros]  [🏠 Base]  ║
║                                      ║
║  [⚔️ Guerra] [🏆 Ranking] [💰 Banco] ║
║                                      ║
║  [📈 Níveis] [📦 Baú]  [📜 Logs]    ║
║                                      ║
║  [💬 Chat]  [🎯 Missões] [❌ Sair]   ║
╚══════════════════════════════════════╝
```

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
<summary><b>Os dados são salvos em banco de dados?</b></summary>
Atualmente os dados são salvos em arquivos YAML. Suporte a MySQL está planejado para versões futuras.
</details>

<details>
<summary><b>Como desativo um sistema específico?</b></summary>
Cada sistema pode ser desativado individualmente no config.yml usando a opção `habilitado: false`
</details>

<details>
<summary><b>Posso personalizar as mensagens?</b></summary>
Sim! Todas as mensagens estão no arquivo messages.yml e são 100% customizáveis.
</details>

---

## 🛠️ Compilando do Código-Fonte

```bash
# Clone o repositório
git clone https://github.com/seu-usuario/VKClans.git

# Entre na pasta
cd VKClans

# Compile com Maven
mvn clean package

# O JAR estará em target/VKClans-1.0.1.jar
```

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor, leia o [CONTRIBUTING.md](CONTRIBUTING.md) antes de enviar pull requests.

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFeature`)
3. Commit suas mudanças (`git commit -m 'Add: nova feature'`)
4. Push para a branch (`git push origin feature/NovaFeature`)
5. Abra um Pull Request

---

## 📞 Suporte

- 📧 **Email**: suporte@seuservidor.com
- 💬 **Discord**: [Servidor de Suporte](https://discord.gg/seuservidor)
- 🐛 **Issues**: [GitHub Issues](https://github.com/seu-usuario/VKClans/issues)

---

## 📜 Changelog

### v1.0.1 (2024)
- 🐛 Corrigido bug no MessageUtil que adicionava prefixo em títulos de GUI
- 🐛 Melhorada detecção de menus usando stripColor() para evitar problemas com acentos
- ✨ Novo método `getRaw()` no MessageUtil para mensagens sem prefixo

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

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

---

<p align="center">
  Feito com ❤️ para a comunidade Minecraft
</p>

<p align="center">
  ⭐ Se este projeto te ajudou, considere dar uma estrela!
</p>
