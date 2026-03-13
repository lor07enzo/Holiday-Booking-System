package com.lorenzo.pelone.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hosts")
public class HostModel {
    @Id
    @Column(name = "host_code")
    private int hostCode;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @Column(name = "super_host")
    private boolean superHost;
}
