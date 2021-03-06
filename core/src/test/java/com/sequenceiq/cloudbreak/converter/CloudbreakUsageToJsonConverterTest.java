package com.sequenceiq.cloudbreak.converter;

import static com.sequenceiq.cloudbreak.common.type.CloudConstants.AWS;
import static com.sequenceiq.cloudbreak.common.type.CloudConstants.GCP;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.sequenceiq.cloudbreak.TestUtil;
import com.sequenceiq.cloudbreak.api.model.CloudbreakUsageJson;
import com.sequenceiq.cloudbreak.common.type.CbUserRole;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.domain.CloudbreakUsage;
import com.sequenceiq.cloudbreak.service.user.UserDetailsService;
import com.sequenceiq.cloudbreak.service.user.UserFilterField;

public class CloudbreakUsageToJsonConverterTest extends AbstractEntityConverterTest<CloudbreakUsage> {

    @InjectMocks
    private CloudbreakUsageToJsonConverter underTest;

    @Mock
    private UserDetailsService userDetailsService;

    private CbUser user;

    @Before
    public void setUp() {
        underTest = new CloudbreakUsageToJsonConverter();
        user = createCbUser();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testConvert() {
        // GIVEN
        given(userDetailsService.getDetails(anyString(), any(UserFilterField.class))).willReturn(user);
        // WHEN
        CloudbreakUsageJson result = underTest.convert(getSource());
        // THEN
        assertEquals(GCP, result.getProvider());
        assertEquals("john.smith@example.com", result.getUsername());
        assertAllFieldsNotNull(result, Lists.newArrayList("availabilityZone", "duration"));
    }

    @Test
    public void testConvertWithAwsProvider() {
        // GIVEN
        getSource().setProvider(AWS);
        getSource().setRegion("us_east_1");
        given(userDetailsService.getDetails(anyString(), any(UserFilterField.class))).willReturn(user);
        // WHEN
        CloudbreakUsageJson result = underTest.convert(getSource());
        // THEN
        assertEquals(AWS, result.getProvider());
        assertEquals("john.smith@example.com", result.getUsername());
        assertAllFieldsNotNull(result, Lists.newArrayList("availabilityZone", "duration"));
    }

    @Test
    public void testConvertWithGcpProvider() {
        // GIVEN
        getSource().setProvider(GCP);
        getSource().setRegion("us_central1");
        getSource().setAvailabilityZone("us_central1_a");
        given(userDetailsService.getDetails(anyString(), any(UserFilterField.class))).willReturn(user);
        // WHEN
        CloudbreakUsageJson result = underTest.convert(getSource());
        // THEN
        assertEquals(GCP, result.getProvider());
        assertEquals("john.smith@example.com", result.getUsername());
        assertAllFieldsNotNull(result, Lists.newArrayList("availabilityZone", "duration"));
    }

    @Override
    public CloudbreakUsage createSource() {
        return TestUtil.gcpCloudbreakUsage(1L);
    }

    private CbUser createCbUser() {
        return new CbUser("dummyUserId", "john.smith@example.com", "dummyAccount",
                Arrays.asList(CbUserRole.ADMIN, CbUserRole.USER), "John", "Smith", new Date());
    }

}
