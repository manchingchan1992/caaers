package gov.nih.nci.cabig.caaers.domain;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Embeddable;
import javax.persistence.AttributeOverrides;
import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.math.BigDecimal;

/**
 * @author Kulasekaran
 * @version 1.0
 */
@Entity
@Table (name="participant_histories")
@GenericGenerator(name="id-generator", strategy = "native",
    parameters = {
        @Parameter(name="sequence", value="seq_participant_histories_id")
    }
)
public class ParticipantHistory extends AbstractExpeditedReportSingleChild {
    private String baselinePerformanceStatus;
    private Measure height;
    private Measure weight;

    @AttributeOverrides( {
        @AttributeOverride(name = "quantity", column = @Column(name = "height")),
        @AttributeOverride(name = "unit", column = @Column(name = "height_unit"))
    } )
    public Measure getHeight() {
        if (height == null) setHeight(new Measure());
        return height;
    }

    public void setHeight(Measure height) {
        this.height = height;
    }

    @AttributeOverrides( {
        @AttributeOverride(name = "quantity", column = @Column(name = "weight")),
        @AttributeOverride(name = "unit", column = @Column(name = "weight_unit"))
    } )
    public Measure getWeight() {
        if (weight == null) setWeight(new Measure());
        return weight;
    }

    public void setWeight(Measure weight) {
        this.weight = weight;
    }

    public String getBaselinePerformanceStatus() {
        return baselinePerformanceStatus;
    }

    public void setBaselinePerformanceStatus(String baselinePerformance) {
        this.baselinePerformanceStatus = baselinePerformance;
    }
    
    @Transient
    public double getBodySurfaceArea(){
    	if(weight == null || height == null) return 0;
    	if(weight.quantity == null || height.quantity == null) return 0;
    	if(weight.unit == null || height.unit == null) return 0;
    	
    	double wt = (weight.unit.equalsIgnoreCase("Pound")) ? weight.quantity.doubleValue()/2.20462262185: weight.quantity.doubleValue();
    	double ht = (height.unit.equalsIgnoreCase("Inch"))  ? height.quantity.doubleValue() *  2.54 : height.quantity.doubleValue();
    	return Math.sqrt((wt * ht) / 3600);
    }
    
    @Embeddable
    public static class Measure {
        private BigDecimal quantity;
        private String unit;

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}

