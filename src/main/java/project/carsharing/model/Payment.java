package project.carsharing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
    @OneToOne
    @JoinColumn(nullable = false, name = "rental_id")
    private Rental rental;
    @Column(name = "session_url")
    @ToString.Exclude
    private String sessionUrl;
    @Column(name = "session_id")
    private String sessionId;
    @Column(nullable = false)
    private BigDecimal amount;
    
    public enum Status {
        PENDING,
        PAID,
        PAYMENT_ERROR,
        EXPIRED
    }
    
    public enum Type {
        PAYMENT,
        FINE
    }
}
