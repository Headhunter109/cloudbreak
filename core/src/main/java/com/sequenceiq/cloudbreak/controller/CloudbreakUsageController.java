package com.sequenceiq.cloudbreak.controller;

import static java.time.ZoneId.systemDefault;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.endpoint.UsageEndpoint;
import com.sequenceiq.cloudbreak.api.model.CloudbreakFlexUsageJson;
import com.sequenceiq.cloudbreak.api.model.CloudbreakUsageJson;
import com.sequenceiq.cloudbreak.domain.CbUsageFilterParameters;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.facade.CloudbreakUsagesFacade;
import com.sequenceiq.cloudbreak.logger.MDCBuilder;

@Component
public class CloudbreakUsageController implements UsageEndpoint {

    @Autowired
    private CloudbreakUsagesFacade cloudbreakUsagesFacade;

    @Autowired
    private AuthenticatedUserService authenticatedUserService;

    @Override
    public List<CloudbreakUsageJson> getDeployer(
            Long since,
            Long filterEndDate,
            String userId,
            String accountId,
            String cloud,
            String zone) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        CbUsageFilterParameters params = new CbUsageFilterParameters.Builder().setAccount(accountId).setOwner(userId)
                .setSince(since).setCloud(cloud).setRegion(zone).setFilterEndDate(filterEndDate).build();
        return cloudbreakUsagesFacade.getUsagesFor(params);
    }

    public List<CloudbreakUsageJson> getAccount(
            Long since,
            Long filterEndDate,
            String userId,
            String cloud,
            String zone) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        CbUsageFilterParameters params = new CbUsageFilterParameters.Builder().setAccount(user.getAccount()).setOwner(userId)
                .setSince(since).setCloud(cloud).setRegion(zone).setFilterEndDate(filterEndDate).build();
        return cloudbreakUsagesFacade.getUsagesFor(params);
    }

    @Override
    public List<CloudbreakUsageJson> getUser(
            Long since,
            Long filterEndDate,
            String cloud,
            String zone) {
        CbUser user = authenticatedUserService.getCbUser();
        MDCBuilder.buildUserMdcContext(user);
        CbUsageFilterParameters params = new CbUsageFilterParameters.Builder().setAccount(user.getAccount()).setOwner(user.getUserId())
                .setSince(since).setCloud(cloud).setRegion(zone).setFilterEndDate(filterEndDate).build();
        return cloudbreakUsagesFacade.getUsagesFor(params);
    }

    @Override
    public List<CloudbreakFlexUsageJson> getDailyFlexUsages() {
        long fromDate = LocalDate.now()
                .minusDays(1)
                .atStartOfDay(systemDefault())
                .toInstant()
                .toEpochMilli();

        long endDate = LocalDate.now()
                .atStartOfDay(systemDefault())
                .toInstant()
                .toEpochMilli();

        CbUsageFilterParameters cbUsageFilterParameters = new CbUsageFilterParameters.Builder()
                .setSince(fromDate)
                .setFilterEndDate(endDate)
                .build();
        return cloudbreakUsagesFacade.getFlexUsagesFor(cbUsageFilterParameters);
    }

    @Override
    public List<CloudbreakFlexUsageJson> getLatestFlexUsages() {
        long fromDate = LocalDate.now()
                .atStartOfDay(systemDefault())
                .toInstant()
                .toEpochMilli();

        CbUsageFilterParameters cbUsageFilterParameters = new CbUsageFilterParameters.Builder()
                .setSince(fromDate)
                .build();
        return cloudbreakUsagesFacade.getFlexUsagesFor(cbUsageFilterParameters);
    }

}
