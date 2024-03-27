package uz.solarnature.solarnaturebot.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uz.solarnature.solarnaturebot.config.properties.ApplicationProperties;
import uz.solarnature.solarnaturebot.domain.openstreetmap.OSMResponse;

import java.util.HashMap;
import java.util.Map;

@Component
public class AddressUtil {

    private final RestTemplate restTemplate;
    private final String URL;

    public AddressUtil(ApplicationProperties properties) {
        this.restTemplate = new RestTemplate();
        this.URL = properties.getOsm().getUrl();
    }

    public String getByCoordinates(Double latitude, Double longitude) {
        var params = getParams(latitude, longitude);
        var response = restTemplate.getForEntity(URL, OSMResponse.class, params);
        if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
            return response.getBody().displayName();
        } else {
            return "(%.10f, %.10f)".formatted(latitude, longitude);
        }
    }

    private Map<String, String> getParams(Double lat, Double lon) {
        var params = new HashMap<String, String>();
        params.put("format", "json");
        params.put("lat", lat.toString());
        params.put("lon", lon.toString());
        return params;
    }
}
