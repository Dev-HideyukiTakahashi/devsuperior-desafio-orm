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
  - A primeira para busca paginada
  - A segunda será o JOIN reaproveitando o cache das categorias, sem a necessidade de buscar novamente cada categoria
