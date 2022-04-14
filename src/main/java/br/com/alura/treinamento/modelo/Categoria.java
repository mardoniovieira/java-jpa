package br.com.alura.treinamento.modelo;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class Categoria {

    @EmbeddedId
    private CategoriaId id;

    public Categoria() {}

    public Categoria(String nome) {
        this.id = new CategoriaId(nome, "tipo");
    }

    public String getNome() {
        return this.id.getNome();
    }
}
