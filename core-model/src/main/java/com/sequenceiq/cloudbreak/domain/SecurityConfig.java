package com.sequenceiq.cloudbreak.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "SecurityConfig")
public class SecurityConfig implements ProvisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "securityconfig_generator")
    @SequenceGenerator(name = "securityconfig_generator", sequenceName = "securityconfig_id_seq", allocationSize = 1)
    private Long id;

    @Type(type = "encrypted_string")
    @Column(columnDefinition = "TEXT")
    private String clientKey;

    @Type(type = "encrypted_string")
    @Column(columnDefinition = "TEXT")
    private String clientCert;

    @Column(columnDefinition = "TEXT")
    private String cloudbreakSshPublicKey;

    @Type(type = "encrypted_string")
    @Column(columnDefinition = "TEXT")
    private String cloudbreakSshPrivateKey;

    @Column(columnDefinition = "TEXT")
    private String saltSignPublicKey;

    @Type(type = "encrypted_string")
    @Column(columnDefinition = "TEXT")
    private String saltSignPrivateKey;

    @Type(type = "encrypted_string")
    private String saltPassword;

    @Type(type = "encrypted_string")
    private String saltBootPassword;

    @Type(type = "encrypted_string")
    private String knoxMasterSecret;

    @OneToOne(fetch = FetchType.LAZY)
    private Stack stack;

    @Column(nullable = false)
    private boolean usePrivateIpToTls;

    public SecurityConfig() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getClientCert() {
        return clientCert;
    }

    public void setClientCert(String clientCert) {
        this.clientCert = clientCert;
    }

    public String getCloudbreakSshPublicKey() {
        return cloudbreakSshPublicKey;
    }

    public void setCloudbreakSshPublicKey(String cloudbreakSshPublicKey) {
        this.cloudbreakSshPublicKey = cloudbreakSshPublicKey;
    }

    public String getCloudbreakSshPrivateKey() {
        return cloudbreakSshPrivateKey;
    }

    public void setCloudbreakSshPrivateKey(String cloudbreakSshPrivateKey) {
        this.cloudbreakSshPrivateKey = cloudbreakSshPrivateKey;
    }

    public String getSaltPassword() {
        return saltPassword;
    }

    public void setSaltPassword(String saltPassword) {
        this.saltPassword = saltPassword;
    }

    public String getSaltBootPassword() {
        return saltBootPassword;
    }

    public void setSaltBootPassword(String saltBootPassword) {
        this.saltBootPassword = saltBootPassword;
    }

    public String getKnoxMasterSecret() {
        return knoxMasterSecret;
    }

    public void setKnoxMasterSecret(String knoxMasterSecret) {
        this.knoxMasterSecret = knoxMasterSecret;
    }

    public Stack getStack() {
        return stack;
    }

    public void setStack(Stack stack) {
        this.stack = stack;
    }

    public boolean usePrivateIpToTls() {
        return usePrivateIpToTls;
    }

    public void setUsePrivateIpToTls(boolean usePrivateIpToTls) {
        this.usePrivateIpToTls = usePrivateIpToTls;
    }

    public String getSaltSignPublicKey() {
        return saltSignPublicKey;
    }

    public void setSaltSignPublicKey(String saltSignPublicKey) {
        this.saltSignPublicKey = saltSignPublicKey;
    }

    public String getSaltSignPrivateKey() {
        return saltSignPrivateKey;
    }

    public void setSaltSignPrivateKey(String saltSignPrivateKey) {
        this.saltSignPrivateKey = saltSignPrivateKey;
    }
}
