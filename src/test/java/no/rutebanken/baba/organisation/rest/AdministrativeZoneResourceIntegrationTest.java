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


import no.rutebanken.baba.organisation.repository.BaseIntegrationTest;
import no.rutebanken.baba.organisation.rest.dto.organisation.AdministrativeZoneDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Arrays;

import static no.rutebanken.baba.organisation.rest.ResourceTestUtils.createAdministrativeZone;
import static no.rutebanken.baba.organisation.rest.ResourceTestUtils.validPolygon;
import static org.assertj.core.api.Assertions.assertThat;

class AdministrativeZoneResourceIntegrationTest extends BaseIntegrationTest {

    private static final String PATH = "/services/organisations/administrative_zones";

    @Test
    void administrativeZoneNotFound()  {
        ResponseEntity<AdministrativeZoneDTO> entity = restTemplate.getForEntity(PATH + "/unknownAdministrativeZones",
                AdministrativeZoneDTO.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void crudAdministrativeZone() {
        AdministrativeZoneDTO createAdministrativeZone = createAdministrativeZone("administrativeZone name", "privateCode", validPolygon());
        URI uri = restTemplate.postForLocation(PATH, createAdministrativeZone);
        assertAdministrativeZone(createAdministrativeZone, uri);

        AdministrativeZoneDTO updateAdministrativeZone = createAdministrativeZone("new name", createAdministrativeZone.privateCode, validPolygon());
        restTemplate.put(uri, updateAdministrativeZone);
        assertAdministrativeZone(updateAdministrativeZone, uri);


        AdministrativeZoneDTO[] allAdministrativeZones =
                restTemplate.getForObject(PATH, AdministrativeZoneDTO[].class);
        assertAdministrativeZoneInArray(updateAdministrativeZone, allAdministrativeZones);

        restTemplate.delete(uri);

        ResponseEntity<AdministrativeZoneDTO> entity = restTemplate.getForEntity(uri,
                AdministrativeZoneDTO.class);

        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    private void assertAdministrativeZoneInArray(AdministrativeZoneDTO administrativeZone, AdministrativeZoneDTO[] array) {
        assertThat(array).isNotNull();
        assertThat(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(administrativeZone.privateCode))).isTrue();
    }


    protected void assertAdministrativeZone(AdministrativeZoneDTO inAdministrativeZone, URI uri) {
        assertThat(uri).isNotNull();
        ResponseEntity<AdministrativeZoneDTO> rsp = restTemplate.getForEntity(uri, AdministrativeZoneDTO.class);
        AdministrativeZoneDTO outAdministrativeZone = rsp.getBody();
        assertThat(outAdministrativeZone).isNotNull();
        assertThat(inAdministrativeZone.name).isEqualTo(outAdministrativeZone.name);
        assertThat(inAdministrativeZone.privateCode).isEqualTo(outAdministrativeZone.privateCode);
        assertThat(inAdministrativeZone.type).isEqualTo(outAdministrativeZone.type);
        assertThat(inAdministrativeZone.source).isEqualTo(outAdministrativeZone.source);
    }

    @Test
    void createInvalidAdministrativeZone() {
        AdministrativeZoneDTO inAdministrativeZone = createAdministrativeZone("administrativeZone name", "privateCode", null);
        ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inAdministrativeZone, String.class);

        assertThat(rsp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
