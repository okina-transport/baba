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
import no.rutebanken.baba.organisation.rest.dto.TypeDTO;
import no.rutebanken.baba.organisation.rest.dto.responsibility.EntityTypeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


class EntityTypeResourceIntegrationTest extends BaseIntegrationTest {

	private static final String PATH = "/services/organisations/entity_types";

	@Test
	void entityTypeNotFound() {
		ResponseEntity<EntityTypeDTO> entity = restTemplate.getForEntity(PATH + "/unknownEntityTypes",
				EntityTypeDTO.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void crudEntityType() {
		EntityTypeDTO createEntityType = createEntityType("entityType name", "privateCode");
		URI uri = restTemplate.postForLocation(PATH, createEntityType);
		assertEntityType(createEntityType, uri);

		EntityTypeDTO updateEntityType = createEntityType("new name", createEntityType.privateCode);
		restTemplate.put(uri, updateEntityType);
		assertEntityType(updateEntityType, uri);

		// Save same again, should yield no changes
		restTemplate.put(uri, updateEntityType);
		assertEntityType(updateEntityType, uri);


		EntityTypeDTO[] allEntityTypes =
				restTemplate.getForObject(PATH, EntityTypeDTO[].class);
		assertEntityTypeInArray(updateEntityType, allEntityTypes);

		restTemplate.delete(uri);

		ResponseEntity<EntityTypeDTO> entity = restTemplate.getForEntity(uri,
				EntityTypeDTO.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}


	@Test
	void updateEntityClassifications() {
		TypeDTO classification1 = createClassification("c1", "n1");
		TypeDTO classification2 = createClassification("c2", "n2");
		EntityTypeDTO entityType = createEntityType("RspSetUpdate", "RspSet name", classification1, classification2);
		URI uri = restTemplate.postForLocation(PATH, entityType);
		EntityTypeDTO createdType = assertEntityType(entityType, uri);

		createdType.classifications.getFirst().name = "newName";
		createdType.classifications.remove(1);

		TypeDTO classification3 = createClassification("c3", "n3");
		createdType.classifications.add(classification3);

		restTemplate.put(uri, createdType);
		EntityTypeDTO updatedType = assertEntityType(createdType, uri);

		updatedType.classifications.clear();

		restTemplate.put(uri, updatedType);
		assertEntityType(updatedType, uri);
	}


	public EntityTypeDTO assertEntityType(EntityTypeDTO in, URI uri) {
        assertThat(uri).isNotNull();
		ResponseEntity<EntityTypeDTO> rsp = restTemplate.getForEntity(uri, EntityTypeDTO.class);
		EntityTypeDTO out = rsp.getBody();
        assertThat(out).isNotNull();
        assertThat(in.name).isEqualTo(out.name);
        assertThat(in.privateCode).isEqualTo(out.privateCode);
		if (CollectionUtils.isEmpty(in.classifications)) {
            assertThat(in.classifications).isEmpty();
		} else {
            assertThat(in.classifications).hasSameSizeAs(out.classifications);
			for (TypeDTO inClassification : in.classifications) {
				assertThat(out.classifications.stream().anyMatch(outClassification -> outClassification.privateCode.equals(inClassification.privateCode))).isTrue();
			}
		}
		return out;
	}

	private void assertEntityTypeInArray(EntityTypeDTO entityType, EntityTypeDTO[] array) {
        assertThat(array).isNotNull();
        assertThat(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(entityType.privateCode))).isTrue();
	}

	protected EntityTypeDTO createEntityType(String name, String privateCode, TypeDTO... classifications) {
		EntityTypeDTO entityType = new EntityTypeDTO();
		entityType.name = name;
		entityType.privateCode = privateCode;
		entityType.codeSpace = TestConstantsOrganisation.CODE_SPACE_ID;
		if (classifications != null) {
			entityType.classifications = new ArrayList<>(Arrays.asList(classifications));
		}
		return entityType;
	}

	protected TypeDTO createClassification(String privateCode, String name) {
		TypeDTO classification = new TypeDTO();
		classification.name = name;
		classification.privateCode = privateCode;

		return classification;
	}


	@Test
	void createInvalidEntityType() {
		EntityTypeDTO inEntityType = createEntityType("entityType name", null);
		ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inEntityType, String.class);

        assertThat(rsp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
	}

}