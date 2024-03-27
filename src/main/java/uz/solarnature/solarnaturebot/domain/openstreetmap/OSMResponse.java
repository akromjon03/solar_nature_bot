package uz.solarnature.solarnaturebot.domain.openstreetmap;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OSMResponse (
        @JsonProperty("place_id") String placeId,
        String lat,
        String lon,
        String type,
        @JsonProperty("address_type") String addresstype,
        @JsonProperty("display_name") String displayName
) {}
