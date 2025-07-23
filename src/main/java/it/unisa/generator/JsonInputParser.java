package it.unisa.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Map;
import java.util.List;

public class JsonInputParser {
    public static Map<String, List<String>> parse(String path) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(path), Map.class);
    }
}

