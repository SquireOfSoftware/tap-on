//package com.squireofsoftware.peopleproject.entities;
//
//import lombok.*;
//
//import javax.persistence.*;
//
//@Entity
//@Builder
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "t_servicecomponent_person")
//public class PersonServiceComponent {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Integer id;
//    @ManyToOne
//    @PrimaryKeyJoinColumn(name = "service_component_id")
//    private Integer serviceComponentId;
//    @ManyToOne
//    @PrimaryKeyJoinColumn(name = "person_id")
//    private Integer personId;
//    @ManyToOne
//    @PrimaryKeyJoinColumn(name = "service_id")
//    private Integer serviceId;
//}
