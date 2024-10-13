package org.example.ProjectTraninng.Common.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "warehouse_store")
public class WarehouseStore extends BaseEntity {


    @Column(name = "quantity", nullable = false)
    @NotNull(message = "Quantity is required")
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "medicineId", nullable = false)
    @JsonBackReference("medicine-warehouseStore")
    private Medicine medicine;

    @Column(name = "isDeleted" , nullable = false )
    @JsonIgnore
    @Builder.Default
    private boolean isDeleted = false;

}
