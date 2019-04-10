package br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.dao;

import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.entidade.Fornecedor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FornecedorDAO extends CrudRepository<Fornecedor, Integer> {
    
}
