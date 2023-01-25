package ge.pozdniakov.project3.repositories;

import ge.pozdniakov.project3.models.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement,Integer> {
    int countAllByRainingIsTrue();
}
