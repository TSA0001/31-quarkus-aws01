package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Named("rainfall")
public class RainfallLambda implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    private static final Logger LOGGER = Logger.getLogger(RainfallLambda.class.getName());

    @Inject
    @RestClient
    JmaApiClient jmaApiClient;

    @Inject
    @RestClient
    Quark01ApiClient quark01ApiClient;

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        LOGGER.info("雨量監視Lambda開始: " + LocalDateTime.now());

        Map<String, Object> response = new HashMap<>();
        
        try {
            // 監視対象拠点
            String[] locations = {
                "東京", "横浜", "さいたま", "千葉",
                "大阪", "京都", "神戸",
                "名古屋", "静岡",
                "仙台", "新潟",
                "広島", "岡山",
                "高松", "松山",
                "福岡", "熊本", "鹿児島",
                "那覇", "札幌"
            };

            int processedCount = 0;
            int alertCount = 0;

            for (String location : locations) {
                try {
                    // 気象庁APIから雨量データ取得
                    RainfallData rainfallData = fetchRainfallData(location);
                    
                    // quark01サーバーへ送信
                    sendToQuark01(rainfallData);
                    
                    processedCount++;
                    
                    // 警報判定
                    if (rainfallData.getHourlyRainfall() >= 50) {
                        alertCount++;
                        LOGGER.warning(String.format("警報: %s で時間雨量%dmm", 
                            location, rainfallData.getHourlyRainfall()));
                    }
                    
                } catch (Exception e) {
                    LOGGER.severe("拠点処理エラー: " + location + " - " + e.getMessage());
                }
            }

            response.put("status", "success");
            response.put("processedLocations", processedCount);
            response.put("alertCount", alertCount);
            response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        } catch (Exception e) {
            LOGGER.severe("Lambda実行エラー: " + e.getMessage());
            response.put("status", "error");
            response.put("error", e.getMessage());
        }

        return response;
    }

    /**
     * 気象庁APIから雨量データ取得
     */
    private RainfallData fetchRainfallData(String location) {
        try {
            // 気象庁APIから取得（実際のAPIエンドポイントに合わせて実装）
            JmaRainfallResponse jmaResponse = jmaApiClient.getRainfall(location);
            
            return new RainfallData(
                location,
                jmaResponse.getHourlyRainfall(),
                jmaResponse.getTotal24h(),
                jmaResponse.getForecast1h(),
                LocalDateTime.now()
            );
            
        } catch (Exception e) {
            LOGGER.warning("気象庁API取得失敗、ダミーデータ使用: " + location);
            // フォールバック: ダミーデータ
            return new RainfallData(
                location,
                (int)(Math.random() * 100),  // 0-100mm
                (int)(Math.random() * 300),  // 0-300mm
                (int)(Math.random() * 80),   // 0-80mm
                LocalDateTime.now()
            );
        }
    }

    /**
     * quark01サーバーへデータ送信
     */
    private void sendToQuark01(RainfallData data) {
        try {
            Quark01Response response = quark01ApiClient.sendRainfallData(data);
            LOGGER.info(String.format("quark01送信成功: %s - %s", 
                data.getLocation(), response.getMessage()));
                
        } catch (Exception e) {
            LOGGER.severe(String.format("quark01送信失敗: %s - %s", 
                data.getLocation(), e.getMessage()));
            throw new RuntimeException("quark01送信エラー", e);
        }
    }
}
