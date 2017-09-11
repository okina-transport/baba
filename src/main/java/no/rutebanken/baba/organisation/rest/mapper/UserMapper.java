package no.rutebanken.baba.organisation.rest.mapper;

import no.rutebanken.baba.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.baba.organisation.model.user.ContactDetails;
import no.rutebanken.baba.organisation.model.user.User;
import no.rutebanken.baba.organisation.repository.OrganisationRepository;
import no.rutebanken.baba.organisation.repository.ResponsibilitySetRepository;
import no.rutebanken.baba.organisation.rest.dto.user.ContactDetailsDTO;
import no.rutebanken.baba.organisation.rest.dto.user.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserMapper implements DTOMapper<User, UserDTO> {

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ResponsibilitySetRepository responsibilitySetRepository;


    @Autowired
    private OrganisationMapper organisationMapper;

    @Autowired
    private ResponsibilitySetMapper responsibilitySetMapper;

    @Autowired
    private NotificationConfigurationMapper notificationConfigurationMapper;

    public UserDTO toDTO(User org, boolean fullDetails) {
        UserDTO dto = new UserDTO();
        dto.id = org.getId();
        dto.username = org.getUsername();

        dto.contactDetails = toDTO(org.getContactDetails());

        dto.responsibilitySetRefs = toRefList(org.getResponsibilitySets());
        dto.organisationRef = org.getOrganisation().getId();

        if (fullDetails) {
            dto.notifications = notificationConfigurationMapper.toDTO(org.getNotificationConfigurations());
            dto.organisation = organisationMapper.toDTO(org.getOrganisation(), false);
            dto.responsibilitySets = org.getResponsibilitySets().stream().map(rs -> responsibilitySetMapper.toDTO(rs, false)).collect(Collectors.toList());
        }

        return dto;
    }

    public User createFromDTO(UserDTO dto, Class<User> clazz) {
        User entity = new User();
        entity.setPrivateCode(UUID.randomUUID().toString());
        entity.setUsername(dto.username.toLowerCase());

        return updateFromDTO(dto, entity);
    }

    public User updateFromDTO(UserDTO dto, User entity) {
        entity.setContactDetails(fromDTO(dto.contactDetails));

        if (dto.organisationRef != null) {
            entity.setOrganisation(organisationRepository.getOneByPublicId(dto.organisationRef));
        }
        if (CollectionUtils.isEmpty(dto.responsibilitySetRefs)) {
            entity.setResponsibilitySets(new HashSet<>());
        } else {
            entity.setResponsibilitySets(dto.responsibilitySetRefs.stream().map(ref -> responsibilitySetRepository.getOneByPublicId(ref)).collect(Collectors.toSet()));
        }

        return entity;
    }



    private ContactDetailsDTO toDTO(ContactDetails entity) {
        if (entity == null) {
            return null;
        }
        ContactDetailsDTO dto = new ContactDetailsDTO();
        dto.email = entity.getEmail();
        dto.firstName = entity.getFirstName();
        dto.lastName = entity.getLastName();
        dto.phone = entity.getPhone();
        return dto;
    }

    private ContactDetails fromDTO(ContactDetailsDTO dto) {
        if (dto == null) {
            return null;
        }
        ContactDetails entity = new ContactDetails();
        entity.setFirstName(dto.firstName);
        entity.setLastName(dto.lastName);
        entity.setEmail(dto.email);
        entity.setPhone(dto.phone);
        return entity;
    }

    private List<String> toRefList(Set<ResponsibilitySet> responsibilitySetSet) {
        if (responsibilitySetSet == null) {
            return null;
        }
        return responsibilitySetSet.stream().map(rs -> rs.getId()).collect(Collectors.toList());
    }


}
