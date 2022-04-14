
# Java e JPA

## Sobre este projeto

Este projeto foi desenvolvido seguindo um curso da plataforma **Alura** sobre Java e JPA ministrado
pelo Rodrigo Ferreira (https://github.com/rcaneppele).

O curso aborda os seguintes tópicos:

- Modelar relacionamentos bidirecionais

- Utilizar o recurso de select new para realizar consultas

- Diferença entre relacionamentos EAGER e LAZY

- Recurso de join fetch para planejar queries

- API de Criteria da JPA

- Mapear entidades que utilizam herança e chave composta

Link para o curso "*Java e JPA: consultas avançadas, performance e modelos complexos*":
https://cursos.alura.com.br/course/java-jpa-consultas-avancadas-performance-modelos-complexos

O foco do projeto é voltado à utilização dos conceitos do JPA, não levando em consideração
a organização do projeto Java. Além disso, não é utilizado nenhum Framework (como Spring),
então as consultas ainda manipulam o EntityManagerFactory manualmente.

## Arquivo pom.xml utilizado

```
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
</properties>

<dependencies>
    <dependency>
        <groupId>org.hibernate</groupId>
        <artifactId>hibernate-entitymanager</artifactId>
        <version>5.4.27.Final</version>
    </dependency>

    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>1.4.200</version>
    </dependency>
</dependencies>
```

## Persistence.xml

Esse arquivo define quais as unidades de persistência que serão utilizadas pela aplicação.
É possível utilizar vários bancos de dados distintos criando unidades de persistências.

> O arquivo persistence.xml deve estar no diretório /src/main/resources/META-INF/persistence.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<persistence version="2.2"
             xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd">

    <persistence-unit name="treinamento" transaction-type="RESOURCE_LOCAL">
        <properties>
            <property name="javax.persistence.jdbc.driver" value="org.h2.Driver"/>
            <property name="javax.persistence.jdbc.url" value="jdbc:h2:mem:treinamento"/>
            <property name="javax.persistence.jdbc.user" value="sa"/>
            <property name="javax.persistence.jdbc.password" value=""/>

            <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.format_sql" value="true"/>
            <property name="hibernate.hbm2ddl.auto" value="update"/>
        </properties>
    </persistence-unit>
</persistence>
```

## EntityManagerFactory e EntityManager

```
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAUtil {

    private static final EntityManagerFactory FACTORY = Persistence
            .createEntityManagerFactory("treinamento");

    public static EntityManager getEntityManager() {
        return FACTORY.createEntityManager();
    }

}
```

## Criando entidades

Utilize a anotação @Entity para mapear uma entidade com JPA. E a anotação @Table para especificar
manualmente o nome da tabela no banco de dados.

> É importante existir o construtor não parametrizado e os métodos get/set dos atributos.

```
@Entity
@Table(name = "produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    ...
}
```

## Criando entidades com chave composta

A chave primária de uma Categoria será uma chave composta do nome e do tipo da categoria.

```
@Entity
public class Categoria {

    @EmbeddedId
    private CategoriaId id;
    ...
}
   
@Embeddable
public class CategoriaId implements Serializable {

    private String nome;
    private String tipo;
    ...
}
```

## Simplificando classes com Embeddable e Embedded

É possível mover atributos de uma entidade para outras classes para falicitar a organização, de modo que
o JPA veja todas essas classes como apenas uma entidade. No exemplo a seguir, a classe DadosPessoais possui atributos que são da classe Cliente.

> Note que DadosPessoais não é uma Entidade.

```
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Embedded
    private DadosPessoais dadosPessoais;
    ...
}

@Embeddable
public class DadosPessoais {

    private String nome;
    private String cpf;
    ...
}
```

## Mapeamento de Herança com @Inheritance

A estratégia ```InheritanceType.SINGLE_TABLE``` faz com que exista apenas uma tabela que possui todos
os atributos das entidades envolvidas. Essa estratégia tem mais performance, no entento, a tabela pode
ficar esparsa (muitas células vazias).

A estratégia ```InheritanceType.JOINED``` cria uma tabela para cada entidade (mãe e filhas).
Onde a chave primária do filho será uma chave extrageira para a chave primária da classe mãe.
Essa estratégia  possibilita maior organização, mas um SELECT em uma classe filha irá fazer um
JOIN para a classe mãe.

```
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    ...
}

@Entity
public class Livro extends Produto {

    private String autor;
    private Integer numeroDePaginas;
    ...
}

@Entity
public class Informatica extends Produto {

    private String marca;
    private Integer modelo;
    ...
}
```

## Relacionamento @OneToMany e @ManyToOne

Por padrão, os atributos @OneToMany e @ManyToMany são LAZY e os atributos @OneToOne e @ManyToOne são EAGER.

É uma boa prática fazer com que os atributos @ManyToOne e @OneToOne também sejam LAZY.
Crie consultas personalizadas (com JOIN FETCH) para momentos que realmente precisam desse atributo.

```
@OneToOne(fetch = FetchType.LAZY)
@ManyToOne(fetch = FetchType.LAZY)
```

### Unidirecional

No exemplo a seguir, uma companhia possui várias filiais que não conhecem a companhia.

```
@Entity
public class Companhia {
  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String nome;
    @OneToMany(targetEntity=Filial.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Filial> filiais = new ArrayList<>();
    ...
}

@Entity
public class Filial {
  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private string nome;
    ...
}
```

No próximo exemplo, uma filial possui uma companhia e uma companhia possui várias filiais.
Mas a companhia não conhece suas filiais. Portanto, a filial é o lado proprietário,
pois possui a associação de chave estrangeira.

```
@Entity
public class Companhia {
  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String nome;
    ...
}
    
@Entity
public class Filial {
  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private string nome;
    
    @ManyToOne(cascade = CascadeType.ALL)
    private Companhia companhia;
    ...
}
```

No exemplo a seguir, um ItemPedido conhece o Produto. Mas o Produto não conhece o ItemPedido.

```
@Entity
public class ItemPedido {
    ...
    @ManyToOne(fetch = FetchType.LAZY)
    private Produto produto;
    ...
}

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    ...
}
```

### Bidirecional

O lado inverso de um relacionamento bidirecional deve se referir ao seu lado proprietário.
O atributo que é @OneToMany deve possuir o **mappedBy**, onde seu valor é o nome do atributo
que possui a anotação @ManyToOne.

> O lado múltiplo dos @ManyToOne relacionamentos bidirecionais não deve definir o mappedBy.
> O lado 'muitos' é sempre o lado proprietário do relacionamento

Para relacionamentos bidirecionais @ManyToMany, qualquer um dos lados pode ser o lado
proprietário.

O valor ```cascade = CascadeType.ALL``` faz com que caso um pedido seja deletado, todos os
seus itens também serão removidos. Neste exemplo, um pedido possui vários itens que conhecem o pedido.

```
@Entity
public class Pedido {
    ...
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<ItemPedido> itens = new ArrayList<>();
    ...
}


@Entity
public class ItemPedido {
    ...
    @ManyToOne(fetch = FetchType.LAZY)
    private Pedido pedido;
    ...
}
```

## JPQL

A Java Persistence Query Language (JPQL) é uma maneira de consultar objetos na JPA,
isso se deve por ela ter sido fortemente inspirada na sintaxe do SQL. Apesar disso,
o foco dela não está relacionado a tabelas e colunas, mas sim a objetos e seus atributos.

### Consultas para buscar produtos

```
public List<Produto> buscarTodos() {
    String jpql = "SELECT p FROM Produto p";
    return em.createQuery(jpql, Produto.class).getResultList();
}
```

```
public List<Produto> buscarPorNome(String nome) {
    String jpql = "SELECT p FROM Produto p WHERE p.nome = ?1";
    return em.createQuery(jpql, Produto.class)
            .setParameter(1, nome)
            .getResultList();
}
```

```
public List<Produto> buscarPorNome(String nome) {
    String jpql = "SELECT p FROM Produto p WHERE p.nome = :nome";
    return em.createQuery(jpql, Produto.class)
            .setParameter("nome", nome)
            .getResultList();
}
```

```
public BigDecimal buscarPrecoDoProdutoComNome(String nome) {
    String jpql = "SELECT p.preco FROM Produto p WHERE p.nome = ?1";
    return em.createQuery(jpql, BigDecimal.class)
            .setParameter(1, nome)
            .getSingleResult();
}
```

```
public List<Produto> buscarPorNomeDaCategoria(String nome) {
    String jpql = "SELECT p FROM Produto p WHERE p.categoria.id.nome = :nome";
    return em.createQuery(jpql, Produto.class)
            .setParameter("nome", nome)
            .getResultList();
}
```

### Utilizando @NamedQuery na classe Modelo

```
@Entity
@NamedQuery(name = "Produto.produtosPorCategoria", query = "SELECT p FROM Produto p WHERE p.categoria.id.nome = :nome")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    ...
}    
    
public List<Produto> buscarPorNomeDaCategoria(String nome) {
    return em.createNamedQuery("Produto.produtosPorCategoria", Produto.class)
            .setParameter("nome", nome)
            .getResultList();
}
```

### Buscar produtos passando parâmetros dinâmicos

```
public List<Produto> buscarPorParametros(String nome, BigDecimal preco, LocalDate dataCadastro) {
    String jpql = "SELECT p FROM Produto p WHERE 1=1 ";
    if (nome != null && !nome.trim().isEmpty()) {
        jpql += " AND p.nome = :nome";
    }
    if (preco != null) {
        jpql += " AND p.preco = :preco";
    }
    if (dataCadastro != null) {
        jpql += " AND p.dataCadastro = :dataCadastro";
    }
    TypedQuery<Produto> query = em.createQuery(jpql, Produto.class);
    if (nome != null && !nome.trim().isEmpty()) {
        query.setParameter("nome", nome);
    }
    if (preco != null) {
        query.setParameter("preco", preco);
    }
    if (dataCadastro != null) {
        query.setParameter("dataCadastro", dataCadastro);
    }
    return query.getResultList();
}
```

### Criteria Query

Criteria Query é uma forma menos verbosa para buscar produtos passando parâmetros dinâmicos,
mas possui uma complexidade de desenvolvimento e manutenção maior.

```
public List<Produto> buscarPorParametrosComCriteria(String nome, BigDecimal preco, LocalDate dataCadastro) {
    CriteriaBuilder builder = em.getCriteriaBuilder();
    CriteriaQuery<Produto> query = builder.createQuery(Produto.class);
    Root<Produto> from = query.from(Produto.class);

    Predicate filtros = builder.and();
    if (nome != null && !nome.trim().isEmpty()) {
        filtros = builder.and(filtros, builder.equal(from.get("nome"), nome));
    }
    if (preco != null) {
        filtros = builder.and(filtros, builder.equal(from.get("preco"), preco));
    }
    if (dataCadastro != null) {
        filtros = builder.and(filtros, builder.equal(from.get("dataCadastro"), dataCadastro));
    }
    query.where(filtros);

    return em.createQuery(query).getResultList();
}
```

### SELECT SUM

No JPQL também podemos chamar as funções de agregação (sum, min, max, AVG) que existem nos bancos de dados.

Consulta para calcular o valor total de todos os pedidos.

```
public BigDecimal valorTotalVendido() {
    String jpql = "SELECT SUM(p.valorTotal) FROM Pedido p";
    return this.em.createQuery(jpql, BigDecimal.class).getSingleResult();
}
```

### SELECT new

Utilize o SELECT new para retornar objetos personalizados, que encapsulam atributos de várias entidades.

> É necessário que a classe auxiliar possua o construtor que vai ser utilizado na consulta.

```
// Não possui @Entity
public class RelatorioDeVendasVo {
    private String nomeProduto;
    private Long quantidadeVendida;
    private LocalDate dataUltimaVenda;
    
    public RelatorioDeVendasVo(String nomeProduto, Long quantidadeVendida, LocalDate dataUltimaVenda) {
        this.nomeProduto = nomeProduto;
        this.quantidadeVendida = quantidadeVendida;
        this.dataUltimaVenda = dataUltimaVenda;
    }
    ...
}

public List<RelatorioDeVendasVo> relatorioDeVendas() {
    String jpql = "SELECT new br.com.alura.treinamento.vo.RelatorioDeVendasVo(produto.nome, SUM(item.quantidade), MAX(pedido.data)) "
            + "FROM Pedido pedido "
            + "JOIN pedido.itens item "
            + "JOIN item.produto produto "
            + "GROUP BY produto.nome "
            + "ORDER BY item.quantidade DESC";
    return em.createQuery(jpql, RelatorioDeVendasVo.class).getResultList();
}
```

### JOIN FETCH

O JOIN FETCH vai trazer o cliente mesmo o atributo cliente da classe Pedido sendo LAZY.

```
@Entity
public class Pedido {
    ...
    @ManyToOne(fetch = FetchType.LAZY)
    private Cliente cliente;
    ...
}

public Pedido buscarPedidoComCliente(Long id) {
    String jpql = "SELECT p FROM Pedido p JOIN FETCH p.cliente WHERE p.id=:id";
    return em.createQuery(jpql, Pedido.class)
            .setParameter("id", id)
            .getSingleResult();
}
```
