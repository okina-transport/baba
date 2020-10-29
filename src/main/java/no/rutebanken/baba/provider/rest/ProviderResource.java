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

package no.rutebanken.baba.provider.rest;

import io.swagger.annotations.Api;

import no.rutebanken.baba.provider.domain.Provider;
import no.rutebanken.baba.provider.domain.TransportMode;
import no.rutebanken.baba.provider.repository.ProviderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ROUTE_DATA_ADMIN;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ROUTE_DATA_EDIT;


@Component
@Produces("application/json")
@Path("")
@Api
public class ProviderResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProviderRepository providerRepository;

    @GET
    @Path("/{providerId}")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#providerId)")
    public Provider getProvider(@PathParam("providerId") Long providerId) {
        logger.debug("Returning provider with id '" + providerId + "'");
        Provider provider = providerRepository.getProvider(providerId);
        if (provider == null) {
            throw new NotFoundException("Unable to find provider with id=" + providerId);
        }
        return provider;
    }


    @GET
    @Path("/referential")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#providerId)")
    public Provider getProvider(@QueryParam("referential") String referential) {
        logger.debug("Returning provider with referential '" + referential + "'");
        Optional<Provider> provider = providerRepository.getProviderByReferential(referential);
        if (!provider.isPresent()) {
            throw new NotFoundException("No provider found matching referential " + referential);
        }
        return provider.get();
    }

    @DELETE
    @Path("/{providerId}")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#providerId)")
    public void deleteProvider(@PathParam("providerId") Long providerId) {
        logger.info("Deleting provider with id '" + providerId + "'");
        providerRepository.deleteProvider(providerId);
    }

    @PUT
    @Path("/{providerId}")
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#provider.id)")
    public void updateProvider(Provider provider) {
        logger.info("Updating provider " + provider);
        providerRepository.updateProvider(provider);
    }

    @POST
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "') or @providerAuthenticationService.hasRoleForProvider(authentication,'" + ROLE_ROUTE_DATA_EDIT + "',#provider.id)")
    public Provider createProvider(Provider provider) {
        logger.info("Creating provider " + provider);
        return providerRepository.createProvider(provider);
    }


    @POST
    @Path("/sync")
//    @PreAuthorize("isAuthenticated()")
    public List<Provider> syncProviders(List<Provider> providers) {
        logger.info("Synching providers ");
        providers.forEach(p -> {
            providerRepository.updateMosaicIdByName(p.name, p.mosaicId);
            providerRepository.updateChouetteInfo(p.chouetteInfo.user, p.chouetteInfo.codeIdfm, p.chouetteInfo.nameNetexStopIdfm, p.chouetteInfo.nameNetexOffreIdfm);
        });
        return providers;
    }

    @GET
    @Path("/all")
    @Deprecated
    public Collection<Provider> getAllProviders() {
        return getProviders();
    }

    @GET
    public Collection<Provider> getProviders() {
        logger.debug("Returning all providers.");
        return providerRepository.getProviders();
    }


    @GET
    @Path("transport_modes")
    public TransportMode[] getTransportModes() {
        return TransportMode.values();
    }

}
