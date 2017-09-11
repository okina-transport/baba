package no.rutebanken.baba.organisation.rest.mapper;

import no.rutebanken.baba.organisation.model.CodeSpaceEntity;
import no.rutebanken.baba.organisation.model.TypeEntity;
import no.rutebanken.baba.organisation.model.VersionedEntity;
import no.rutebanken.baba.organisation.repository.CodeSpaceRepository;
import no.rutebanken.baba.organisation.rest.dto.TypeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TypeMapper<R extends VersionedEntity & TypeEntity> implements DTOMapper<R, TypeDTO> {
	@Autowired
	protected CodeSpaceRepository codeSpaceRepository;

	public TypeDTO toDTO(R entity, boolean fullDetails) {
		TypeDTO dto = new TypeDTO();
		dto.name = entity.getName();
		dto.id = entity.getId();
		dto.privateCode = entity.getPrivateCode();
		if (entity instanceof CodeSpaceEntity) {
			dto.codeSpace = ((CodeSpaceEntity) entity).getCodeSpace().getId();
		}
		return dto;
	}

	@Override
	public R createFromDTO(TypeDTO dto, Class<R> clazz) {
		R entity = createInstance(clazz);

		entity.setPrivateCode(dto.privateCode);
		if (entity instanceof CodeSpaceEntity) {
			((CodeSpaceEntity) entity).setCodeSpace(codeSpaceRepository.getOneByPublicId(dto.codeSpace));
		}

		return updateFromDTO(dto, entity);
	}

	private R createInstance(Class<R> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Failed to create instance of class: " + clazz + " : " + e.getMessage());
		}
	}

	@Override
	public R updateFromDTO(TypeDTO dto, R entity) {
		entity.setName(dto.name);
		return entity;
	}

}