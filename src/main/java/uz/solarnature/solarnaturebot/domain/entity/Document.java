package uz.solarnature.solarnaturebot.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.solarnature.solarnaturebot.domain.enumeration.types.*;

import java.time.LocalDate;

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
    private String contactTime;
    private String commercialOffer;

    @Enumerated(EnumType.STRING)
    private StationType stationType;
    private String stationTypeOther;

    @Enumerated(EnumType.STRING)
    private BuildingType buildingType;
    private String buildingTypeOther;

    private String plan;

    @Enumerated(EnumType.STRING)
    private PaymentForm paymentForm;
    private String sesPower;

    @Enumerated(EnumType.STRING)
    private PanelBrands panel;
    private String panelOther;

    @Enumerated(EnumType.STRING)
    private InverterBrands inverter;
    private String inverterOther;

    private String negotiationPlan;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private boolean isBusiness;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private FillingType fillingType;

}
