package hlf.ssconvert.ssconvert;

import java.util.Map;

public interface SequenceDs {
    Iterable<Map<String, Object>> read() throws Exception;
}
