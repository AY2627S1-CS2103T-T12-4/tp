package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LogsCenterTest {
    @TempDir
    public Path temporaryFolder;

    private final Logger logger = Logger.getLogger("tassist.test." + getClass().getSimpleName());
    private final List<LogRecord> records = new ArrayList<>();
    private final Handler recordingHandler = new Handler() {
        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {}

        @Override
        public void close() {}
    };

    @AfterEach
    public void removeHandlers() {
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
            handler.close();
        }
    }

    @Test
    public void addFileHandler_writableFolder_writesLogFile() {
        Path logFile = temporaryFolder.resolve("tassist.log");

        assertTrue(LogsCenter.addFileHandler(logger, logFile.toString()));
        logger.warning("Saved to the log file");

        assertTrue(List.of(logger.getHandlers()).stream().anyMatch(handler -> handler instanceof FileHandler));
        assertTrue(Files.exists(temporaryFolder.resolve("tassist.log.0")));
    }

    @Test
    public void addFileHandler_unwritableLocation_warnsAndKeepsOtherHandlers() {
        logger.addHandler(recordingHandler);
        String logFile = temporaryFolder.resolve("missing-folder").resolve("tassist.log").toString();

        assertFalse(LogsCenter.addFileHandler(logger, logFile));

        assertEquals(List.of(recordingHandler), List.of(logger.getHandlers()));
        assertEquals(1, records.size());
        assertTrue(records.getFirst().getMessage().startsWith("Could not write the log file " + logFile));
    }

    @Test
    public void addFileHandler_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> LogsCenter.addFileHandler(null, "tassist.log"));
        assertThrows(NullPointerException.class, () -> LogsCenter.addFileHandler(logger, null));
    }
}
