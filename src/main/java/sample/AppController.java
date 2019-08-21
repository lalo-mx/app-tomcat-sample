package sample;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    private static final Logger LOG = LoggerFactory.getLogger(AppController.class);

    private final Random rand;
    
    public AppController() {
        this.rand = new Random();
    }
    @RequestMapping("/")
    public @ResponseBody void data(final HttpServletRequest request, final HttpServletResponse response) throws IOException, URISyntaxException {
        Path sample = null;
        try {
            LOG.trace("Requesting DATA");
            sample = Files.createTempFile("test", "test");
            fillFile(sample);
            try (
                    final InputStream input = Files.newInputStream(sample);
                    final OutputStream output = response.getOutputStream();
                ) {
                final byte[] bytes = new byte[1048576];
                boolean stillWriting = true;
                while (stillWriting) {
                    final int bytesRead = input.read(bytes);
                    stillWriting = bytesRead != -1;
                    if (stillWriting) {
                        output.write(bytes, 0, bytesRead);
                    }
                }
                output.flush();
            }
        } catch (final IOException ex) {
            LOG.error("failed: {}", ex.toString());
        } finally {
            if (sample != null) {
                Files.delete(sample);

            }
        }
    }

    private void fillFile(final Path sample) throws IOException {
        try (final FileWriter writesToFile = new FileWriter(sample.toFile())) {
            try (
                    BufferedWriter writer = new BufferedWriter(writesToFile)) {
                int line;
                for (int j = 0; j < 1000; j++) {
                    line = rand.nextInt(500000);
                    writer.write(line + "\n");
                }
            }
        } catch (final IOException e) {
            LOG.error("failed: {}", e.toString());
        }
    }
}
