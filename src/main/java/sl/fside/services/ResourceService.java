package sl.fside.services;

import sl.fside.exceptions.*;
import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import com.google.inject.*;

import java.io.*;
import java.util.*;

public class ResourceService implements IResourceService {

    @SuppressWarnings("FieldCanBeLocal")
    private final String textsFile = "/sl/fside/texts/texts.json";

    private HashMap<String, String> textsDictionary = new HashMap<>();
    private final LoggerService loggerService;

    private final ObjectMapper objectMapper;

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
            textsDictionary = objectMapper.readValue(getClass().getResourceAsStream(textsFile), new TypeReference<>() {});
        } catch (IOException e) {
            loggerService.logDebug("Couldn't load resource \"Texts\"");
        }

        loggerService.logDebug("Loaded resource \"Texts\"");
    }
}
