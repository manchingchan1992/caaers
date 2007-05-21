package gov.nih.nci.cabig.caaers.domain.notification;

import gov.nih.nci.cabig.ctms.domain.AbstractMutableDomainObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
/**
 * This class contains the details of the notification, that is to be send
 * out.
 * 
 * @author Biju Joseph
 *
 */

@Entity
@Table(name = "SCHEDULED_NOTIFICATIONS")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name = "dtype",
    discriminatorType = DiscriminatorType.STRING
)
@DiscriminatorValue("dtype")
@GenericGenerator(name = "id-generator", strategy = "native",
    parameters = {
        @Parameter(name = "sequence", value = "scheduled_notifications_id_seq")
    }
)
public  class ScheduledNotification extends AbstractMutableDomainObject  implements Serializable{
	
	
	protected DeliveryStatus deliveryStatus;
	
	@Column(name="CREATED_ON")
	protected Date createdOn;
	
	@Column(name="SCHEDULED_ON")
	protected Date scheduledOn;
	
	private PlannedNotification planedNotificaiton;

	byte[] body;
	
	public ScheduledNotification(){
		deliveryStatus = DeliveryStatus.CREATED;
	}
	
	@Type(type = "deliveryStatus")
    @Column(name = "DELIVERY_STATUS_CODE")
	public DeliveryStatus getDeliveryStatus() {
		return deliveryStatus;
	}
	public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Date getScheduledOn() {
		return scheduledOn;
	}
	public void setScheduledOn(Date scheduledOn) {
		this.scheduledOn = scheduledOn;
	}

	/**
	 * @return the planedNotificaiton
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="plnf_id", nullable=false)
	public PlannedNotification getPlanedNotificaiton() {
		return planedNotificaiton;
	}

	/**
	 * @param planedNotificaiton the planedNotificaiton to set
	 */
	public void setPlanedNotificaiton(PlannedNotification planedNotificaiton) {
		this.planedNotificaiton = planedNotificaiton;
	}

	/**
	 * @return the bodyContent
	 */
	@Lob
	public byte[] getBody() {
		return body;
	}

	/**
	 * @param bodyContent the bodyContent to set
	 */
	public void setBody(byte[] bodyContent) {
		this.body = bodyContent;
	}
	
}
