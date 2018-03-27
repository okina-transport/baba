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

package no.rutebanken.baba.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyCloakClientConfiguration {

    @Value("${iam.keycloak.admin.path}")
    private String adminPath;

    @Value("${iam.keycloak.admin.realm:master}")
    private String masterRealm;

    @Value("${iam.keycloak.user.realm:rutebanken}")
    private String userRealm;


    @Value("${iam.keycloak.admin.client:baba}")
    private String clientId;

    @Value("${iam.keycloak.admin.client.secret}")
    private String clientSecret;

    @Bean
    public RealmResource keycloakAdminClient() {

        // Allow upgrading keycloak without too much pain.
        ResteasyJackson2Provider jackson2Provider = new ResteasyJackson2Provider();
        ObjectMapper mapper = new ObjectMapper()
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        jackson2Provider.setMapper(mapper);

        return KeycloakBuilder.builder()
                .serverUrl(adminPath)
                .realm(masterRealm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .resteasyClient(new ResteasyClientBuilder()
                        .connectionPoolSize(10)
                        .register(jackson2Provider)
                        .build())
                .build().realm(userRealm);

    }
}
