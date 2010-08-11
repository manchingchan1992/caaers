package gov.nih.nci.cabig.caaers.event;

import gov.nih.nci.cabig.ctms.domain.DomainObject;

import org.acegisecurity.Authentication;

public class OrganizationModificationEvent extends EntityModificationEvent {

    public OrganizationModificationEvent(Authentication authentication, DomainObject entity) {
        super(authentication, entity, EventType.ORGANIZATION);
    }
}
