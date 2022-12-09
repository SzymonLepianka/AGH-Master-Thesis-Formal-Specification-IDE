package sl.fside.services;

import bgs.formalspecificationide.exceptions.*;

public interface IResourceService {

    String getText(String name) throws KeyNotFoundException;

}
