package ge.pozdniakov.project3.services;

import ge.pozdniakov.project3.models.Measurement;
import ge.pozdniakov.project3.repositories.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MeasurementService {

    private final MeasurementRepository measurementRepository;

    @Autowired
    public MeasurementService(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    public List<Measurement> getAllMeasurements(){
        return measurementRepository.findAll();
    }

    public int getRainingDaysCount(){
        return measurementRepository.countAllByRainingIsTrue();
    }

    @Transactional
    public void save(Measurement measurement){
        measurementRepository.save(measurement);
    }
}
