# Contribuindo para o VKClans

Obrigado pelo interesse em contribuir com o VKClans! 🦊

## 📋 Código de Conduta

Este projeto segue um código de conduta. Ao participar, você concorda em respeitá-lo.

## 🐛 Reportando Bugs

Antes de criar um issue:

1. **Verifique** se o bug já não foi reportado
2. **Atualize** para a versão mais recente
3. **Colete** informações relevantes:
   - Versão do Minecraft
   - Versão do Spigot/Paper
   - Versão do Java
   - Logs de erro
   - Passos para reproduzir

### Template de Bug Report

```markdown
**Descrição do Bug**
Uma descrição clara do bug.

**Passos para Reproduzir**
1. Vá para '...'
2. Execute '...'
3. Veja o erro

**Comportamento Esperado**
O que deveria acontecer.

**Screenshots/Logs**
Se aplicável, adicione screenshots ou logs.

**Ambiente**
- Minecraft: [ex: 1.8.8]
- Server: [ex: Spigot, Paper]
- Java: [ex: 8, 11, 17]
- VKClans: [ex: 1.0.0]
```

## 💡 Sugerindo Features

Adoramos novas ideias! Ao sugerir uma feature:

1. **Verifique** se já não foi sugerida
2. **Descreva** claramente a funcionalidade
3. **Explique** o caso de uso
4. **Considere** a compatibilidade com o sistema existente

## 🔧 Enviando Pull Requests

### Configurando o Ambiente

```bash
# Fork o repositório no GitHub

# Clone seu fork
git clone https://github.com/seu-usuario/VKClans.git

# Entre na pasta
cd VKClans

# Adicione o upstream
git remote add upstream https://github.com/original/VKClans.git

# Instale dependências
mvn install
```

### Processo de Desenvolvimento

1. **Crie uma branch** a partir da `main`:
   ```bash
   git checkout -b feature/minha-feature
   ```

2. **Faça suas alterações** seguindo o guia de estilo

3. **Teste** suas alterações:
   ```bash
   mvn clean test
   ```

4. **Commit** suas mudanças:
   ```bash
   git commit -m "Add: descrição da mudança"
   ```

5. **Push** para seu fork:
   ```bash
   git push origin feature/minha-feature
   ```

6. **Abra um Pull Request** no GitHub

### Convenções de Commit

Use prefixos nos commits:

| Prefixo | Uso |
|---------|-----|
| `Add:` | Nova funcionalidade |
| `Fix:` | Correção de bug |
| `Update:` | Atualização de código existente |
| `Remove:` | Remoção de código |
| `Refactor:` | Refatoração sem mudança de funcionalidade |
| `Docs:` | Alterações na documentação |
| `Style:` | Formatação, ponto e vírgula, etc |
| `Test:` | Adição/modificação de testes |

Exemplos:
```
Add: sistema de alianças entre clãs
Fix: erro ao teleportar para base inexistente
Update: melhorar performance do ranking
Docs: adicionar exemplos na API
```

## 📝 Guia de Estilo

### Java

```java
// ✅ Bom
public class MeuManager {
    private static MeuManager instance;
    
    private MeuManager() {}
    
    public static MeuManager getInstance() {
        if (instance == null) {
            instance = new MeuManager();
        }
        return instance;
    }
    
    /**
     * Descrição do método
     * @param param Descrição do parâmetro
     * @return Descrição do retorno
     */
    public String meuMetodo(String param) {
        if (param == null) {
            return null;
        }
        return param.toLowerCase();
    }
}

// ❌ Evite
public class meuManager {
    public static meuManager i;
    public String m(String p) { return p.toLowerCase(); }
}
```

### Diretrizes

1. **Nomenclatura**
   - Classes: `PascalCase`
   - Métodos/Variáveis: `camelCase`
   - Constantes: `UPPER_SNAKE_CASE`
   - Pacotes: `lowercase`

2. **Documentação**
   - Javadoc em métodos públicos
   - Comentários em código complexo
   - README atualizado

3. **Código**
   - Máximo 120 caracteres por linha
   - Indentação com 4 espaços
   - Chaves na mesma linha
   - Verificar null quando necessário

4. **Organização**
   - Uma classe por arquivo
   - Imports organizados
   - Métodos agrupados por funcionalidade

## 🧪 Testes

Antes de enviar um PR:

```bash
# Compile o projeto
mvn clean compile

# Execute os testes
mvn test

# Gere o JAR
mvn package
```

## 📁 Estrutura do Projeto

```
VKClans/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/VKClans/
│       │       ├── VKClans.java      # Classe principal
│       │       ├── api/               # API pública
│       │       ├── command/           # Comandos
│       │       ├── gui/               # Interfaces gráficas
│       │       ├── listener/          # Event listeners
│       │       ├── manager/           # Gerenciadores
│       │       ├── model/             # Modelos de dados
│       │       └── util/              # Utilitários
│       └── resources/
│           ├── config.yml             # Configurações
│           ├── messages.yml           # Mensagens
│           └── plugin.yml             # Descritor do plugin
├── pom.xml                            # Maven config
├── README.md                          # Documentação
├── CONTRIBUTING.md                    # Este arquivo
└── LICENSE                            # Licença
```

## ❓ Dúvidas?

- Abra uma [Discussion](https://github.com/seu-usuario/VKClans/discussions)
- Entre no [Discord](https://discord.gg/seuservidor)
- Envie um email para suporte@seuservidor.com

---

Obrigado por contribuir! 🎉
