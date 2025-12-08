# Changelog

Todas as mudanças notáveis deste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/pt-BR/1.0.0/),
e este projeto adere ao [Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [1.2.0] - 2025-12-08

### Adicionado

#### Interface Gráfica Interativa (GUI)
- **Criação de Clãs via GUI**: Sistema completo de criação de clãs através de menus gráficos
- **Menu de Criação Guiado**: Passos intuitivos para inserir nome, tag e cor do clã
- **Seleção de Cores**: Interface dedicada com preview das cores disponíveis
- **Captura de Chat Segura**: Sistema de input via chat com validação em tempo real
- **Timeout Automático**: Prevenção de sessões abandonadas (30 segundos)
- **Validações Integradas**: Verificação de disponibilidade, formato e restrições

#### Suporte a Banco de Dados
- **MySQL/MariaDB**: Migração completa do sistema de armazenamento
- **HikariCP**: Pool de conexões otimizado para performance
- **Async Saves**: Salvamentos assíncronos para não bloquear o servidor
- **Backup Automático**: Sistema de backup integrado com compressão
- **Migração Transparente**: Conversão automática de YAML para DB

#### Melhorias de Performance
- **Salvamentos Assíncronos**: Todas as operações de I/O em background
- **Cache Otimizado**: Melhor utilização de memória
- **Cooldowns Inteligentes**: Sistema de cooldowns configurável
- **Validações Eficientes**: Regex compilado e cache de padrões

#### Segurança e Validações
- **Input Sanitization**: Limpeza de entradas do usuário
- **Rate Limiting**: Proteção contra spam de comandos
- **World Restrictions**: Restrição de comandos por mundo
- **IP Limits**: Controle de múltiplas contas por IP
- **SQL Injection Protection**: Prepared statements

#### API para Desenvolvedores
- **VKClansAPI**: Interface completa para integração
- **Eventos Customizados**: ClanEventManager com Observer pattern
- **Factory Pattern**: ClanFactory para criação padronizada
- **Listener Manager**: Gerenciamento centralizado de listeners

### Corrigido
- **Deprecation Warnings**: Atualização de métodos deprecated
- **Memory Leaks**: Correção de vazamentos de memória
- **Thread Safety**: Operações seguras em ambientes multi-thread
- **Error Handling**: Tratamento robusto de exceções

### Alterado
- **Dependências**: Atualização para MySQL Connector/J
- **Estrutura**: Refatoração com padrões de design
- **Configuração**: Novas opções para performance e segurança

## [1.0.9] - 2024-12-01

### Adicionado

#### Sistema de Aliancas
- Comando `/clan alianca convidar <clan>` para enviar convite de alianca
- Comando `/clan alianca aceitar/recusar <clan>` para gerenciar convites
- Comando `/clan alianca remover <clan>` para desfazer alianca
- Comando `/clan alianca lista` para ver aliados
- Limite configuravel de aliados por cla
- Friendly fire desativado entre aliados (configuravel)
- Arquivo `allies.yml` para persistencia

#### Sistema de Conquistas (Achievements)
- 16 conquistas disponiveis para desbloquear
- Categorias: Kills, Guerras, Membros, Nivel, Banco
- Recompensas em pontos e dinheiro ao desbloquear
- Comando `/clan conquistas` para ver progresso
- Notificacao broadcast ao desbloquear
- Arquivo `achievements.yml` para persistencia

#### Ranking Semanal
- Ranking separado de kills semanais
- Reset automatico a cada 7 dias
- Recompensas para Top 3 (pontos e dinheiro)
- Broadcast do Top 3 ao resetar
- Comando `/clan semanal` para ver ranking
- Arquivo `weekly.yml` para persistencia

#### Limite de IP (Anti Multi-Conta)
- Sistema para limitar clas por IP
- Configuracao `max-clas-por-ip` no config.yml
- Verificacao ao criar e entrar em cla
- Registro automatico de IPs no login
- Arquivo `ips.yml` para persistencia

#### Friendly Fire
- Configuracao para permitir/bloquear dano entre membros do mesmo cla
- Opcao separada para permitir durante guerras
- Listener dedicado `FriendlyFireListener`

#### PlaceholderAPI Integration
- Classe `VKClansPlaceholder` com 15+ placeholders
- Placeholders: clan, tag, role, kills, deaths, kdr, points, level, members, bank, wars_won, wars_lost, weekly_kills, in_war, allies, weekly_reset

### Alterado
- Atualizado sistema de kill para adicionar kills semanais
- Sistema de kill agora verifica conquistas apos cada morte

---

## [1.0.8] - 2024-12-01

### Adicionado
- Sistema de blacklist para nomes e tags de cla
- Sistema de restricao de mundos para definir base
- Sistema de promocao com escolha de cargo especifico

---

## [1.0.1] - 2024-12-01

### 🐛 Corrigido

#### Sistema de GUI
- **MessageUtil**: Corrigido bug onde títulos de GUI recebiam o prefixo do plugin, causando falhas na detecção de menus
- **ClanGUIListener**: Melhorada detecção de menus usando `ChatColor.stripColor()` para comparação sem códigos de cor
- **ClanGUIListener**: Adicionado suporte a caracteres acentuados na detecção de títulos (ex: "Clã" → "meu cl")

### 🔄 Alterado

#### MessageUtil
- Método `get()` agora não adiciona prefixo para chaves que começam com "gui-"
- Novo método `getRaw()` para obter mensagens sem prefixo (útil para títulos e GUIs)

### 🔧 Técnico
- Adicionado logging de debug temporário para diagnóstico de problemas com GUI

---

## [1.0.0] - 2024-12-01

### ✨ Adicionado

#### Sistema de Clãs
- Criação de clãs com nome e tag personalizados
- Sistema de convites com expiração configurável
- Limite de membros configurável
- Pontos iniciais configuráveis

#### Sistema de Cargos
- 4 níveis hierárquicos: Dono, Sub-Dono, Administrador, Membro
- Promoção e rebaixamento de membros
- Transferência de liderança
- Permissões específicas por cargo

#### Sistema de Base
- Definir localização da base do clã
- Teleporte com delay configurável
- Cancelamento ao mover (configurável)
- Cooldown entre teleportes

#### Sistema de Guerra ⚔️
- Declarar guerra contra outros clãs
- Duração configurável (em horas)
- Contagem de kills para vencer
- Cooldown entre guerras
- Recompensas em pontos e dinheiro
- Histórico de guerras

#### Sistema de Ranking 🏆
- Ranking por pontos
- Ranking por kills
- Ranking por KDR (Kill/Death Ratio)
- Ranking por nível
- Ranking por saldo do banco
- Ranking por vitórias em guerras

#### Banco do Clã 💰
- Depósitos e saques
- Valores mínimos configuráveis
- Taxas de transação
- Integração completa com Vault
- Funciona sem Vault (desativado automaticamente)

#### Sistema de Níveis 📈
- 5 níveis iniciais (expansível)
- XP por kills, blocos minerados e guerras
- Aumento de limite de membros por nível
- Upgrade com dinheiro do banco

#### Baú Compartilhado 📦
- Inventário compartilhado entre membros
- Tamanho aumenta com nível do clã
- Log de todas as transações
- Itens salvos automaticamente

#### Chat Privado 💬
- Chat exclusivo do clã
- Toggle para ativar/desativar
- Formato personalizável
- Modo spy para administradores

#### Sistema de Missões 🎯
- 4 tipos de missões:
  - Matar jogadores
  - Minerar blocos
  - Vencer guerras
  - Depositar dinheiro
- Geração diária automática
- Recompensas em pontos e dinheiro
- Duração configurável

#### Log de Ações 📋
- 17 tipos de ações registradas
- Histórico persistente
- Visualização por comando
- Limite de registros por clã

#### Interface Gráfica (GUI)
- Menu principal interativo
- Menu de membros com paginação
- Confirmações de ações importantes
- Design intuitivo

#### API para Desenvolvedores
- Classe VKClansAPI completa
- Acesso a todos os sistemas
- Métodos para integração
- Documentação completa

#### Configuração
- `config.yml` com todas as opções
- `messages.yml` com 70+ mensagens
- Cores e formatação suportadas
- Placeholders em todas as mensagens

#### Comandos
- 25 subcomandos implementados
- Tab completion completo
- Mensagens de ajuda
- Verificação de permissões

#### Permissões
- Permissões granulares
- Permissões admin separadas
- Bypass para staff

### 🔧 Técnico
- Suporte a Minecraft 1.8.8+
- Java 8+ compatível
- Armazenamento em YAML
- Singleton pattern nos managers
- Event-driven architecture

---

## [Unreleased]

### Planejado
- [ ] Suporte a MySQL/SQLite
- [ ] Integração com PlaceholderAPI
- [ ] Sistema de alianças
- [ ] Sistema de territórios
- [ ] BStats metrics
- [ ] Update checker
- [ ] Mais tipos de missões
- [ ] Achievements do clã
- [ ] Sistema de upgrades personalizados

---

## Legenda

- ✨ **Adicionado** - Novas funcionalidades
- 🔄 **Alterado** - Mudanças em funcionalidades existentes
- 🗑️ **Removido** - Funcionalidades removidas
- 🐛 **Corrigido** - Correções de bugs
- 🔒 **Segurança** - Correções de vulnerabilidades
- ⚠️ **Deprecado** - Funcionalidades que serão removidas
