package org.example.cargo29.repository;

import java.time.LocalDate;
import java.util.List;

import org.example.cargo29.entity.Cargo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CargoRepository extends JpaRepository<Cargo, Long> {

    @Query("SELECT c FROM Cargo c WHERE CONCAT(c.name, ' ', c.content, ' ', c.cityofdeparture, ' ', c.dateofdeparture, ' ', c.cityofarrival, ' ', c.dateofarrival) LIKE %?1%")
    List<Cargo> search(String keyword);

    List<Cargo> findAllByOrderByDateofarrivalAsc();

    List<Cargo> findAllByOrderByDateofarrivalDesc();

    @Query("SELECT DATE(c.dateofarrival) AS date, COUNT(c) AS count FROM Cargo c GROUP BY DATE(c.dateofarrival) ORDER BY DATE(c.dateofarrival)")
    List<Object[]> getHistogramData();

    @Query("SELECT COUNT(c) FROM Cargo c WHERE DATE(c.dateofarrival) = ?1")
    int countCargoByDate(LocalDate date);
}
