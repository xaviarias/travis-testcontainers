import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestNetwork {
    private static final Logger logger = LoggerFactory.getLogger(TestNetwork.class);

    @Test
    public void yay() throws IOException {
        try (
                final Network network = Network.newNetwork();

                final GenericContainer foo = new GenericContainer()
                        .withLogConsumer(new Slf4jLogConsumer(logger))
                        .withNetwork(network)
                        .withNetworkAliases("foo")
                        .withExposedPorts(8080)
                        .withCommand("/bin/sh", "-c", "while true ; do printf 'HTTP/1.1 200 OK\\n\\nyay' | nc -l -p 8080; done")
        ) {
            foo.start();

            final InputStream inputStream =
                    new URL("http://localhost:" + foo.getMappedPort(8080)).openStream();

            final List<String> yay = IOUtils.readLines(inputStream, StandardCharsets.UTF_8);
            Assert.assertEquals("yay", yay.get(0));

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
