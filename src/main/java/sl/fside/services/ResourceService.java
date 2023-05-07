package sl.fside.services;

import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import com.google.inject.*;
import sl.fside.exceptions.*;

import java.io.*;
import java.util.*;

public class ResourceService implements IResourceService {

    @SuppressWarnings("FieldCanBeLocal")
    private final String textsFile = "/sl/fside/texts/texts.json";
    private final LoggerService loggerService;
    private final ObjectMapper objectMapper;
    private HashMap<String, String> textsDictionary = new HashMap<>();

    @Inject
    ResourceService(LoggerService loggerService) {
        this.loggerService = loggerService;
        objectMapper = new ObjectMapper();
        loadTexts();
    }

    @Override
    public String getText(String name) throws KeyNotFoundException {
        var value = textsDictionary.get(name);
        if (value == null) throw new KeyNotFoundException();
        return value;
    }

    private void loadTexts() {
        try {
            textsDictionary = objectMapper.readValue(getClass().getResourceAsStream(textsFile), new TypeReference<>() {
            });
        } catch (IOException e) {
            loggerService.logDebug("Couldn't load resource \"Texts\"");
        }

        loggerService.logDebug("Loaded resource \"Texts\"");
    }
}
