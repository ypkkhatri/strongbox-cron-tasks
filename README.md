[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/strongbox/strongbox-cron-tasks?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![Build Status](http://dev.carlspring.org/status/jenkins/strongbox-webapp)](https://dev.carlspring.org/jenkins/view/strongbox/job/strongbox-cron-tasks/)

# Strongbox Cron Tasks
An API for configuring Java/Groovy cron tasks on RESTful services

## Getting Started

This guidelines will let you know how to integrate your own custom cron task using API either its in Java or in Groovy

### RESTful URLs
* PUT    - http://example.com/config/crontasks/crontask (To create new cron task)
* DELETE - http://example.com/config/crontasks/crontask?name=MyCron (To delete existing cron task)
* GET    - http://example.com/config/crontasks/crontask?name=MyCron (To get cron task by its name)
* GET    - http://example.com/config/crontasks/ (To get list of all configured cron tasks)
* PUT    - http://example.com/config/crontasks/crontask/groovy/?cronName=MyCron (To upload groovy script for its cron task)
* GET    - http://example.com/config/crontasks/groovy/names (To get list of all configured groovy scripts names)

### Java Cron Task
To create a java based cron task you need to extend a class org.carlspring.strongbox.crontask.api.jobs.JavaCronJob and implements an abstract method executeInternal()

```
public class MyTask
        extends JavaCronJob
{

    private final Logger logger = LoggerFactory.getLogger(MyTask.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)
            throws JobExecutionException
    {
        logger.debug("My Rest Cron Task is working");
    }
}
```
### Groovy Cron Task
bla bla bla

## Built With

* Maven

## Authors

* **Yougeshwar Khatri** - *Initial work* - [ypkkhatri](https://github.com/ypkkhatri)
* **Martin Todorov** - [carlspring](https://github.com/carlspring)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the Apache License - see the [LICENSE.md](LICENSE.md) file for details
