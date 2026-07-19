package test.takehome.transinnovate.service;

import test.takehome.transinnovate.constants.ApplicationConstants;
import test.takehome.transinnovate.dto.OrderRequest;

public interface IntegrationService {
    Object processOrder(ApplicationConstants.ChannelType channel, OrderRequest request) throws Exception;
}
