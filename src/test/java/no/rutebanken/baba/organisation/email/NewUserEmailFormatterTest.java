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

package no.rutebanken.baba.organisation.email;

import no.rutebanken.baba.organisation.model.user.ContactDetails;
import no.rutebanken.baba.organisation.model.user.User;
import no.rutebanken.baba.organisation.repository.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class NewUserEmailFormatterTest extends BaseIntegrationTest {

    @Autowired
    private NewUserEmailFormatter emailFormatter;

    @Test
    void testFormatNewUserEmail() {
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setEmail("e@e.org");
        contactDetails.setFirstName("First");
        contactDetails.setLastName("Last");
        User user = User.builder().withContactDetails(contactDetails).withUsername("test-user").build();

        String msg = emailFormatter.formatMessage(user,  Locale.of("no"));

        assertTrue(msg.startsWith("<html>"));
        assertTrue(msg.contains(contactDetails.getFirstName() + " " + contactDetails.getLastName()));
        assertTrue(msg.contains(user.getUsername()));
    }

    @Test
    void testGetNewUserEmailSubject() {
        assertEquals("Référential Multimodal Régional - Création de compte", emailFormatter.getSubject(Locale.FRENCH));
    }

}
