package test.takehome.transinnovate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportResponse {

    @JsonProperty("fileName")
    private String fileName;

    @JsonProperty("order")
    private OrderResponse order;

    @JsonProperty("error")
    private String error;
}
