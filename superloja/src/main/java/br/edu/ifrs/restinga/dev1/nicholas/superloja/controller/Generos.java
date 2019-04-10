package br.edu.ifrs.restinga.dev1.nicholas.superloja.controller;

import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.dao.GeneroDAO;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.entidade.Genero;
import br.edu.ifrs.restinga.dev1.nicholas.superloja.modelo.entidade.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class Generos {
    
    @Autowired
    GeneroDAO generoDAO;
    
    @RequestMapping(path = "/generos/", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Genero cadastrar(@RequestBody Genero genero) {
        genero.setId(0);
        return generoDAO.save(genero);
    }

    @RequestMapping(path = "/generos/", method = RequestMethod.GET)
    public Iterable<Genero> listar() {
        return generoDAO.findAll();
    }
    
    
}
