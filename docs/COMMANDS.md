# 🎮 VKClans - Guia de Comandos

Este guia contém todos os comandos disponíveis no VKClans com exemplos de uso.

---

## 📋 Índice

- [Comandos Básicos](#comandos-básicos)
- [Comandos de Gerenciamento](#comandos-de-gerenciamento)
- [Comandos de Base](#comandos-de-base)
- [Comandos Avançados](#comandos-avançados)
- [Comandos de Administração](#comandos-de-administração)

---

## Comandos Básicos

### `/clan criar <nome> <tag>`
Cria um novo clã.

**Exemplo:**
```
/clan criar Warriors WAR
```

**Requisitos:**
- Não estar em um clã
- Nome: 3-16 caracteres
- Tag: 2-5 caracteres
- Permissão: `VKClans.create`

---

### `/clan menu`
Abre o menu principal do clã com interface gráfica.

**Alias:** `/clans`

---

### `/clan info [nome]`
Mostra informações do clã.

**Exemplos:**
```
/clan info           # Seu clã
/clan info Warriors  # Clã específico
```

**Informações mostradas:**
- Nome e tag
- Líder
- Nível e pontos
- Membros online/total
- Estatísticas (kills, deaths, KDR)
- Guerras vencidas/perdidas

---

### `/clan membros`
Lista todos os membros do seu clã.

**Mostra:**
- Nome do jogador
- Cargo (com cor)
- Status (online/offline)

---

### `/clan convidar <jogador>`
Convida um jogador para o clã.

**Exemplo:**
```
/clan convidar Steve
```

**Requisitos:**
- Ser Administrador ou superior
- Clã não estar cheio
- Jogador não ter clã

---

### `/clan aceitar`
Aceita o convite pendente para um clã.

---

### `/clan recusar`
Recusa o convite pendente.

---

### `/clan sair`
Sai do clã atual.

**Nota:** O líder não pode sair. Deve transferir a liderança ou deletar o clã.

---

## Comandos de Gerenciamento

### `/clan kick <jogador>`
Expulsa um membro do clã.

**Exemplo:**
```
/clan kick Alex
```

**Requisitos:**
- Ser Administrador ou superior
- Não pode expulsar alguém de cargo igual ou superior

---

### `/clan promover <jogador>`
Promove um membro para o próximo cargo.

**Exemplo:**
```
/clan promover Steve
```

**Hierarquia:**
```
Membro → Administrador → Sub-Dono → Dono
```

**Requisitos:**
- Ser Sub-Dono ou superior
- Não pode promover acima do seu cargo

---

### `/clan rebaixar <jogador>`
Rebaixa um membro para o cargo anterior.

**Exemplo:**
```
/clan rebaixar Alex
```

**Requisitos:**
- Ser Sub-Dono ou superior

---

### `/clan transferir <jogador>`
Transfere a liderança do clã para outro membro.

**Exemplo:**
```
/clan transferir Steve
```

**Requisitos:**
- Ser o Dono do clã

---

### `/clan deletar`
Deleta o clã permanentemente.

**Requisitos:**
- Ser o Dono do clã
- Confirmação necessária (digitar novamente)

---

## Comandos de Base

### `/clan base`
Teleporta para a base do clã.

**Comportamento:**
1. Inicia contagem regressiva
2. Cancela se você se mover (configurável)
3. Teleporta após o delay
4. Aplica cooldown

---

### `/clan setbase`
Define a base do clã na sua localização atual.

**Requisitos:**
- Ser Sub-Dono ou superior

---

## Comandos Avançados

### `/clan guerra <clan>`
Declara guerra contra outro clã.

**Exemplo:**
```
/clan guerra Dragons
```

**Requisitos:**
- Ser Sub-Dono ou superior
- Não estar em guerra
- Cooldown entre guerras

**Mecânica:**
- Duração configurável (padrão: 24h)
- Vence quem atingir X kills primeiro
- Ou quem tiver mais kills quando o tempo acabar

---

### `/clan top [tipo]`
Mostra o ranking de clãs.

**Tipos disponíveis:**
```
/clan top          # Por pontos (padrão)
/clan top points   # Por pontos
/clan top kills    # Por kills
/clan top kdr      # Por K/D ratio
/clan top nivel    # Por nível
/clan top banco    # Por saldo do banco
/clan top wins     # Por vitórias em guerras
```

**Alias:** `/clan ranking`

---

### `/clan banco <ação> <valor>`
Gerencia o banco do clã.

**Exemplos:**
```
/clan banco depositar 1000
/clan banco sacar 500
```

**Ações:**
- `depositar` / `deposit` - Deposita dinheiro
- `sacar` / `withdraw` - Saca dinheiro (Sub-Dono+)

**Requisitos:**
- Vault instalado
- Valores mínimos configuráveis

---

### `/clan nivel [upgrade]`
Mostra informações do nível ou compra upgrade.

**Exemplos:**
```
/clan nivel           # Mostra info do nível
/clan nivel upgrade   # Compra próximo nível
```

**Benefícios por nível:**
- Mais membros permitidos
- Mais linhas no baú
- Status especial

---

### `/clan bau`
Abre o baú compartilhado do clã.

**Alias:** `/clan chest`

**Características:**
- Tamanho aumenta com nível
- Todas as ações são logadas
- Persistente entre reinícios

---

### `/clan log`
Mostra o histórico de ações do clã.

**Ações registradas:**
- Entrada/saída de membros
- Promoções/rebaixamentos
- Expulsões
- Alterações na base
- Transações do banco
- Guerras
- E mais...

---

### `/clan chat`
Ativa/desativa o chat exclusivo do clã.

**Quando ativo:**
- Todas suas mensagens vão apenas para membros do clã
- Formato especial no chat

---

### `/clan missoes`
Mostra as missões ativas do clã.

**Tipos de missões:**
- 🗡️ Matar X jogadores
- ⛏️ Minerar X blocos
- ⚔️ Vencer X guerras
- 💰 Depositar $X no banco

**Recompensas:**
- Pontos para o clã
- Dinheiro para o banco

---

## Comandos de Administração

### `/clan spy`
Ativa/desativa o modo espião.

**Permissão:** `VKClans.spy`

**Função:**
- Ver todas as mensagens de todos os chats de clãs

---

### `/clan reload`
Recarrega as configurações do plugin.

**Permissão:** `VKClans.reload`

**Recarrega:**
- config.yml
- messages.yml

---

### `/clan ajuda`
Mostra a lista de comandos disponíveis.

**Alias:** `/clan help`

---

## Resumo de Permissões por Cargo

| Comando | Membro | Admin | Sub-Dono | Dono |
|---------|:------:|:-----:|:--------:|:----:|
| criar | ✅ | ✅ | ✅ | ✅ |
| menu | ✅ | ✅ | ✅ | ✅ |
| info | ✅ | ✅ | ✅ | ✅ |
| membros | ✅ | ✅ | ✅ | ✅ |
| base | ✅ | ✅ | ✅ | ✅ |
| bau | ✅ | ✅ | ✅ | ✅ |
| chat | ✅ | ✅ | ✅ | ✅ |
| missoes | ✅ | ✅ | ✅ | ✅ |
| semanal | ✅ | ✅ | ✅ | ✅ |
| conquistas | ✅ | ✅ | ✅ | ✅ |
| alianca lista | ✅ | ✅ | ✅ | ✅ |
| sair | ✅ | ✅ | ✅ | ❌ |
| convidar | ❌ | ✅ | ✅ | ✅ |
| kick | ❌ | ✅ | ✅ | ✅ |
| setbase | ❌ | ❌ | ✅ | ✅ |
| promover | ❌ | ❌ | ✅ | ✅ |
| rebaixar | ❌ | ❌ | ✅ | ✅ |
| guerra | ❌ | ❌ | ✅ | ✅ |
| banco sacar | ❌ | ❌ | ✅ | ✅ |
| nivel upgrade | ❌ | ❌ | ✅ | ✅ |
| alianca convidar | ❌ | ❌ | ✅ | ✅ |
| alianca aceitar | ❌ | ❌ | ✅ | ✅ |
| alianca remover | ❌ | ❌ | ✅ | ✅ |
| transferir | ❌ | ❌ | ❌ | ✅ |
| deletar | ❌ | ❌ | ❌ | ✅ |

---

## Comandos de Alianca

### `/clan alianca`
Gerencia aliancas com outros clas.

**Subcomandos:**
```
/clan alianca convidar <clan> - Envia convite de alianca
/clan alianca aceitar <clan>  - Aceita convite pendente
/clan alianca recusar <clan>  - Recusa convite pendente
/clan alianca remover <clan>  - Remove alianca existente
/clan alianca lista           - Lista aliados e convites
```

**Requisitos:**
- Ser Sub-Dono ou superior para gerenciar
- Qualquer membro pode ver a lista

---

## Comandos de Ranking

### `/clan semanal`
Exibe o ranking semanal de kills.

**Informacoes exibidas:**
- Top 10 clas da semana
- Tempo ate o proximo reset
- Posicao do seu cla

---

### `/clan conquistas`
Exibe as conquistas do cla.

**Informacoes exibidas:**
- Total de conquistas obtidas
- Lista de todas as conquistas
- Status de cada uma (desbloqueada ou nao)

---

## Atalhos Úteis

| Comando | Atalho |
|---------|--------|
| `/clan menu` | `/clans` |
| `/clan top` | `/clan ranking` |
| `/clan bau` | `/clan chest` |
| `/clan ajuda` | `/clan help` |
| `/clan banco depositar` | `/clan banco deposit` |
| `/clan banco sacar` | `/clan banco withdraw` |
| `/clan alianca` | `/clan ally` |
| `/clan semanal` | `/clan weekly` |
| `/clan conquistas` | `/clan achievements` |

---

## Dicas

1. **Use Tab** para autocompletar comandos e nomes de jogadores
2. **O menu GUI** (`/clan menu`) é a forma mais fácil de gerenciar o clã
3. **Verifique o log** regularmente para monitorar atividades do clã
4. **Mantenha o banco** com saldo para upgrades de nível
5. **Complete missões** diariamente para ganhar recompensas
6. **Faca aliancas** com outros clas para protecao mutua
7. **Acompanhe o ranking semanal** para ganhar recompensas
