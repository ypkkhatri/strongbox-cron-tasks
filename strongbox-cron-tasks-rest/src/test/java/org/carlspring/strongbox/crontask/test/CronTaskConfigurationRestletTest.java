package org.carlspring.strongbox.crontask.test;

import org.carlspring.strongbox.crontask.configuration.CronTasksConfig;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { CronTasksConfig.class })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class })
@FixMethodOrder(MethodSorters.JVM)
@Ignore
public class CronTaskConfigurationRestletTest
{

    @Test
    public void testFoo()
    {
    }
}
