package test.takehome.transinnovate.dataSeed;

import test.takehome.transinnovate.dto.ImportResponse;
import test.takehome.transinnovate.dto.OrderRequest;
import test.takehome.transinnovate.dto.OrderResponse;

import java.time.LocalDate;

public class OrderDataSeed {
    public static OrderRequest mockOrderRequest() {
        return OrderRequest.builder()
            .consignor("ACME")
            .consignee("Globex")
            .invoiceTo("ACME")
            .notifyParty("Globex")
            .collectionDate(LocalDate.of(2026, 6, 10))
            .collectionAddress("Rotterdam")
            .deliveryDate(LocalDate.of(2026, 6, 12))
            .deliveryAddress("Berlin")
            .palletType("EUR")
            .quantity(4.0)
            .weight(1200.0)
            .loadMeters(2.4)
            .build();
    }

    public static OrderResponse mockOrderResponse() {
        return OrderResponse.builder()
            .id("70211a66-d84e-4495-9c69-ca9ba2ee3b96")
            .consignor("ACME")
            .consignee("Globex")
            .invoiceTo("ACME")
            .notifyParty("Globex")
            .collectionDate(LocalDate.of(2026, 6, 10))
            .collectionAddress("Rotterdam")
            .deliveryDate(LocalDate.of(2026, 6, 12))
            .deliveryAddress("Berlin")
            .palletType("EUR")
            .quantity(4.0)
            .weight(1200.0)
            .loadMeters(2.4)
            .build();
    }

    public static ImportResponse mockImportResponse() {
        return ImportResponse.builder()
            .fileName("order_2026-06-10.xml")
            .order(mockOrderResponse())
            .error(null)
            .build();
    }
}
