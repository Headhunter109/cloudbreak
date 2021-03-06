package com.sequenceiq.cloudbreak.service.decorator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import com.sequenceiq.cloudbreak.api.model.BlueprintRequest;
import com.sequenceiq.cloudbreak.api.model.HostGroupRequest;
import com.sequenceiq.cloudbreak.api.model.LdapConfigRequest;
import com.sequenceiq.cloudbreak.api.model.RDSConfigJson;
import com.sequenceiq.cloudbreak.api.model.SssdConfigRequest;
import com.sequenceiq.cloudbreak.controller.BadRequestException;
import com.sequenceiq.cloudbreak.controller.validation.blueprint.BlueprintValidator;
import com.sequenceiq.cloudbreak.controller.validation.ldapconfig.LdapConfigValidator;
import com.sequenceiq.cloudbreak.domain.Blueprint;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.domain.Cluster;
import com.sequenceiq.cloudbreak.domain.HostGroup;
import com.sequenceiq.cloudbreak.domain.LdapConfig;
import com.sequenceiq.cloudbreak.domain.RDSConfig;
import com.sequenceiq.cloudbreak.domain.SssdConfig;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.service.blueprint.BlueprintService;
import com.sequenceiq.cloudbreak.service.ldapconfig.LdapConfigService;
import com.sequenceiq.cloudbreak.service.rdsconfig.RdsConfigService;
import com.sequenceiq.cloudbreak.service.sssdconfig.SssdConfigService;
import com.sequenceiq.cloudbreak.service.stack.StackService;

@Component
public class ClusterDecorator implements Decorator<Cluster> {

    private enum DecorationData {
        STACK_ID,
        USER,
        BLUEPRINT_ID,
        HOSTGROUP_JSONS,
        VALIDATE_BLUEPRINT,
        SSSDCONFIG_ID,
        RDSCONFIG_ID,
        LDAP_CONFIG_ID,
        BLUEPRINT,
        SSSDCONFIG,
        RDSCONFIG,
        LDAP_CONFIG;
    }

    @Inject
    private BlueprintService blueprintService;

    @Inject
    private BlueprintValidator blueprintValidator;

    @Inject
    private StackService stackService;

    @Inject
    private ConversionService conversionService;

    @Inject
    private HostGroupDecorator hostGroupDecorator;

    @Inject
    private SssdConfigService sssdConfigService;

    @Inject
    private RdsConfigService rdsConfigService;

    @Inject
    private LdapConfigService ldapConfigService;

    @Inject
    private LdapConfigValidator ldapConfigValidator;

    @Override
    public Cluster decorate(Cluster subject, Object... data) {
        if (null == data || data.length != DecorationData.values().length) {
            throw new IllegalArgumentException("Invalid decoration data provided. Cluster: " + subject.getName());
        }
        Long stackId = (Long) data[DecorationData.STACK_ID.ordinal()];
        CbUser user = (CbUser) data[DecorationData.USER.ordinal()];
        Long blueprintId = (Long) data[DecorationData.BLUEPRINT_ID.ordinal()];
        Long ldapConfigId = (Long) data[DecorationData.LDAP_CONFIG_ID.ordinal()];
        Set<HostGroupRequest> hostGroupsJsons = (Set<HostGroupRequest>) data[DecorationData.HOSTGROUP_JSONS.ordinal()];
        BlueprintRequest requestBlueprint = (BlueprintRequest) data[DecorationData.BLUEPRINT.ordinal()];
        SssdConfigRequest requestSssd = (SssdConfigRequest) data[DecorationData.SSSDCONFIG.ordinal()];
        Set<RDSConfigJson> requestRdsConfigs = (Set<RDSConfigJson>) data[DecorationData.RDSCONFIG.ordinal()];
        LdapConfigRequest ldapConfigRequest = (LdapConfigRequest) data[DecorationData.LDAP_CONFIG.ordinal()];
        Set<Long> rdsConfigIds = (Set<Long>) data[DecorationData.RDSCONFIG_ID.ordinal()];
        Long sssdConfigId = (Long) data[DecorationData.SSSDCONFIG_ID.ordinal()];

        Stack stack = stackService.getById(stackId);

        if (blueprintId != null) {
            subject.setBlueprint(blueprintService.get(blueprintId));
        } else if (requestBlueprint != null) {
            Blueprint blueprint = conversionService.convert(requestBlueprint, Blueprint.class);
            blueprint.setPublicInAccount(stack.isPublicInAccount());
            blueprint = blueprintService.create(user, blueprint, new ArrayList<>());
            subject.setBlueprint(blueprint);
        } else {
            throw new BadRequestException("Blueprint does not configured for the cluster!");
        }
        subject.setHostGroups(convertHostGroupsFromJson(stack, user, subject, hostGroupsJsons));
        boolean validate = (boolean) data[DecorationData.VALIDATE_BLUEPRINT.ordinal()];
        if (validate) {
            Blueprint blueprint = null;
            if (blueprintId != null) {
                blueprint = blueprintService.get(blueprintId);
            } else if (requestBlueprint != null) {
                blueprint = subject.getBlueprint();
            }
            blueprintValidator.validateBlueprintForStack(blueprint, subject.getHostGroups(), stack.getInstanceGroups());
        }
        subject.setTopologyValidation(validate);
        prepareSssd(subject, user, sssdConfigId, requestSssd, stack);
        prepareRds(subject, user, rdsConfigIds, requestRdsConfigs, stack);
        prepareLdap(subject, user, ldapConfigId, ldapConfigRequest, stack);
        return subject;
    }

    private void prepareSssd(Cluster subject, CbUser user, Long sssdConfigId, SssdConfigRequest requestSssd, Stack stack) {
        if (sssdConfigId != null) {
            SssdConfig config = sssdConfigService.get(sssdConfigId);
            subject.setSssdConfig(config);
        } else if (requestSssd != null) {
            SssdConfig sssdConfig = conversionService.convert(requestSssd, SssdConfig.class);
            sssdConfig.setPublicInAccount(stack.isPublicInAccount());
            sssdConfig = sssdConfigService.create(user, sssdConfig);
            subject.setSssdConfig(sssdConfig);
        }
    }

    private void prepareLdap(Cluster subject, CbUser user, Long ldapConfigId, LdapConfigRequest ldapConfigRequest, Stack stack) {
        if (ldapConfigId != null) {
            LdapConfig ldapConfig = ldapConfigService.get(ldapConfigId);
            subject.setLdapConfig(ldapConfig);
        } else if (ldapConfigRequest != null) {
            LdapConfig ldapConfig = conversionService.convert(ldapConfigRequest, LdapConfig.class);
            ldapConfig.setPublicInAccount(stack.isPublicInAccount());
            ldapConfigValidator.validateLdapConnection(ldapConfig);
            ldapConfig = ldapConfigService.create(user, ldapConfig);
            subject.setLdapConfig(ldapConfig);
        }
    }

    private void prepareRds(Cluster subject, CbUser user, Set<Long> rdsConfigIds, Set<RDSConfigJson> requestRdsConfigs, Stack stack) {
        subject.setRdsConfigs(new HashSet<>());
        if (rdsConfigIds != null && !rdsConfigIds.isEmpty()) {
            for (Long rdsConfigId : rdsConfigIds) {
                RDSConfig rdsConfig = rdsConfigService.get(rdsConfigId);
                subject.getRdsConfigs().add(rdsConfig);
            }
        } else if (requestRdsConfigs != null && !requestRdsConfigs.isEmpty()) {
            for (RDSConfigJson requestRdsConfig : requestRdsConfigs) {
                RDSConfig rdsConfig = conversionService.convert(requestRdsConfig, RDSConfig.class);
                rdsConfig.setPublicInAccount(stack.isPublicInAccount());
                rdsConfig = rdsConfigService.create(user, rdsConfig);
                subject.getRdsConfigs().add(rdsConfig);
            }
        }
    }

    private Set<HostGroup> convertHostGroupsFromJson(Stack stack, CbUser user, Cluster cluster, Set<HostGroupRequest> hostGroupsJsons) {
        Set<HostGroup> hostGroups = new HashSet<>();
        for (HostGroupRequest json : hostGroupsJsons) {
            HostGroup hostGroup = conversionService.convert(json, HostGroup.class);
            hostGroup.setCluster(cluster);
            hostGroup = hostGroupDecorator.decorate(hostGroup, stack.getId(), user, json.getConstraint(), json.getRecipeIds(),
                    true, json.getRecipes(), stack.isPublicInAccount());
            hostGroups.add(hostGroup);
        }
        return hostGroups;
    }

}
