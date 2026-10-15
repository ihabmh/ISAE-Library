package exporters;

import java.io.IOException;

public interface Exporter {
    void export(Object data, String filename) throws IOException;
}
