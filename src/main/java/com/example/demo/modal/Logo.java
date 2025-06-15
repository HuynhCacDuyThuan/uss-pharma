package com.example.demo.modal;



import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "logos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Logo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String imageUrl; 

}
