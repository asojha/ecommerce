package com.ecommerce.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@DiscriminatorValue("SUBSCRIPTION")
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Schema(description = "A recurring-billing subscription product. Inherits all standard product fields.")
public class SubscriptionProduct extends Product {

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle")
    @Schema(description = "How often the customer is charged", example = "MONTHLY",
            allowableValues = {"WEEKLY", "MONTHLY", "QUARTERLY", "ANNUAL"},
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BillingCycle billingCycle;

    @Column(name = "trial_days")
    @Schema(description = "Number of free trial days before billing begins; null means no trial", example = "30")
    private Integer trialDays;

    public enum BillingCycle {
        WEEKLY, MONTHLY, QUARTERLY, ANNUAL
    }
}
