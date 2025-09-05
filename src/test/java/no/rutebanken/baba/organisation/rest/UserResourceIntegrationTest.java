/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 */

package no.rutebanken.baba.organisation.rest;

import com.google.common.collect.Sets;
import no.rutebanken.baba.organisation.TestConstantsOrganisation;
import no.rutebanken.baba.organisation.model.user.NotificationType;
import no.rutebanken.baba.organisation.model.user.eventfilter.JobState;
import no.rutebanken.baba.organisation.repository.BaseIntegrationTest;
import no.rutebanken.baba.organisation.rest.dto.user.ContactDetailsDTO;
import no.rutebanken.baba.organisation.rest.dto.user.EventFilterDTO;
import no.rutebanken.baba.organisation.rest.dto.user.NotificationConfigDTO;
import no.rutebanken.baba.organisation.rest.dto.user.UserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class UserResourceIntegrationTest extends BaseIntegrationTest {

    private static final String PATH = "/services/organisations/users";

    @Test
    void userNotFound() {
        ResponseEntity<UserDTO> entity = restTemplate.getForEntity(PATH + "/unknownUser",
                UserDTO.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void crudUser() {
        ContactDetailsDTO createContactDetails = new ContactDetailsDTO("first", "last", "phone", "email@email.com");
        UserDTO createUser = createUser("userName", TestConstantsOrganisation.ORGANISATION_ID, createContactDetails);
        ResponseEntity<String> createResponse = restTemplate.postForEntity(PATH, createUser, String.class);
        assertThat(createResponse).isNotNull();
        assertThat(createResponse.getBody()).isNotNull();
        URI uri = createResponse.getHeaders().getLocation();
        assertUser(createUser, uri);

        ContactDetailsDTO updateContactDetails = new ContactDetailsDTO("otherFirst", "otherLast", null, "other@email.org");
        UserDTO updateUser = createUser(createUser.username, createUser.organisationRef, updateContactDetails);
        restTemplate.put(uri, updateUser);
        assertUser(updateUser, uri);

        UserDTO[] allUsers =
                restTemplate.getForObject(PATH, UserDTO[].class);
        assertUserInArray(updateUser, allUsers);

        UserDTO[] allUsersWithFullDetails =
                restTemplate.getForObject(PATH + "?full=true", UserDTO[].class);
        assertUserInArray(updateUser, allUsersWithFullDetails);
        assertThat(allUsersWithFullDetails[0].organisation.name).isNotNull();

        assertThat(uri).isNotNull();
        ResponseEntity<String> resetPasswordResponse = restTemplate.postForEntity(uri.getPath() + "/resetPassword", createUser, String.class);
        assertThat(resetPasswordResponse.getBody()).isNotNull();
        assertThat(resetPasswordResponse.getBody()).isNotEqualTo(createResponse.getBody());

        restTemplate.delete(uri);

        ResponseEntity<UserDTO> entity = restTemplate.getForEntity(uri,
                UserDTO.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateUserWithNotificationConfigurations() {
        ContactDetailsDTO createContactDetails = new ContactDetailsDTO("first", "last", "phone", "email@email.com");
        UserDTO user = createUser("userWithNotificationConfig", TestConstantsOrganisation.ORGANISATION_ID, createContactDetails);
        URI uri = restTemplate.postForLocation(PATH, user);
        assertUser(user, uri);

        Set<NotificationConfigDTO> config = Sets.newHashSet(new NotificationConfigDTO(NotificationType.WEB, false, jobEventFilter("action", JobState.FAILED)));
        ResourceTestUtils.setNotificationConfig(restTemplate, user.username, config);

        user.contactDetails.firstName = "changeFirstName";
        restTemplate.put(uri, user);
        assertUser(user, uri);
    }

    @Test
    void updateUsersResponsibilitySets() {
        ContactDetailsDTO contactDetails = new ContactDetailsDTO("first", "last", "phone", "email@email.com");
        UserDTO user = createUser("userName", TestConstantsOrganisation.ORGANISATION_ID, contactDetails);
        URI uri = restTemplate.postForLocation(PATH, user);
        assertUser(user, uri);

        user.responsibilitySetRefs = List.of(TestConstantsOrganisation.RESPONSIBILITY_SET_ID);
        restTemplate.put(uri, user);
        assertUser(user, uri);

        user.responsibilitySetRefs = Arrays.asList(TestConstantsOrganisation.RESPONSIBILITY_SET_ID, TestConstantsOrganisation.RESPONSIBILITY_SET_ID_2);
        restTemplate.put(uri, user);
        assertUser(user, uri);

        user.responsibilitySetRefs = List.of(TestConstantsOrganisation.RESPONSIBILITY_SET_ID_2);
        restTemplate.put(uri, user);
        assertUser(user, uri);

        user.responsibilitySetRefs = null;
        restTemplate.put(uri, user);
        assertUser(user, uri);
    }

    private void assertUserInArray(UserDTO user, UserDTO[] array) {
        assertThat(array).isNotNull();
        assertThat(Arrays.stream(array).anyMatch(r -> r.username.equals(user.username.toLowerCase()))).isTrue();
    }

    protected UserDTO createUser(String username, String orgRef, ContactDetailsDTO contactDetails, String... respSetRefs) {
        UserDTO user = new UserDTO();
        user.username = username;
        user.organisationRef = orgRef;
        user.contactDetails = contactDetails;
        if (respSetRefs != null) {
            user.responsibilitySetRefs = Arrays.asList(respSetRefs);
        }

        return user;
    }


    protected void assertUser(UserDTO inUser, URI uri) {
        assertThat(uri).isNotNull();
        ResponseEntity<UserDTO> rsp = restTemplate.getForEntity(uri, UserDTO.class);
        UserDTO outUser = rsp.getBody();

        assertThat(outUser).isNotNull();
        assertUserBasics(inUser, outUser);
        assertThat(outUser.organisation).isNull();
        assertThat(outUser.responsibilitySets).isNull();


        ResponseEntity<UserDTO> fullRsp = restTemplate.getForEntity(uri.toString() + "?full=true", UserDTO.class);
        UserDTO fullOutUser = fullRsp.getBody();
        assertThat(fullOutUser).isNotNull();
        assertUserBasics(inUser, fullOutUser);
        assertThat(fullOutUser.organisation.name).isNotNull();
        if (CollectionUtils.isEmpty(inUser.responsibilitySetRefs)) {
            assertThat(fullOutUser.responsibilitySetRefs).isEmpty();
        } else {
            assertThat(inUser.responsibilitySetRefs).hasSameSizeAs(fullOutUser.responsibilitySetRefs);
        }
        assertThat(fullOutUser.responsibilitySets.stream().allMatch(rs -> rs.name != null)).isTrue();
    }

    private void assertUserBasics(UserDTO inUser, UserDTO outUser) {
        assertThat(inUser.username).isEqualToIgnoringCase(outUser.username);
        assertThat(inUser.privateCode).isEqualTo(outUser.privateCode);

        if (CollectionUtils.isEmpty(inUser.responsibilitySetRefs)) {
            assertThat(outUser.responsibilitySetRefs).isEmpty();
        } else {
            assertThat(inUser.responsibilitySetRefs).hasSameSizeAs(outUser.responsibilitySetRefs);
            assertThat(inUser.responsibilitySetRefs).containsAll(outUser.responsibilitySetRefs);
        }

        if (inUser.contactDetails == null) {
            assertThat(outUser.contactDetails).isNotNull();

        } else {
            assertThat(inUser.contactDetails.firstName).isEqualTo(outUser.contactDetails.firstName);
            assertThat(inUser.contactDetails.lastName).isEqualTo(outUser.contactDetails.lastName);
            assertThat(inUser.contactDetails.email).isEqualTo(outUser.contactDetails.email);
            assertThat(inUser.contactDetails.phone).isEqualTo(outUser.contactDetails.phone);
        }

    }

    @Test
    void createInvalidUser() {
        UserDTO inUser = createUser("user name", "privateCode", null);
        ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inUser, String.class);

        assertThat(rsp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    private EventFilterDTO jobEventFilter(String action, JobState jobState) {
        EventFilterDTO eventFilterDTO = new EventFilterDTO(EventFilterDTO.EventFilterType.JOB);
        eventFilterDTO.actions = Sets.newHashSet(action);
        eventFilterDTO.jobDomain = EventFilterDTO.JobDomain.TIMETABLE;
        eventFilterDTO.states = Sets.newHashSet(jobState);
        return eventFilterDTO;
    }
}
