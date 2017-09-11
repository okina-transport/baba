package no.rutebanken.baba.organisation.repository;

import com.google.common.collect.Sets;
import no.rutebanken.baba.organisation.model.responsibility.ResponsibilityRoleAssignment;
import no.rutebanken.baba.organisation.model.responsibility.ResponsibilitySet;
import no.rutebanken.baba.organisation.model.responsibility.Role;
import no.rutebanken.baba.organisation.model.user.ContactDetails;
import no.rutebanken.baba.organisation.model.user.User;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;


public class UserRepositoryTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ResponsibilitySetRepository responsibilitySetRepository;

    @Autowired
    private EntityManager em;

    @Test
    public void testInsertUser() {

        User user = User.builder().withUsername("raffen").withPrivateCode("2").withOrganisation(defaultOrganisation).withContactDetails(minimalContactDetails()).build();
        User createdUser = userRepository.saveAndFlush(user);

        User fetchedUser = userRepository.getOne(createdUser.getPk());
        Assert.assertTrue(fetchedUser.getId().equals("User:2"));


    }

    protected ContactDetails minimalContactDetails() {
        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setEmail("valid@email.org");
        return contactDetails;
    }


    @Test
    public void testFindByResponsibilitySet() {
        Role role = roleRepository.save(new Role("testCode", "testRole"));
        ResponsibilityRoleAssignment responsibilityRoleAssignment =
                ResponsibilityRoleAssignment.builder().withPrivateCode("pCode").withResponsibleOrganisation(defaultOrganisation)
                        .withTypeOfResponsibilityRole(role).withCodeSpace(defaultCodeSpace).build();
        ResponsibilitySet responsibilitySet = new ResponsibilitySet(defaultCodeSpace, "pCode", "name", Sets.newHashSet(responsibilityRoleAssignment));

        responsibilitySet = responsibilitySetRepository.save(responsibilitySet);

        User userWithRespSet =
                userRepository.saveAndFlush(User.builder()
                                                    .withUsername("userWithRespSet").withPrivateCode("userWithRespSet")
                                                    .withOrganisation(defaultOrganisation)
                                                    .withResponsibilitySets(Sets.newHashSet(responsibilitySet))
                                                    .withContactDetails(minimalContactDetails())
                                                    .build());
        User userWithoutRespSet = userRepository.saveAndFlush(User.builder().withUsername("userWithoutRespSet").withPrivateCode("userWithoutRespSet").withOrganisation(defaultOrganisation).withContactDetails(minimalContactDetails()).build());
        List<User> usersWithRespSet = userRepository.findUsersWithResponsibilitySet(responsibilitySet);

        Assert.assertEquals(1, usersWithRespSet.size());
        Assert.assertTrue(usersWithRespSet.contains(userWithRespSet));
        Assert.assertFalse(usersWithRespSet.contains(userWithoutRespSet));

        userRepository.delete(usersWithRespSet);

        em.flush();
    }

}

