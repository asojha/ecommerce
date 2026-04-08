package com.ecommerce.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("SUBSCRIPTION")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SubscriptionProduct extends Product {

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle")
    private BillingCycle billingCycle;

    /** Number of free trial days; null means no trial. */
    @Column(name = "trial_days")
    private Integer trialDays;

    public enum BillingCycle {
        WEEKLY, MONTHLY, QUARTERLY, ANNUAL
    }
}
