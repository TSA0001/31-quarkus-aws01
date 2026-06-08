package com.example.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "rainfall_records")
public class RainfallRecord extends PanacheEntity {
    public String location;
    public Integer hourlyRainfall;
    public Integer total24h;
    public Integer forecast1h;
    public LocalDateTime timestamp;
    public LocalDateTime createdAt;

    public RainfallRecord() {
        this.createdAt = LocalDateTime.now();
    }
}
