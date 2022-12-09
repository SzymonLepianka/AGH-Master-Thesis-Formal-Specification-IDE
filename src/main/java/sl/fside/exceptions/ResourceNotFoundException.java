package sl.fside.exceptions;

public class ResourceNotFoundException extends Exception {

    private final String resource;

    public ResourceNotFoundException(String resource) {
        this.resource = resource;
    }

    public String getResource() {
        return resource;
    }
}
