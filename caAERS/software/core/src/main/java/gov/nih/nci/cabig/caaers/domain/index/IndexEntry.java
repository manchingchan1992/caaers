package gov.nih.nci.cabig.caaers.domain.index;

import gov.nih.nci.cabig.caaers.domain.UserGroupType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

 
/**
 * Will hold the accessible objects for a role. 
 * @author: Biju Joseph
 */



public class IndexEntry {

    /** The role. */
    private List<UserGroupType> roles;
    
    /** The entity ids. */
    private Integer entityId;

    /**
     * Instantiates a new index entry.
     *
     * @param entityId the entity ID
     */
    public IndexEntry(Integer entityId){

        this.entityId = entityId;
        this.roles = new ArrayList<UserGroupType>();
    }

    public List<UserGroupType> getRoles() {
        return roles;
    }

    public void setRoles(List<UserGroupType> roles) {
        this.roles = roles;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public void addRole(UserGroupType... roles){
        for(UserGroupType role : roles)  {
            if(!this.roles.contains(role)) this.roles.add(role);
        }
    }

    public void addRole(List<UserGroupType> roles){
        addRole(roles.toArray(new UserGroupType[]{}));
    }

    public boolean hasAnyOfTheRoles(UserGroupType... roles){
        for(UserGroupType role : roles) if(hasRole(role)) return true;
        return false;
    }

    public boolean hasRole(UserGroupType role){
        return roles.contains(role);
    }
    public boolean  hasRoles()   {
        return !roles.isEmpty();
    }

    public boolean isAllSiteOrAllStudy(){
        return Integer.MIN_VALUE == entityId;
    }

    /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
    @Override
    public String toString() {
        return String.valueOf(entityId) + ", [" + String.valueOf(roles) + "]\r\n";
    }
}

