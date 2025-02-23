package nl.novi.eindprojectbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @NotNull(message = "Price is required")
    private Double price;

    @NotNull(message = "Stock is required")
    private Integer stock;

    public Part() {}

    public Part(String name, Double price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
