package com.jabiseo.domain.certificate.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subject {

    @Id
    @Column(name = "subject_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int sequence;

    @ManyToOne
    @JoinColumn(name = "certificate_id", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Certificate certificate;

    private Subject(String name, int sequence, Certificate certificate) {
        this.name = name;
        this.sequence = sequence;
        this.certificate = certificate;
    }

    public static Subject of(String name, int sequence, Certificate certificate) {
        Subject subject = new Subject(name, sequence, certificate);
        certificate.addSubject(subject);
        return subject;
    }

}
