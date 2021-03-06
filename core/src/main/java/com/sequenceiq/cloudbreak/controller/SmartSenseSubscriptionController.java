package com.sequenceiq.cloudbreak.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.sequenceiq.cloudbreak.api.endpoint.SmartSenseSubscriptionEndpoint;
import com.sequenceiq.cloudbreak.api.model.SmartSenseSubscriptionJson;
import com.sequenceiq.cloudbreak.converter.JsonToSmartSenseSubscriptionConverter;
import com.sequenceiq.cloudbreak.converter.SmartSenseSubscriptionToJsonConverter;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.domain.SmartSenseSubscription;
import com.sequenceiq.cloudbreak.service.smartsense.SmartSenseSubscriptionService;

@Component
public class SmartSenseSubscriptionController implements SmartSenseSubscriptionEndpoint {

    @Inject
    private SmartSenseSubscriptionService smartSenseSubService;

    @Inject
    private AuthenticatedUserService authenticatedUserService;

    @Inject
    private SmartSenseSubscriptionToJsonConverter toJsonConverter;

    @Inject
    private JsonToSmartSenseSubscriptionConverter toSmartSenseSubscriptionConverter;

    @Override
    public SmartSenseSubscriptionJson get(Long id) {
        SmartSenseSubscription subscription = smartSenseSubService.findOneById(id);
        return toJsonConverter.convert(subscription);
    }

    @Override
    public void delete(Long id) {
        CbUser cbUser = authenticatedUserService.getCbUser();
        smartSenseSubService.delete(id, cbUser);
    }

    @Override
    public SmartSenseSubscriptionJson postPublic(SmartSenseSubscriptionJson smartSenseSubscriptionJson) {
        return createSmartSenseSubscription(smartSenseSubscriptionJson, true);
    }

    @Override
    public List<SmartSenseSubscriptionJson> getPublics() {
        List<SmartSenseSubscription> result = Lists.newArrayList();
        smartSenseSubService.getOne().ifPresent(result::add);
        return toJsonConverter.convert(result);
    }

    @Override
    public SmartSenseSubscriptionJson postPrivate(SmartSenseSubscriptionJson smartSenseSubscriptionJson) {
        return createSmartSenseSubscription(smartSenseSubscriptionJson, false);
    }

    @Override
    public List<SmartSenseSubscriptionJson> getPrivates() {
        return getPublics();
    }

    private SmartSenseSubscriptionJson createSmartSenseSubscription(SmartSenseSubscriptionJson json, boolean publicInAccount) {
        CbUser cbUser = authenticatedUserService.getCbUser();
        SmartSenseSubscription subscription = toSmartSenseSubscriptionConverter.convert(json);
        subscription.setAccount(cbUser.getAccount());
        subscription.setOwner(cbUser.getUserId());
        subscription.setPublicInAccount(publicInAccount);
        subscription = smartSenseSubService.create(subscription);
        return toJsonConverter.convert(subscription);
    }
}
