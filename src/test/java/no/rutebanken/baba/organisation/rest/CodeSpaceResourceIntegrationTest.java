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
import no.rutebanken.baba.organisation.rest.dto.CodeSpaceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;


class CodeSpaceResourceIntegrationTest extends BaseIntegrationTest {

    private static final String PATH = "/services/organisations/code_spaces";

    @Test
    void codeSpaceNotFound() {
        ResponseEntity<CodeSpaceDTO> entity = restTemplate.getForEntity(PATH + "/unknownCodeSpaces",
                CodeSpaceDTO.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void crudCodeSpace() {
        CodeSpaceDTO createCodeSpace = createCodeSpace("CodeTest", "xmlnsTest", "xmlnsUrlTest");
        URI uri = restTemplate.postForLocation(PATH, createCodeSpace);
        assertCodeSpace(createCodeSpace, uri);

        CodeSpaceDTO updateCodeSpace = createCodeSpace(createCodeSpace.privateCode, createCodeSpace.xmlns, "other url");
        restTemplate.put(uri, updateCodeSpace);
        assertCodeSpace(updateCodeSpace, uri);

        CodeSpaceDTO[] allCodeSpaces =
                restTemplate.getForObject(PATH, CodeSpaceDTO[].class);
        assertCodeSpaceInArray(updateCodeSpace, allCodeSpaces);

        restTemplate.delete(uri);

        ResponseEntity<CodeSpaceDTO> entity = restTemplate.getForEntity(uri,
                CodeSpaceDTO.class);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }

    private void assertCodeSpaceInArray(CodeSpaceDTO codeSpace, CodeSpaceDTO[] array) {
        assertThat(array).isNotNull();
        assertThat(Arrays.stream(array).anyMatch(r -> r.privateCode.equals(codeSpace.privateCode))).isTrue();
    }

    protected CodeSpaceDTO createCodeSpace(String privateCode, String xmlns, String xmlnsUrl) {
        CodeSpaceDTO codeSpace = new CodeSpaceDTO();
        codeSpace.privateCode = privateCode;
        codeSpace.xmlns = xmlns;
        codeSpace.xmlnsUrl = xmlnsUrl;
        return codeSpace;
    }


    protected void assertCodeSpace(CodeSpaceDTO inCodeSpace, URI uri) {
        assertThat(uri).isNotNull();
        ResponseEntity<CodeSpaceDTO> rsp = restTemplate.getForEntity(uri, CodeSpaceDTO.class);
        CodeSpaceDTO outCodeSpace = rsp.getBody();
        assertThat(outCodeSpace).isNotNull();
        assertThat(inCodeSpace.xmlns).isEqualTo(outCodeSpace.xmlns);
        assertThat(inCodeSpace.xmlnsUrl).isEqualTo(outCodeSpace.xmlnsUrl);
        assertThat(inCodeSpace.privateCode).isEqualTo(outCodeSpace.privateCode);
    }

    @Test
    void createInvalidCodeSpace() {
        CodeSpaceDTO inCodeSpace = createCodeSpace("Code", "xmlns", null);
        ResponseEntity<String> rsp = restTemplate.postForEntity(PATH, inCodeSpace, String.class);

        assertThat(rsp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

}
