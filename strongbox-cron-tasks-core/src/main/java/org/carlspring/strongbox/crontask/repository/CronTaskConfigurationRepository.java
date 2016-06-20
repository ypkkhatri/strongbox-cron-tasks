package org.carlspring.strongbox.crontask.repository;

import org.carlspring.strongbox.crontask.domain.CronTaskConfiguration;
import org.carlspring.strongbox.data.repository.OrientRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Yougeshwar
 */
@Transactional
public interface CronTaskConfigurationRepository
        extends OrientRepository<CronTaskConfiguration>
{

    CronTaskConfiguration findByName(String name);
}
