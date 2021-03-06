package com.sequenceiq.cloudbreak.core.flow2.stack.termination;

import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.domain.Blueprint;
import com.sequenceiq.cloudbreak.domain.Cluster;
import com.sequenceiq.cloudbreak.domain.Credential;
import com.sequenceiq.cloudbreak.domain.HostGroup;
import com.sequenceiq.cloudbreak.domain.InstanceGroup;
import com.sequenceiq.cloudbreak.domain.LdapConfig;
import com.sequenceiq.cloudbreak.domain.Network;
import com.sequenceiq.cloudbreak.domain.Recipe;
import com.sequenceiq.cloudbreak.domain.SecurityGroup;
import com.sequenceiq.cloudbreak.domain.SssdConfig;
import com.sequenceiq.cloudbreak.domain.Stack;
import com.sequenceiq.cloudbreak.domain.Template;
import com.sequenceiq.cloudbreak.repository.BlueprintRepository;
import com.sequenceiq.cloudbreak.repository.HostGroupRepository;
import com.sequenceiq.cloudbreak.repository.LdapConfigRepository;
import com.sequenceiq.cloudbreak.repository.NetworkRepository;
import com.sequenceiq.cloudbreak.repository.RecipeRepository;
import com.sequenceiq.cloudbreak.repository.SecurityGroupRepository;
import com.sequenceiq.cloudbreak.repository.SssdConfigRepository;
import com.sequenceiq.cloudbreak.repository.TemplateRepository;
import com.sequenceiq.cloudbreak.service.credential.CredentialService;

@Service
public class DependecyDeletionService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DependecyDeletionService.class);

    @Inject
    private NetworkRepository networkRepository;

    @Inject
    private CredentialService credentialService;

    @Inject
    private BlueprintRepository blueprintRepository;

    @Inject
    private SssdConfigRepository sssdConfigRepository;

    @Inject
    private LdapConfigRepository ldapConfigRepository;

    @Inject
    private RecipeRepository recipeRepository;

    @Inject
    private SecurityGroupRepository securityGroupRepository;

    @Inject
    private TemplateRepository templateRepository;

    @Inject
    private HostGroupRepository hostGroupRepository;

    public void deleteDependencies(Stack stack) {
        deleteNetwork(stack.getNetwork());
        deleteCredential(stack.getCredential());

        for (InstanceGroup instanceGroup : stack.getInstanceGroups()) {
            deleteSecurityGroup(instanceGroup.getSecurityGroup());
            deleteTemplate(instanceGroup.getTemplate());
        }
        if (stack.getCluster() != null) {
            Cluster cluster = stack.getCluster();
            deleteBlueprint(cluster.getBlueprint());
            deleteSssd(cluster.getSssdConfig());
            deleteLdap(cluster.getLdapConfig());
            Set<HostGroup> hostGroupsInCluster = hostGroupRepository.findHostGroupsInCluster(cluster.getId());
            for (HostGroup hostGroup : hostGroupsInCluster) {
                hostGroup.getRecipes().forEach(this::deleteRecipe);
            }
        }
    }

    private void deleteNetwork(Network network) {
        try {
            if (network != null) {
                networkRepository.delete(network);
            }
        } catch (Exception ex) {
            LOGGER.warn("Could not delete network {} which is associated with the stack: {}", network, ex.getMessage());
        }
    }

    private void deleteCredential(Credential credential) {
        try {
            if (credential != null) {
                credentialService.delete(credential);
            }
        } catch (Exception ex) {
            LOGGER.warn("Could not delete credential {} which is associated with the stack: {}", credential, ex.getMessage());
        }
    }

    private void deleteSecurityGroup(SecurityGroup securityGroup) {
        try {
            if (securityGroup != null) {
                securityGroupRepository.delete(securityGroup);
            }
        } catch (Exception ex) {
            LOGGER.warn("Could not delete securityGroup {} which is associated with the stack: {}", securityGroup, ex.getMessage());
        }
    }

    private void deleteTemplate(Template template) {
        try {
            if (template != null) {
                templateRepository.delete(template);
            }
        } catch (Exception ex) {
            LOGGER.warn("Could not delete template {} which is associated with the stack: {}", template, ex.getMessage());
        }
    }

    private void deleteBlueprint(Blueprint blueprint) {
        try {
            if (blueprint != null) {
                blueprintRepository.delete(blueprint);
            }
        } catch (Exception ex) {
            LOGGER.warn("Could not delete blueprint {} which is associated with the stack: {}", blueprint, ex.getMessage());
        }
    }

    private void deleteSssd(SssdConfig sssdConfig) {
        try {
            if (sssdConfig != null) {
                sssdConfigRepository.delete(sssdConfig);
            }
        } catch (Exception ex) {
            LOGGER.warn("Could not delete sssdConfig {} which is associated with the stack: {}", sssdConfig, ex.getMessage());
        }
    }

    private void deleteLdap(LdapConfig ldapConfig) {
        try {
            if (ldapConfig != null) {
                ldapConfigRepository.delete(ldapConfig);
            }
        } catch (Exception ex) {
            LOGGER.warn("Could not delete ldapConfig {} which is associated with the stack: {}", ldapConfig, ex.getMessage());
        }
    }

    private void deleteRecipe(Recipe recipe) {
        try {
            if (recipe != null) {
                recipeRepository.delete(recipe);
            }
        } catch (Exception ex) {
            LOGGER.warn("Could not delete recipe {} which is associated with the stack: {}", recipe, ex.getMessage());
        }
    }
}
