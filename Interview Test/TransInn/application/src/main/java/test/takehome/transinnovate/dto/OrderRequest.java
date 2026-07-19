package test.takehome.transinnovate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JacksonXmlRootElement(localName = "order")
public class OrderRequest {

    @JsonProperty("consignor")
    private String consignor;

    @JsonProperty("consignee")
    private String consignee;

    @JsonProperty("invoiceTo")
    private String invoiceTo;

    @JsonProperty("notifyParty")
    private String notifyParty;

    @JsonProperty("collectionDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate collectionDate;

    @JsonProperty("collectionAddress")
    private String collectionAddress;

    @JsonProperty("deliveryDate")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    @JsonProperty("deliveryAddress")
    private String deliveryAddress;

    @JsonProperty("palletType")
    private String palletType;

    @JsonProperty("quantity")
    private Double quantity;

    @JsonProperty("weight")
    private Double weight;

    @JsonProperty("loadMeters")
    private Double loadMeters;
}
