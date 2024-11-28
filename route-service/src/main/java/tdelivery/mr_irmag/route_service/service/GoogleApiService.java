package tdelivery.mr_irmag.route_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tdelivery.mr_irmag.route_service.domain.dto.calculationDelivery.GoogleDistanceMatrixResponse;
import tdelivery.mr_irmag.route_service.domain.entity.Address;

@Service
@Log4j2
public class GoogleApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.geocode.url}")
    private String googleGeocodeApiUrl;

    @Value("${google.distanceMatrix.url}")
    private String googleDistanceMatrixApiKey;

    @Value("${google.googlemaps.api_key}")
    private String googleApiKey;

    @Autowired
    public GoogleApiService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Point getCoordinates(String address) {
        String newUrl = googleGeocodeApiUrl + address;
        log.info("Google Geocode API URL: {}", newUrl);
        var rawResponse = restTemplate.getForEntity(newUrl, String.class).toString();
        String cleanedResponse = cleanResponse(rawResponse);

        try {
            JsonNode rootNode = objectMapper.readTree(cleanedResponse);
            JsonNode locationNode = rootNode.path("results").get(0).path("geometry").path("location");

            double latitude = locationNode.path("lat").asDouble();
            double longitude = locationNode.path("lng").asDouble();

            Point point = new Point(latitude, longitude);
            log.info(point.toString());

            return point;
        } catch (Exception e) {
            log.error("Error getting coordinates", e);
            throw new RuntimeException("Error getting coordinates", e);
        }
    }

    public GoogleDistanceMatrixResponse getDurationOfDelivery(String url) {
        log.info("Making request: {}", url);
        var rawResponse = restTemplate.getForEntity(url, String.class).toString();
        String cleanedResponse = cleanResponse(rawResponse);

        try {
            return objectMapper.readTree(cleanedResponse)
                    .path("rows")
                    .get(0)
                    .path("elements")
                    .get(0)
                    .traverse(objectMapper)
                    .readValueAs(GoogleDistanceMatrixResponse.class);
        } catch (Exception e) {
            log.error("Error getting delivery duration", e);
            throw new RuntimeException("Error getting delivery duration", e);
        }
    }

    public String buildDistanceMatrixUrl(Point origin, Point destination) {
        return googleDistanceMatrixApiKey +
                "mode=driving&departureTime=now&origins=" + origin.getX() + "," + origin.getY() +
                "&destinations=" + destination.getX() + "," + destination.getY() +
                "&key=" + googleApiKey;
    }

    public static Point toPoint(Address address){
        return new Point(address.getX(), address.getY());
    }

    private String cleanResponse(String rawResponse) {
        return rawResponse.replaceAll("<", "").replaceAll(">", "").trim().replace("200 OK OK,", "");
    }
}

