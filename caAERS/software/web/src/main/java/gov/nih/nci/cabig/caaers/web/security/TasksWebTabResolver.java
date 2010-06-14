package gov.nih.nci.cabig.caaers.web.security;

import gov.nih.nci.cabig.ctms.web.chrome.Task;

import java.util.Map;

/**
 * @author Ion C. Olaru
 *
 */
public class TasksWebTabResolver implements WebTabResolver {

    protected Map<String, String> objectPrivilegeMap;

    public String resolve(Object o) {
        return objectPrivilegeMap.get(((Task)o).getUrl());
    }

    public void setObjectPrivilegeMap(Map<String, String> objectPrivilegeMap) {
        this.objectPrivilegeMap = objectPrivilegeMap;
    }
}