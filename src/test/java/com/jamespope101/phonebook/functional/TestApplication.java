package com.jamespope101.phonebook.functional;

import com.jamespope101.phonebook.ApplicationBoot;
import org.junit.rules.ExternalResource;

/**
 * Created by jpope on 07/02/2018.
 */
public class TestApplication extends ExternalResource {

    static {
        System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
    }

    private static String[] ARGS = {
        "--spring.profiles.active=end-to-end",
    };

    private static class ServiceInstanceHolder {
        private static final ApplicationBoot instance = new ApplicationBoot(ARGS);
    }

    private TestApplication() { }

    @Override
    protected void before() throws Throwable {
        ServiceInstanceHolder.instance.context();
    }

    public static TestApplication running() {
        ServiceInstanceHolder.instance.context();
        return new TestApplication();
    }

    public ApplicationBoot getApplication() {
        return ServiceInstanceHolder.instance;
    }
}
