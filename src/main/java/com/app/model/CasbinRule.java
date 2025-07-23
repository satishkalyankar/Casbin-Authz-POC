package com.app.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Entity mapping for the casbin_rule table.
 */
@Entity
@Table(name = "casbin_rule")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CasbinRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ptype")
    private String ptype;

    @Column(name = "v0")
    private String v0;

    @Column(name = "v1")
    private String v1;

    @Column(name = "v2")
    private String v2;
}
