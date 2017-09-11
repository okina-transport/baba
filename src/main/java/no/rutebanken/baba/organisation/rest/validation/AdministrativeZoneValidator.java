package no.rutebanken.baba.organisation.rest.validation;

import no.rutebanken.baba.organisation.model.organisation.AdministrativeZone;
import no.rutebanken.baba.organisation.rest.dto.organisation.AdministrativeZoneDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AdministrativeZoneValidator implements DTOValidator<AdministrativeZone, AdministrativeZoneDTO> {

	@Override
	public void validateCreate(AdministrativeZoneDTO dto) {
		Assert.hasLength(dto.privateCode, "privateCode required");
		Assert.hasLength(dto.codeSpace, "codeSpace required");
		assertCommon(dto);
	}

	@Override
	public void validateUpdate(AdministrativeZoneDTO dto, AdministrativeZone entity) {
		assertCommon(dto);
	}

	private void assertCommon(AdministrativeZoneDTO dto){
		Assert.hasLength(dto.name, "name required");
		Assert.notNull(dto.polygon,"polygon required");
		Assert.notNull(dto.source,"source required");
		Assert.notNull(dto.type,"type required");
	}

	@Override
	public void validateDelete(AdministrativeZone entity) {
		// TODO check whether in user
	}
}
