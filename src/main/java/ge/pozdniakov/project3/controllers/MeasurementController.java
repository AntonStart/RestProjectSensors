package ge.pozdniakov.project3.controllers;

import ge.pozdniakov.project3.dto.MeasurementDTO;
import ge.pozdniakov.project3.models.Measurement;
import ge.pozdniakov.project3.services.MeasurementService;
import ge.pozdniakov.project3.services.SensorService;
import ge.pozdniakov.project3.util.ErrorResponse;
import ge.pozdniakov.project3.util.MeasurementNotCreatedException;
import ge.pozdniakov.project3.util.MeasurementNotFoundException;
import ge.pozdniakov.project3.util.SensorNotFoundException;
import jakarta.validation.Valid;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;
    private final SensorService sensorService;
    private final ModelMapper modelMapper;

    @Autowired
    public MeasurementController(MeasurementService measurementService, SensorService sensorService, ModelMapper modelMapper) {
        this.measurementService = measurementService;
        this.sensorService = sensorService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/add")
    public ResponseEntity<HttpStatus> registerMeasurement(@RequestBody @Valid MeasurementDTO measurementDTO,
                                                     BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            StringBuilder errorMsg = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error: errors) {
                errorMsg.append(error.getField())
                        .append(" - ")
                        .append(error.getDefaultMessage())
                        .append(";");
            }
            throw new MeasurementNotCreatedException(errorMsg.toString());
        }
        if (sensorService.findSensorByName(measurementDTO.getSensor()).equals(null))
            throw new SensorNotFoundException("Sensor do not register in DB");
        measurementService.save(convertToMeasurement(measurementDTO));

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("")
    public List<MeasurementDTO> showAll() throws IOException {
        /*List<Double> y = measurementService.getAllMeasurements().stream().map(Measurement::getValue).collect(Collectors.toList());
        List<Integer> x = IntStream.rangeClosed(1, measurementService.getAllMeasurements().size())
                .boxed().collect(Collectors.toList());
        XYChart chart = QuickChart.getChart("Temperature","days","t","y(x)",x,y);
        try {
            BitmapEncoder.saveBitmap(chart, "C:\\Users\\79533\\Desktop\\1.png", BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return measurementService.getAllMeasurements().stream().map(this::convertToMeasurementDTO).collect(Collectors.toList());
    }

    @GetMapping("/rainyDaysCount")
    public int allRainyDays(){
        return measurementService.getRainingDaysCount();
    }

    private Measurement convertToMeasurement(MeasurementDTO measurementDTO) {

        Measurement measurement = new Measurement();
        measurement.setValue(measurementDTO.getValue());
        measurement.setRaining(measurementDTO.isRaining());
        measurement.setSensor(sensorService.findSensorByName(measurementDTO.getSensor()));
        measurement.setMeasurementTime(LocalDateTime.now());

        return measurement;
    }

    private MeasurementDTO convertToMeasurementDTO(Measurement measurement) {
        MeasurementDTO measurementDTO = modelMapper.map(measurement, MeasurementDTO.class);
        measurementDTO.setSensor(measurement.getSensor().getName());
        return measurementDTO;
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(MeasurementNotCreatedException measurementNotCreatedException){
        ErrorResponse personErrorResponse = new ErrorResponse(
                measurementNotCreatedException.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<ErrorResponse>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(MeasurementNotFoundException measurementNotFoundException){
        ErrorResponse personErrorResponse = new ErrorResponse(
                measurementNotFoundException.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<ErrorResponse>(personErrorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(SensorNotFoundException sensorNotFoundException){
        ErrorResponse sensorErrorResponse = new ErrorResponse(
                "Sensor with this name was not found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<ErrorResponse>(sensorErrorResponse, HttpStatus.NOT_FOUND);
    }


}
