## Referência para problema N+1 consultas

- Problema causado com busca paginada (`Pageable`) com uma classe associada

* Exemplo:
  - Busca paginada de um produto com size=3
  - Retorna os 3 produtos com as categorias associadas
  - O Hibernate faz o "select" 1 vez para buscar o produto
  - Porém a busca do Hibernate faz o "select" 3 vezes para buscar as categorias associadas

- No exemplo com as entidades `Product` e `Category` em `@ManyToMany`

* A cláusula "JOIN FETCH" do JPQL não funciona com `Page`

---

#### Como ficaria a consulta com SQL

- O `LIMIT` do SQL não funcionaria
  - Pois o SQL busca as primeiras 5 linhas e não os 5 primeiros produtos
  - Portanto pode haver repetição de produto
    - Product / Category : ID:1 Computador / ID:1 Computer
    - Product / Category : ID:1 Computador / ID:2 Electronics

```
SELECT * FROM tb_product
	INNER JOIN tb_product_category ON tb_product.id = tb_product_category.product_id
	INNER JOIN tb_category ON tb_category.id = tb_product_category.category_id
	LIMIT 0,5
```

- Buscando apenas os 5 primeiros id

```
SELECT * FROM tb_product
	INNER JOIN tb_product_category ON tb_product.id = tb_product_category.product_id
	INNER JOIN tb_category ON tb_category.id = tb_product_category.category_id
	WHERE tb_product.id IN (1,2,3,4,5)
```

---

#### Como ficaria a consulta com JPQL

- Repository

```
@Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj IN :products")

List<Product> findProductsCategories(List<Product> products)

```
Alternativa:

```
@Query(value = "SELECT obj FROM Product obj JOIN FETCH obj.categories",
	countQuery = "SELECT count(obj) FROM Product obj JOIN obj.categories")
public List<Product> searchAll();
```

- Service

```
public Page<ProductDTO> find(Pageable pageable) {

  // Fazendo a consulta paginada com JPA
  // Mantendo cache em memória das categories
  Page<Product> page = repository.findAll(pageable);

  // Convertendo a page em list para consulta customizada
  repository.findProductsCategories(page.stream().toList());

  return page.map(x -> new ProductDTO(x));
}
```

- Conclusão:
  - Haverá apenas 2 consultas no banco
  - A primeira para busca paginada do produto
  - A segunda será o JOIN reaproveitando o cache das categorias com a busca no repository da 'List', sem a necessidade de buscar novamente cada categoria
 
---

## Referência para problema @ManyToOne

- Problema causado por uma busca de muitos para um

- Exemplo `@ManyToOne Funcionario` e `@OneToMany Departamento`
  - Um seed com 10 funcionários e 5 departamentos
  - Ao realizar a busca da para retornar uma `List` com `repository.findAll()`
  - O JPA faz 1 busca para todos funcionários
  - E várias buscas de departamentos para associar o funcionário

---

#### Solução em JPQL

- A cláusula "JOIN FETCH" do JPQL não funciona com `Page`
- A solução para esse caso seria `countQuery`
  - Dessa maneira o JPA sabe quantos elementos buscar

```
@Query("SELECT obj FROM Funcionario obj JOIN FETCH obj.departamento,
    countQuery = "SELECT COUNT(obj) FROM Funcionario obj JOIN obj.department)

Page<Funcionario> searchAll(Pageable pageable);

```

---
## Referência para criar o SQL da aplicação e fazer o seed


* application.properties
* criar o arquivo 'create.sql' na raiz do projeto
* o comando abaixo busca o seed no arquivo 'import.sql'
* após aplicar comentar/apagar as linhas

```
#spring.jpa.properties.jakarta.persistence.schema-generation.create-source=metadata
#spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create
#spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=create.sql
#spring.jpa.properties.hibernate.hbm2ddl.delimiter=;
```

