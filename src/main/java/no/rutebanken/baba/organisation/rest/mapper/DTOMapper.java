package no.rutebanken.baba.organisation.rest.mapper;

import no.rutebanken.baba.organisation.model.VersionedEntity;
import no.rutebanken.baba.organisation.rest.dto.BaseDTO;

public interface DTOMapper<E extends VersionedEntity, D extends BaseDTO> {

	E createFromDTO(D dto, Class<E> clazz);

	E updateFromDTO(D dto, E entity);

	D toDTO(E entity, boolean fullDetails);

}
