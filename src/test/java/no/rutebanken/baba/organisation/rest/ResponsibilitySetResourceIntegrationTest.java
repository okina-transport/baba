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

import no.rutebanken.baba.organisation.TestConstantsOrganisation;
import no.rutebanken.baba.organisation.repository.BaseIntegrationTest;
import no.rutebanken.baba.organisation.rest.dto.responsibility.EntityClassificationAssignmentDTO;
import no.rutebanken.baba.organisation.rest.dto.responsibility.ResponsibilityRoleAssignmentDTO;
import no.rutebanken.baba.organisation.rest.dto.responsibility.ResponsibilitySetDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class ResponsibilitySetResourceIntegrationTest extends BaseIntegrationTest {

    private static final String PATH = "/services/organisations/responsibility_sets";

    @Test
    void responsibilitySetNotFound() {
        ResponseEntity<ResponsibilitySetDTO> entity = restTemplate.getForEntity(PATH + "/unknownResponsibilitySet",
                ResponsibilitySetDTO.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void crudResponsibilitySet() {

        ResponsibilitySetDTO createResponsibilitySet = createResponsibilitySet("RspSet", "RspSet name",
                new ResponsibilityRoleAssignmentDTO(TestConstantsOrganisation.ROLE_ID, TestConstantsOrganisation.ORGANISATION_ID));

        URI uri = restTemplate.postForLocation(PATH, createResponsibilitySet);
        assertResponsibilitySet(createResponsibilitySet, uri);

        ResponsibilityRoleAssignmentDTO role1 = new ResponsibilityRoleAssignmentDTO(TestConstantsOrganisation.ROLE_ID, TestConstantsOrganisation.ORGANISATION_ID);

        ResponsibilitySetDTO updateResponsibilitySet = createResponsibilitySet(createResponsibilitySet.privateCode, "RspSet new name", role1);
        restTemplate.put(uri, updateResponsibilitySet);
        assertResponsibilitySet(updateResponsibilitySet, uri);

        ResponsibilitySetDTO[] allResponsibilitySets =
                restTemplate.getForObject(PATH, ResponsibilitySetDTO[].class);
        assertResponsibilitySetInArray(updateResponsibilitySet, allResponsibilitySets);

        restTemplate.delete(uri);

        ResponseEntity<ResponsibilitySetDTO> entity = restTemplate.getForEntity(uri,
                ResponsibilitySetDTO.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void updateResponsibilitySetRoles() {
        ResponsibilityRoleAssignmentDTO role1 = new ResponsibilityRoleAssignmentDTO(TestConstantsOrganisation.ROLE_ID, TestConstantsOrganisation.ORGANISATION_ID);
        role1.responsibleOrganisationRef = TestConstantsOrganisation.ORGANISATION_ID;
        role1.typeOfResponsibilityRoleRef = TestConstantsOrganisation.ROLE_ID;
        role1.responsibleAreaRef = ResourceTestUtils.addAdminZone(restTemplate, "respArea1");

        ResponsibilityRoleAssignmentDTO role2 = new ResponsibilityRoleAssignmentDTO(TestConstantsOrganisation.ROLE_ID, TestConstantsOrganisation.ORGANISATION_ID);
        role2.entityClassificationAssignments = List.of(new EntityClassificationAssignmentDTO(TestConstantsOrganisation.ENTITY_CLASSIFICATION_ID, true));

        ResponsibilitySetDTO responsibilitySet = createResponsibilitySet("RspSetUpdate", "RspSet name", role1, role2);
        URI uri = restTemplate.postForLocation(PATH, responsibilitySet);
        assertResponsibilitySet(responsibilitySet, uri);

        role1.responsibleAreaRef = null;
        role2.entityClassificationAssignments = Arrays.asList(new EntityClassificationAssignmentDTO(TestConstantsOrganisation.ENTITY_CLASSIFICATION_ID, false),
                new EntityClassificationAssignmentDTO(TestConstantsOrganisation.ENTITY_CLASSIFICATION_ID_2, true));

        restTemplate.put(uri, responsibilitySet);
        assertResponsibilitySet(responsibilitySet, uri);
        responsibilitySet.roles.remove(role2);

        ResponsibilityRoleAssignmentDTO role3 = new ResponsibilityRoleAssignmentDTO(TestConstantsOrganisation.ROLE_ID, TestConstantsOrganisation.ORGANISATION_ID);
        responsibilitySet.roles.add(role3);
        role1.entityClassificationAssignments = List.of(new EntityClassificationAssignmentDTO(TestConstantsOrganisation.ENTITY_CLASSIFICATION_ID_2, true));
        role2.entityClassificationAssignments = null;

        restTemplate.put(uri, responsibilitySet);
        assertResponsibilitySet(responsibilitySet, uri);

        responsibilitySet.roles.removeFirst();
        restTemplate.put(uri, responsibilitySet);
        assertResponsibilitySet(responsibilitySet, uri);
    }


    private void assertResponsibilitySetInArray(ResponsibilitySetDTO responsibilitySet, ResponsibilitySetDTO[] array) {
        assertThat(array).isNotNull();
        assertThat(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(responsibilitySet.privateCode))).isTrue();
    }

    protected ResponsibilitySetDTO createResponsibilitySet(String privateCode, String name, ResponsibilityRoleAssignmentDTO... roles) {
        ResponsibilitySetDTO responsibilitySet = new ResponsibilitySetDTO();
        responsibilitySet.codeSpace = TestConstantsOrganisation.CODE_SPACE_ID;
        responsibilitySet.privateCode = privateCode;
        responsibilitySet.name = name;
        if (roles != null) {
            responsibilitySet.roles = new ArrayList<>(Arrays.asList(roles));
        }

        return responsibilitySet;
    }


    protected void assertResponsibilitySet(ResponsibilitySetDTO inResponsibilitySet, URI uri) {
        assertThat(uri).isNotNull();
        ResponseEntity<ResponsibilitySetDTO> rsp = restTemplate.getForEntity(uri, ResponsibilitySetDTO.class);
        ResponsibilitySetDTO outResponsibilitySet = rsp.getBody();
        assertThat(outResponsibilitySet).isNotNull();
        assertThat(inResponsibilitySet.name).isEqualTo(outResponsibilitySet.name);
        assertThat(inResponsibilitySet.privateCode).isEqualTo(outResponsibilitySet.privateCode);
        assertThat(inResponsibilitySet.codeSpace).isEqualTo(outResponsibilitySet.codeSpace);

        if (CollectionUtils.isEmpty(inResponsibilitySet.roles)) {
            assertThat(outResponsibilitySet.roles).isEmpty();
        } else {
            assertThat(inResponsibilitySet.roles).hasSameSizeAs(outResponsibilitySet.roles);
            for (ResponsibilityRoleAssignmentDTO in : inResponsibilitySet.roles) {
                assertThat(outResponsibilitySet.roles.stream().anyMatch(out -> isEqual(in, out))).isTrue();
            }
        }

    }

    private boolean isEqual(ResponsibilityRoleAssignmentDTO in, ResponsibilityRoleAssignmentDTO out) {
        if (!in.typeOfResponsibilityRoleRef.equals(out.typeOfResponsibilityRoleRef)) {
            return false;
        }
        if (!in.responsibleOrganisationRef.equals(out.responsibleOrganisationRef)) {
            return false;
        }
        if (in.responsibleAreaRef == null) {
            if (out.responsibleAreaRef != null) {
                return false;
            }
        } else if (!in.responsibleAreaRef.equals(out.responsibleAreaRef)) {
            return false;
        }

        if (CollectionUtils.isEmpty(in.entityClassificationAssignments)) {
            return CollectionUtils.isEmpty(out.entityClassificationAssignments);
        } else if (out.entityClassificationAssignments == null) {
            return false;
        }

        if (in.entityClassificationAssignments.size() != out.entityClassificationAssignments.size()) {
            return false;
        }
        return in.entityClassificationAssignments.containsAll(out.entityClassificationAssignments);
    }

    @Test
    void createInvalidResponsibilitySetWithMissingRoles() {
        ResponsibilityRoleAssignmentDTO roleWithoutName = new ResponsibilityRoleAssignmentDTO();
        ResponsibilitySetDTO inResponsibilitySet = createResponsibilitySet("privateCode", "responsibilitySet name", roleWithoutName);
        ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inResponsibilitySet, String.class);

        assertThat(rsp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createOrgWithExistingPrivateCode() {
        ResponsibilitySetDTO inResponsibilitySet = createResponsibilitySet("OrgPrivateCode", "responsibilitySet name",
                new ResponsibilityRoleAssignmentDTO(TestConstantsOrganisation.ROLE_ID, TestConstantsOrganisation.ORGANISATION_ID));

        ResponseEntity<String> firstRsp = restTemplate.postForEntity(PATH, inResponsibilitySet, String.class);

        assertThat(firstRsp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> secondRsp = restTemplate.postForEntity(PATH, inResponsibilitySet, String.class);

        assertThat(secondRsp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}