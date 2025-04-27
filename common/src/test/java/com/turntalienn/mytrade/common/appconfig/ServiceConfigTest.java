package com.turntalienn.mytrade.common.appconfig;

import org.cfg4j.provider.ConfigurationProvider;
import org.junit.Test;
import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Properties;


public class ServiceConfigTest {

    @Test
    public void test_load() throws URISyntaxException {
        URI uri = ServiceConfigTest.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        ConfigurationProvider provider = ServiceConfig.load(Paths.get(uri).toString(), "test");
        Properties properties = provider.allConfigurationAsProperties();
        assertEquals(50051, properties.get("grpc.server.port"));
    }

}
