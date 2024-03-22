package uz.solarnature.solarnaturebot.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.solarnature.solarnaturebot.domain.enumeration.DocumentType;
import uz.solarnature.solarnaturebot.domain.enumeration.types.BuildingType;
import uz.solarnature.solarnaturebot.domain.enumeration.types.StationType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;
    private String email;
    private String address;
    private String others;

    private String companyName;
    private String tin;

    private LocalDate visitDate;
    private LocalDateTime contactTime;

    @Enumerated(EnumType.STRING)
    private StationType stationType;
    private String stationTypeOther;

    @Enumerated(EnumType.STRING)
    private BuildingType buildingType;
    private String buildingTypeOther;

    private String plan;
    private String paymentForm;
    private String commercialOffer;

    private String sesPower;
    private String panel;
    private String inverter;
    private String negotiationPlan;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private boolean isBusiness;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
