package com.example.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.time.LocalDateTime;

@Entity
@Table(name = "river_levels")
public class RiverLevel extends PanacheEntity {

    @Column(nullable = false)
    public String riverName;

    @Column(nullable = false)
    public String location;

    @Column(nullable = false)
    public Double currentLevel;  // 現在水位(m)

    @Column(nullable = false)
    public Double warningLevel;  // 警戒水位(m)

    @Column(nullable = false)
    public Double dangerLevel;   // 危険水位(m)

    @Column(nullable = false)
    public LocalDateTime timestamp;

    @Column(nullable = false, updatable = false)
    public LocalDateTime createdAt = LocalDateTime.now();
}
