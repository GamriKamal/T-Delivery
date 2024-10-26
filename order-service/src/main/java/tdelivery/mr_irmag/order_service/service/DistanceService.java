package tdelivery.mr_irmag.order_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.order_service.config.DeliveryConfig;
import tdelivery.mr_irmag.order_service.domain.dto.DeliveryDTO;
import tdelivery.mr_irmag.order_service.domain.dto.DistanceMatrixDTO;

@Service
@RequiredArgsConstructor
@Log4j2
public class DistanceService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DeliveryConfig deliveryConfig;
    @Value("${google.geocode.url}")
    private String googleGeocodeApiUrl;
    @Value("${google.distanceMatrix.url}")
    private String googleDistanceMatrixApiKey;

    public DeliveryDTO calculateDelivery(String address) {
        Point point = getCoordinates(address);
        DistanceMatrixDTO distanceMatrixDTO = getDurationOfDelivery(point);

        double total = deliveryConfig.getBasePrice() +
                (distanceMatrixDTO.getDistance().getValue() * deliveryConfig.getDistancePrice()) +
                (distanceMatrixDTO.getDuration().getValue() * deliveryConfig.getTimePrice());
        total = Math.round(total * 100.0) / 100.0;

        return DeliveryDTO.builder()
                .deliveryPrice(total)
                .deliveryDuration(distanceMatrixDTO.getDuration().getText())
                .build();
    }

    public Point getCoordinates(String address) {
        googleGeocodeApiUrl += address;
        var rawResponse = restTemplate.getForEntity(googleGeocodeApiUrl, String.class).toString();
        String cleanedResponse = rawResponse.replaceAll("<", "").replaceAll(">", "").trim();
        cleanedResponse = cleanedResponse.replace("200 OK OK,", "");

        log.info(cleanedResponse + " yandex");
        try {
            JsonNode rootNode = objectMapper.readTree(cleanedResponse);

            JsonNode locationNode = rootNode.path("results").get(0).path("geometry").path("location");

            double latitude = locationNode.path("lat").asDouble();
            double longitude = locationNode.path("lng").asDouble();

            Point point = new Point(latitude, longitude);

            log.info(point.toString());

            return point;
        } catch (Exception e) {
            log.error("Error getting coordinates", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public DistanceMatrixDTO getDurationOfDelivery(Point point) {
        googleDistanceMatrixApiKey += point.getX() + "," + point.getY();

        var rawResponse = restTemplate.getForEntity(googleDistanceMatrixApiKey, String.class).toString();
        String cleanedResponse = rawResponse.replaceAll("<", "").replaceAll(">", "").trim();
        cleanedResponse = cleanedResponse.replace("200 OK OK,", "");

        try {
            return objectMapper.readTree(cleanedResponse)
                    .path("rows")
                    .get(0)
                    .path("elements")
                    .get(0)
                    .traverse(objectMapper)
                    .readValueAs(DistanceMatrixDTO.class);
        } catch (Exception e) {
            log.error("Error getting coordinates", e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

}
