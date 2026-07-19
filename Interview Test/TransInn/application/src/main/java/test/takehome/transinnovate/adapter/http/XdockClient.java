package test.takehome.transinnovate.adapter.http;

import test.takehome.transinnovate.dto.ImportResponse;
import test.takehome.transinnovate.dto.OrderRequest;
import test.takehome.transinnovate.dto.OrderResponse;

public interface XdockClient {

    OrderResponse sendRestOrder(OrderRequest payload);

    OrderResponse sendRestFailingOrder(OrderRequest payload);

    String sendSoapOrder(String soapEnvelope);

    ImportResponse[] getImports();
}
