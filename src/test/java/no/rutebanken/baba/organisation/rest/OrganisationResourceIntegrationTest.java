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
import no.rutebanken.baba.organisation.rest.dto.organisation.OrganisationDTO;
import no.rutebanken.baba.organisation.rest.dto.organisation.OrganisationPartDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class OrganisationResourceIntegrationTest extends BaseIntegrationTest {

    private static final String PATH = "/services/organisations";

    @Test
    void organisationNotFound() {
        ResponseEntity<OrganisationDTO> entity = restTemplate.getForEntity(PATH + "/unknownOrganisation",
                OrganisationDTO.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void crudOrganisation() {
        OrganisationDTO createOrganisation = createOrganisation("TheOrg", "Org name", null);
        URI uri = restTemplate.postForLocation(PATH, createOrganisation);
        assertOrganisation(createOrganisation, uri);

        OrganisationPartDTO orgPart1 = new OrganisationPartDTO();
        orgPart1.name = "part 1";

        OrganisationDTO updateOrganisation = createOrganisation(createOrganisation.privateCode, "newOrg name", 2L, orgPart1);
        restTemplate.put(uri, updateOrganisation);
        assertOrganisation(updateOrganisation, uri);

        OrganisationDTO[] allOrganisations =
                restTemplate.getForObject(PATH, OrganisationDTO[].class);
        assertOrganisationInArray(updateOrganisation, allOrganisations);

        restTemplate.delete(uri);

        ResponseEntity<OrganisationDTO> entity = restTemplate.getForEntity(uri,
                OrganisationDTO.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    @Test
    void updateOrganisationParts() {
        OrganisationPartDTO orgPart1 = new OrganisationPartDTO();
        orgPart1.name = "part 1";
        orgPart1.administrativeZoneRefs = ResourceTestUtils.addAdminZones(restTemplate, "amd1", "adm2");

        OrganisationPartDTO orgPart2 = new OrganisationPartDTO();
        orgPart2.name = "part2";

        OrganisationDTO organisation = createOrganisation("OrgWithParts", "Org name", null, orgPart1, orgPart2);
        URI uri = restTemplate.postForLocation(PATH, organisation);
        assertOrganisation(organisation, uri);

        orgPart1.administrativeZoneRefs.removeFirst();
        orgPart1.administrativeZoneRefs.addAll(ResourceTestUtils.addAdminZones(restTemplate, "adm3"));

        restTemplate.put(uri, organisation);
        assertOrganisation(organisation, uri);
        organisation.parts.remove(orgPart2);

        OrganisationPartDTO orgPart3 = new OrganisationPartDTO();
        orgPart3.name = "part3";
        organisation.parts.add(orgPart3);

        restTemplate.put(uri, organisation);
        assertOrganisation(organisation, uri);

        organisation.parts = null;
        restTemplate.put(uri, organisation);
        assertOrganisation(organisation, uri);
    }


    private void assertOrganisationInArray(OrganisationDTO organisation, OrganisationDTO[] array) {
        assertThat(array).isNotNull();
        assertThat(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(organisation.privateCode))).isTrue();
    }

    protected OrganisationDTO createOrganisation(String privateCode, String name, Long companyNumber, OrganisationPartDTO... parts) {
        OrganisationDTO organisation = new OrganisationDTO();
        organisation.organisationType = OrganisationDTO.OrganisationType.AUTHORITY;
        organisation.codeSpace = TestConstantsOrganisation.CODE_SPACE_ID;
        organisation.privateCode = privateCode;
        organisation.name = name;
        organisation.companyNumber = companyNumber;
        if (parts != null) {
            organisation.parts = new ArrayList<>(Arrays.asList(parts));
        }

        return organisation;
    }


    protected void assertOrganisation(OrganisationDTO inOrganisation, URI uri) {
        assertThat(uri).isNotNull();
        ResponseEntity<OrganisationDTO> rsp = restTemplate.getForEntity(uri, OrganisationDTO.class);
        OrganisationDTO outOrganisation = rsp.getBody();
        assertThat(outOrganisation).isNotNull();
        assertThat(outOrganisation.name).isEqualTo(inOrganisation.name);
        assertThat(outOrganisation.privateCode).isEqualTo(inOrganisation.privateCode);
        assertThat(outOrganisation.companyNumber).isEqualTo(inOrganisation.companyNumber);

        if (CollectionUtils.isEmpty(inOrganisation.parts)) {
            assertThat(outOrganisation.parts).isEmpty();
        } else {
            assertThat(inOrganisation.parts).hasSameSizeAs(outOrganisation.parts);
            for (OrganisationPartDTO in : inOrganisation.parts) {
                assertThat(outOrganisation.parts.stream().anyMatch(out -> isEqual(in, out))).isTrue();
            }
        }

    }

    private boolean isEqual(OrganisationPartDTO in, OrganisationPartDTO out) {
        if (!in.name.equals(out.name)) {
            return false;
        }

        if (CollectionUtils.isEmpty(in.administrativeZoneRefs)) {
            return CollectionUtils.isEmpty(out.administrativeZoneRefs);
        }

        if (in.administrativeZoneRefs.size() != out.administrativeZoneRefs.size()) {
            return false;
        }
        return in.administrativeZoneRefs.containsAll(out.administrativeZoneRefs);
    }

    @Test
    void createInvalidOrganisation() {
        OrganisationPartDTO partWithoutName = new OrganisationPartDTO();
        OrganisationDTO inOrganisation = createOrganisation("privateCode", "organisation name", null, partWithoutName);
        ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inOrganisation, String.class);

        assertThat(rsp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createOrgWithExistingPrivateCode() {
        OrganisationDTO inOrganisation = createOrganisation("OrgPrivateCode", "organisation name", null);
        ResponseEntity<String> firstRsp = restTemplate.postForEntity(PATH, inOrganisation, String.class);

        assertThat(firstRsp.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<String> secondRsp = restTemplate.postForEntity(PATH, inOrganisation, String.class);

        assertThat(secondRsp.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

}
