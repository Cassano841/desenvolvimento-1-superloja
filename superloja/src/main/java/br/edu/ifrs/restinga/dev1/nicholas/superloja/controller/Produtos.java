package br.edu.ifrs.restinga.dev1.nicholas.superloja.controller;

import br.edu.ifrs.restinga.dev1.nicholas.superloja.erro.NaoEncontrado;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.erro.RequisicaoInvalida;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.dao.EmbalagemDAO;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.dao.FornecedorDAO;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.dao.GeneroDAO;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.dao.ModeloDAO;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.dao.ProdutoDAO;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.entidade.Embalagem;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.entidade.Fornecedor;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.entidade.Genero;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.entidade.Modelo;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.entidade.Produto;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class Produtos {
    
    @Autowired
    ProdutoDAO produtoDAO;
    
    @Autowired
    EmbalagemDAO embalagemDAO;
    
    @Autowired
    ModeloDAO modeloDAO;
    
    @Autowired
    GeneroDAO generoDAO;
    
    @Autowired
    FornecedorDAO fornecedorDAO;
    
    @RequestMapping(path = "/produtos/pesquisar/fornecedor", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Produto> pesquisarFornecedor(@RequestParam(required = false) int id){
        Optional <Fornecedor> optionalFornecedor = fornecedorDAO.findById(id);
        if(!optionalFornecedor.isPresent()){
            throw new NaoEncontrado("Deu ruim!");
        }
        Fornecedor fornecedor = optionalFornecedor.get();
        return produtoDAO.findByFornecedores(fornecedor);
    }
    
    @RequestMapping(path = "/produtos/pesquisar/genero", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Produto> pesquisarGenero(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) Boolean perecivel) {
        
        if(perecivel != null && id != null){
            throw new RequisicaoInvalida("Informar ou id ou perecivel!");
        }
        if(perecivel != null){
            return produtoDAO.findByGeneroPerecivel(perecivel);
        } else if (id != null){
            //Optional<Genero> optionalGenero = generoDAO
            return null;
            }
       return null;
    }
    
    @RequestMapping(path = "/produtos/pesquisar/valor", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Produto> pesquisarvalor(
            @RequestParam(required = false) float inicio,
            @RequestParam(required = false) float fim) {
       
            return produtoDAO.findByValorBetween(inicio, fim);
    }
    
    @RequestMapping(path = "/produtos/pesquisar/nome", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Produto> pesquisarNome(
            @RequestParam(required = false) String contem,
            @RequestParam(required = false) String comeca) {
        if(contem != null) {
            return produtoDAO.findByNomeContaining(contem);
        } else if (comeca != null) {
            return produtoDAO.findByNomeStartingWith(comeca);  
        } else{
            throw new RequisicaoInvalida("Deu ruim!");
        }
    }
    
    @RequestMapping(path = "/produtos/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Produto> listar() {
        return produtoDAO.findAll();    
    }
    
    @RequestMapping(path = "/produtos/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Produto buscar(@PathVariable int id) {
        final Optional<Produto> findById = produtoDAO.findById(id);
        if(findById.isPresent()) {
            return findById.get();
        } else {
            throw new NaoEncontrado("ID não encontrada!");
        }
    }
    
    @RequestMapping(path="/produtos/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void apagar(@PathVariable int id) {
        if(produtoDAO.existsById(id)) {
            produtoDAO.deleteById(id);
        } else {
            throw new NaoEncontrado("ID não encontrada!");
        }
                
    }
    
    @RequestMapping(path="/produtos/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void atualizar(@PathVariable int id, @RequestBody Produto produto) {
        final Produto produtoBanco = this.buscar(id);

        if(produto.getValor()<0) {
            throw new RequisicaoInvalida("Valor do produto deve ser maior que 0");
        }
        
        produtoBanco.setNome(produto.getNome());
        produtoBanco.setValor(produto.getValor());
        Embalagem embalagemPer=embalagemDAO.save(produto.getEmbalagem());
        produtoBanco.setEmbalagem(embalagemPer);
        produtoBanco.setGenero(produto.getGenero());
        produtoDAO.save(produtoBanco);
    }
    

    @RequestMapping(path = "/produtos/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Produto cadastrar(@RequestBody Produto produto) {
        if(produto.getValor()<0) {
            throw new RequisicaoInvalida("Valor do produto deve ser maior que 0");
        } else {
            Embalagem embalagemPer = embalagemDAO.save(produto.getEmbalagem());
            produto.setEmbalagem(embalagemPer);
            Produto produtoBanco = produtoDAO.save(produto);
            return produtoBanco;
        }
    }

    @RequestMapping(path = "/produtos/{idProduto}/modelos/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Modelo cadastrarModelo(@PathVariable int idProduto, @RequestBody Modelo modelo) {
        Produto produtoBanco = this.buscar(idProduto);
        Modelo modeloBanco=modeloDAO.save(modelo);
        produtoBanco.getModelos().add(modeloBanco);
        produtoDAO.save(produtoBanco);
        return modeloBanco;
    }
    
    
    @RequestMapping(path = "/produtos/{idProduto}/fornecedores/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Fornecedor cadastrarFornecedor(@PathVariable int idProduto, @RequestBody Fornecedor fornecedor){
        Produto produto = this.buscar(idProduto);
        produto.getFornecedores().add(fornecedor);
        produtoDAO.save(produto);
        
        return fornecedor;
    }
    
    @RequestMapping(path = "/produtos/{idProduto}/fornecedores/", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public List<Fornecedor> listarFornecedor(@PathVariable int idProduto){
        return this.buscar(idProduto).getFornecedores();
    }
            
}
