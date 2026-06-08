package com.example.service;

import com.example.entity.RainfallRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class RainfallService {

    @Transactional
    public void saveRainfallData(RainfallRecord record) {
        record.persist();
    }

    public List<RainfallRecord> getRecords(String location, int limit) {
        if (location != null && !location.isEmpty()) {
            return RainfallRecord.find("location = ?1 order by timestamp desc", location)
                    .page(0, limit)
                    .list();
        }
        return RainfallRecord.findAll()
                .page(0, limit)
                .list();
    }

    public List<RainfallRecord> getLatestRecords() {
        return RainfallRecord.find("order by timestamp desc")
                .page(0, 10)
                .list();
    }

    public List<RainfallRecord> getAlerts() {
        return RainfallRecord.find("hourlyRainfall >= 50 order by timestamp desc")
                .page(0, 50)
                .list();
    }
}
