package uz.solarnature.solarnaturebot.domain.enumeration.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentForm implements GeneralType {

    CASH("doc.payment.form.cash"),
    LEASING("doc.payment.form.leasing"),
    TRANSFER("doc.payment.form.transfer");

    private final String titleKeyword;

}
