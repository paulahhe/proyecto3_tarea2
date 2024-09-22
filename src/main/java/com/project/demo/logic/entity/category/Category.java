package com.project.demo.logic.entity.category;
import jakarta.persistence.*;

@Table (name = "category")
@Entity
public class Category {

    @Id
    @GeneratedValue (strategy = GenerationType.AUTO)
    @Column (nullable = false)
    private String id;

    @Column (unique = true, nullable = false)
    private String name;

    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
