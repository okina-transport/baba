package no.rutebanken.baba.organisation.repository;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import no.rutebanken.baba.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.baba.organisation.model.organisation.AdministrativeZoneType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class AdministrativeZoneRepositoryTest extends BaseIntegrationTest {

	@Autowired
	private AdministrativeZoneRepository administrativeZoneRepository;


	@Test
	public void testInsertAdministrativeZone() {
		AdministrativeZone zone = new AdministrativeZone();
		zone.setPrivateCode("0101");
		zone.setName("name");
		zone.setCodeSpace(defaultCodeSpace);
		zone.setAdministrativeZoneType(AdministrativeZoneType.COUNTY);
		zone.setSource("KVE");

		GeometryFactory fact = new GeometryFactory();
		LinearRing linear = new GeometryFactory().createLinearRing(new Coordinate[]{new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(1, 1), new Coordinate(0, 0)});
		Polygon poly = new Polygon(linear, null, fact);

		zone.setPolygon(poly);

		administrativeZoneRepository.saveAndFlush(zone);
	}
}
