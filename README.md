## 🚨 **Problema N+1 Consultas**

### **Causa: Busca Paginada com Classe Associada**

**Exemplo:**
- Busca paginada de produtos com 'requestParam:' `size=3`.
- Retorna os 3 produtos com as categorias associadas.
- O Hibernate faz:
  - **1º select** para buscar os produtos.
  - **3 select**s adicionais para buscar as categorias associadas.

**Entidades:**
- `Product` e `Category` com relacionamento `@ManyToMany`.

**Problema:**  
- Múltiplas consultas no banco de dados.
- Obs: a cláusula `JOIN FETCH` do JPQL funciona com `Page` apenas se adicionar a propriedade `countQuery `.

### **Consulta com SQL**

- **Problema com `LIMIT`:**
  - O `LIMIT` do SQL não funciona como esperado, pois ele busca as primeiras 5 linhas, podendo causar repetição de dados.

  ```sql
  SELECT * FROM tb_product
  	INNER JOIN tb_product_category ON tb_product.id = tb_product_category.product_id
  	INNER JOIN tb_category ON tb_category.id = tb_product_category.category_id
  LIMIT 0,5
  ```

- **Consulta buscando apenas os 5 primeiros IDs:**

  ```sql
  SELECT * FROM tb_product
  	INNER JOIN tb_product_category ON tb_product.id = tb_product_category.product_id
  	INNER JOIN tb_category ON tb_category.id = tb_product_category.category_id
  WHERE tb_product.id IN (1,2,3,4,5)
  ```

### **Consulta com JPQL**

#### **Repository**

```java
@Query("SELECT obj FROM Product obj JOIN FETCH obj.categories WHERE obj IN :products")
List<Product> findProductsCategories(List<Product> products);
```

#### **Alternativa para Page**

```java
@Query(value = "SELECT obj FROM Product obj JOIN FETCH obj.categories",
       countQuery = "SELECT count(obj) FROM Product obj JOIN obj.categories")
public Page<Product> searchAll();
```

#### **Service**

```java
public Page<ProductDTO> find(Pageable pageable) {
  // Realizando a consulta paginada com JPA
  Page<Product> page = repository.findAll(pageable);

  // Convertendo a Page em List para consulta customizada
  repository.findProductsCategories(page.stream().toList());

  return page.map(x -> new ProductDTO(x));
}
```

#### **Conclusão:**
- **Apenas duas consultas no banco**:
  1. **Primeira**: Consulta paginada dos produtos.
  2. **Segunda**: `JOIN` reaproveitando o cache das categorias, sem precisar buscar novamente as categorias.

---

## 🔄 **Problema @ManyToOne**

### **Causa: Busca de Relacionamento de Muitos para Um**

**Exemplo:**  
- Relacionamento `@ManyToOne` entre `Funcionario` e `Departamento`.
- Com 10 funcionários e 5 departamentos, o JPA faz:
  - **1 consulta** para buscar todos os funcionários.
  - **Várias consultas** para buscar os departamentos e associá-los a cada funcionário.

### **Solução com JPQL**

**Problema:**  
A cláusula `JOIN FETCH` do JPQL não funciona bem com `Page`.

**Solução:**  
Usar `countQuery` para garantir que o JPA saiba quantos elementos buscar.

```java
@Query("SELECT obj FROM Funcionario obj JOIN FETCH obj.departamento",
       countQuery = "SELECT COUNT(obj) FROM Funcionario obj JOIN obj.departamento")
Page<Funcionario> searchAll(Pageable pageable);
```

---

## 🔍 **Problema com Query JPQL customizada buscando uma lista como parâmetro**

### **Service**

```java
@Transactional(readOnly = true)
public Page<ProductProjection> searchAll(String name, String categoryId, Pageable pageable) {
  List<Long> categoryList = List.of();
  if (!categoryId.equals("0")) {
    // Populando a lista com os ids dos parâmetros
    categoryList = Arrays.stream(categoryId.split(",")).map(Long::parseLong).toList();
    // Ignorando a condição jpql WHERE (:categoryId = '0')
    categoryId = "9999";
  }
  return productRepository.searchAll(name, categoryList, categoryId, pageable);
}
```

### **Repository**

* Obs: o JPQL não aceita EMPTY e NULL para lista, portanto foi reaproveitado o parâmetro que veio do client para testar a condicional

```java
@Query(value = """
       SELECT obj FROM Product obj JOIN FETCH obj.categories c
       WHERE (:categoryId = '0' OR c.id IN :categoryList)
       AND (LOWER(obj.name) LIKE LOWER(CONCAT('%', :name, '%')))
       ORDER BY obj.name""",
    countQuery = """
       SELECT COUNT(obj) FROM Product obj JOIN obj.categories c
       WHERE (:categoryId = '0' OR c.id IN :categoryList)
       AND (LOWER(obj.name) LIKE LOWER(CONCAT('%', :name, '%')))""")
Page<ProductProjection> searchAll(String name, List<Long> categoryList, String categoryId,
                                  Pageable pageable);
```

---

## 🛠️ **Gerar SQL e Realizar o Seed**

### **Configuração no `application.properties`**

* Adicione as seguintes propriedades para gerar o arquivo SQL de criação do banco e aplicar o seed.
* Após a execução, comente ou apague essas linhas:

```properties
# spring.jpa.properties.jakarta.persistence.schema-generation.create-source=metadata
# spring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create
# spring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=create.sql
# spring.jpa.properties.hibernate.hbm2ddl.delimiter=;
```

---

