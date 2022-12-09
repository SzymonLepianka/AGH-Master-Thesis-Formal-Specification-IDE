package sl.fside.services;

import sl.fside.exceptions.*;

public interface IResourceService {

    String getText(String name) throws KeyNotFoundException;

}
