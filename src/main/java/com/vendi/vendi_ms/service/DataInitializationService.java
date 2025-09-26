package com.vendi.vendi_ms.service;

import com.vendi.vendi_ms.model.*;
import com.vendi.vendi_ms.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Service responsável pela inicialização de dados básicos do sistema.
 * 
 * @author Sistema Vendi
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService implements CommandLineRunner {

    private final PermissaoRepository permissaoRepository;
    private final PerfilRepository perfilRepository;
    private final EmpresaRepository empresaRepository;
    private final LojaRepository lojaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Inicializando dados básicos do sistema...");
        
        criarPermissoes();
        criarPerfis();
        criarEmpresaPadrao();
        criarUsuarioAdministrador();
        
        log.info("Dados básicos inicializados com sucesso!");
    }

    private void criarPermissoes() {
        if (permissaoRepository.count() == 0) {
            log.info("Criando permissões do sistema...");
            
            String[][] permissoesData = {
                {"dashboard", "Dashboard", "Acesso ao painel principal", "Sistema"},
                {"users", "Usuários", "Gerenciamento de usuários", "Sistema"},
                {"settings", "Configurações", "Configurações do sistema", "Sistema"},
                
                {"pos", "PDV - Vendas", "Acesso ao ponto de venda", "Vendas"},
                {"customers", "Clientes", "Gerenciamento de clientes", "Cadastros"},
                {"customers_view", "Clientes (Apenas Visualizar)", "Visualização de clientes", "Cadastros"},
                
                {"products", "Produtos", "Gerenciamento de produtos", "Cadastros"},
                {"inventory", "Estoque", "Controle de estoque", "Operações"},
                
                {"financial", "Financeiro", "Controle financeiro", "Financeiro"},
                {"reports", "Relatórios", "Geração de relatórios", "Relatórios"},
                {"reports_view", "Relatórios (Apenas Visualizar)", "Visualização de relatórios", "Relatórios"}
            };

            for (String[] permissaoData : permissoesData) {
                Permissao permissao = new Permissao();
                permissao.setChave(permissaoData[0]);
                permissao.setNome(permissaoData[1]);
                permissao.setDescricao(permissaoData[2]);
                permissao.setCategoria(permissaoData[3]);
                permissao.setAtivo(true);
                permissaoRepository.save(permissao);
            }
            
            log.info("Permissões criadas com sucesso!");
        }
    }

    private void criarPerfis() {
        if (perfilRepository.count() == 0) {
            log.info("Criando perfis do sistema...");
            
            Set<Permissao> todasPermissoes = new HashSet<>(permissaoRepository.findAtivas());
            Perfil admin = new Perfil();
            admin.setNome("Administrador");
            admin.setCodigo("admin");
            admin.setDescricao("Acesso total ao sistema");
            admin.setAtivo(true);
            admin.setSistema(true);
            admin.setPermissoes(todasPermissoes);
            perfilRepository.save(admin);
            
            
            Perfil vendedor = new Perfil();
            vendedor.setNome("Vendedor");
            vendedor.setCodigo("seller");
            vendedor.setDescricao("Acesso ao PDV e clientes");
            vendedor.setAtivo(true);
            vendedor.setSistema(true);
            vendedor.setPermissoes(Set.of(
                permissaoRepository.findByChaveAndNotDeleted("dashboard").orElse(null),
                permissaoRepository.findByChaveAndNotDeleted("pos").orElse(null),
                permissaoRepository.findByChaveAndNotDeleted("customers").orElse(null),
                permissaoRepository.findByChaveAndNotDeleted("reports_view").orElse(null)
            ));
            perfilRepository.save(vendedor);
            
            
            Perfil estoquista = new Perfil();
            estoquista.setNome("Estoquista");
            estoquista.setCodigo("stock");
            estoquista.setDescricao("Gestão de produtos e estoque");
            estoquista.setAtivo(true);
            estoquista.setSistema(true);
            estoquista.setPermissoes(Set.of(
                permissaoRepository.findByChaveAndNotDeleted("dashboard").orElse(null),
                permissaoRepository.findByChaveAndNotDeleted("inventory").orElse(null),
                permissaoRepository.findByChaveAndNotDeleted("products").orElse(null),
                permissaoRepository.findByChaveAndNotDeleted("reports_view").orElse(null)
            ));
            perfilRepository.save(estoquista);
            
            
            Perfil financeiro = new Perfil();
            financeiro.setNome("Financeiro");
            financeiro.setCodigo("financial");
            financeiro.setDescricao("Controle financeiro e relatórios");
            financeiro.setAtivo(true);
            financeiro.setSistema(true);
            financeiro.setPermissoes(Set.of(
                permissaoRepository.findByChaveAndNotDeleted("dashboard").orElse(null),
                permissaoRepository.findByChaveAndNotDeleted("financial").orElse(null),
                permissaoRepository.findByChaveAndNotDeleted("reports").orElse(null),
                permissaoRepository.findByChaveAndNotDeleted("customers_view").orElse(null)
            ));
            perfilRepository.save(financeiro);
            
            log.info("Perfis criados com sucesso!");
        }
    }

    private void criarEmpresaPadrao() {
        if (empresaRepository.count() == 0) {
            log.info("Criando empresa padrão...");
            
            Empresa empresa = new Empresa();
            empresa.setRazaoSocial("Vendi Sistemas LTDA");
            empresa.setNomeFantasia("Vendi");
            empresa.setCnpj("12.345.678/0001-90");
            empresa.setEndereco("Rua das Flores, 123");
            empresa.setBairro("Centro");
            empresa.setCidade("São Paulo");
            empresa.setUf("SP");
            empresa.setCep("01234-567");
            empresa.setTelefone("(11) 1234-5678");
            empresa.setEmail("contato@vendi.com.br");
            empresa.setAtivo(true);
            empresaRepository.save(empresa);
            
            
            Loja loja = new Loja();
            loja.setNome("Loja Matriz");
            loja.setCodigo("001");
            loja.setDescricao("Loja matriz do sistema");
            loja.setEndereco("Rua das Flores, 123");
            loja.setBairro("Centro");
            loja.setCidade("São Paulo");
            loja.setUf("SP");
            loja.setCep("01234-567");
            loja.setTelefone("(11) 1234-5678");
            loja.setAtivo(true);
            loja.setEmpresa(empresa);
            lojaRepository.save(loja);
            
            log.info("Empresa e loja padrão criadas com sucesso!");
        }
    }

    private void criarUsuarioAdministrador() {
        if (usuarioRepository.count() == 0) {
            log.info("Criando usuário administrador...");
            
            Usuario admin = new Usuario();
            admin.setNome("Administrador do Sistema");
            admin.setUsername("admin");
            admin.setCracha("ADM001");
            admin.setEmail("admin@vendi.com.br");
            admin.setSenha(passwordEncoder.encode("admin123"));
            admin.setAtivo(true);
            admin.setRoot(true);
            
            
            Perfil perfilAdmin = perfilRepository.findByCodigoAndNotDeleted("admin")
                    .orElseThrow(() -> new RuntimeException("Perfil admin não encontrado"));
            admin.setPerfil(perfilAdmin);
            
            
            Empresa empresa = empresaRepository.findAll().get(0);
            admin.setEmpresa(empresa);
            
            
            Loja loja = lojaRepository.findAll().get(0);
            admin.setLoja(loja);
            
            usuarioRepository.save(admin);
            
            log.info("Usuário administrador criado com sucesso!");
            log.info("Username: admin | Senha: admin123");
        }
    }
}
