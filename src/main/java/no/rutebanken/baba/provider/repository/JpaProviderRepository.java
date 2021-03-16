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

package no.rutebanken.baba.provider.repository;


import no.rutebanken.baba.provider.domain.ChouetteInfo;
import no.rutebanken.baba.provider.domain.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
@Transactional
public class JpaProviderRepository implements ProviderRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Collection<Provider> getProviders() {
        return this.entityManager.createQuery("SELECT p FROM Provider p order by p.id", Provider.class).setHint("org.hibernate.cacheable", Boolean.TRUE).getResultList();
    }

	public Optional<Provider> getProviderByReferential(String referential) {
		List<Provider> providers = this.entityManager.createQuery("SELECT p FROM Provider p where p.chouetteInfo.referential=:referential", Provider.class)
				.setParameter("referential", referential)
				.getResultList();
		return !providers.isEmpty() ? Optional.of(providers.get(0)) : Optional.empty();
	}

	public void updateMobiitiIdByName(String name, Long mobiitiId) {
		this.entityManager.createNativeQuery("update provider set mobiiti_id=:mobiitiId where name=:name")
				.setParameter("mobiitiId", mobiitiId)
				.setParameter("name", name)
				.executeUpdate();
	}

	public void updateChouetteInfo(String cuser, String codeIdfm, String nameNetexStopIdfm, String nameNetexOffreIdfm) {
		this.entityManager.createNativeQuery("update chouette_info set code_idfm=:codeIdfm, name_netex_stop_idfm=:nameNetexStopIdfm, name_netex_offre_idfm=:nameNetexOffreIdfm where cuser=:cuser")
				.setParameter("cuser", cuser)
				.setParameter("codeIdfm", codeIdfm)
				.setParameter("nameNetexStopIdfm", nameNetexStopIdfm)
				.setParameter("nameNetexOffreIdfm", nameNetexOffreIdfm)
				.executeUpdate();
	}

    @Override
    public Provider getProvider(Long id) {
        return entityManager.find(Provider.class, id);
    }

	@Override
	public void updateProvider(Provider provider) {
		entityManager.merge(provider);
	}

	@Override
	public Provider createProvider(Provider provider) {
		 entityManager.persist(provider);
		 return provider;
	}

	@Override
	public void deleteProvider(Long providerId) {
		Provider provider = getProvider(providerId);
		if(provider != null) {
			entityManager.remove(provider);
		}
	}
}
