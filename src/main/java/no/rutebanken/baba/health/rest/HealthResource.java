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

package no.rutebanken.baba.health.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import no.rutebanken.baba.health.repository.DbStatusChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Produces("application/json")
@Path("")
@Tags(value = {
        @Tag(name = "Health Resource", description = "Application status resource")
})
public class HealthResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DbStatusChecker dbStatusChecker;

    @GET
    @Path("/ready")
    @Operation(description = "Checks application readiness, including db connection")
    @ApiResponses(value = {
                                  @ApiResponse(responseCode = "200", description = "application is ready"),
                                  @ApiResponse(responseCode = "500", description = "application is not ready")
    })
    public Response isReady() {
        logger.debug("Checking readiness...");
        if (dbStatusChecker.isDbUp()) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/live")
    @Operation(description = "Returns OK if application is running")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
                                  @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "application is running")
    })
    public Response isLive() {
        return Response.ok().build();
    }

}
