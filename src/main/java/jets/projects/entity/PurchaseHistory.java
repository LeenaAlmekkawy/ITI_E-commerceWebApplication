package jets.projects.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PurchaseHistory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseHistory 
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_ID")
    private Long receiptId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_ID", nullable = false)
    private User user;

    @Column(name = "purchase_datetime")
    private LocalDateTime purchaseDatetime = LocalDateTime.now();

    @Column(name = "total_paid", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPaid;

    @Column(name = "receipt_file_URL", length = 255)
    private String receiptFileUrl;
}