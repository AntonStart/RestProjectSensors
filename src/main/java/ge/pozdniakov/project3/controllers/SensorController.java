package ge.pozdniakov.project3.controllers;

import ge.pozdniakov.project3.dto.SensorDTO;
import ge.pozdniakov.project3.models.Sensor;
import ge.pozdniakov.project3.services.SensorService;
import ge.pozdniakov.project3.util.ErrorResponse;
import ge.pozdniakov.project3.util.SensorNotCreatedException;
import ge.pozdniakov.project3.util.SensorNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sensors")
public class SensorController {

    private final SensorService sensorService;
    private final ModelMapper modelMapper;

    @Autowired
    public SensorController(SensorService sensorService, ModelMapper modelMapper) {
        this.sensorService = sensorService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> registerSensor(@RequestBody @Valid SensorDTO sensorDTO,
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
            throw new SensorNotCreatedException(errorMsg.toString());
        }
        sensorService.save(convertToSensor(sensorDTO));

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/all")
    public List<SensorDTO> getAll(){
        return sensorService.findAll().stream().map(this::convertToSensorDTO).collect(Collectors.toList());
    }

    private Sensor convertToSensor(SensorDTO sensorDTO) {
        Sensor sensor = modelMapper.map(sensorDTO, Sensor.class);
        return sensor;
    }

    private SensorDTO convertToSensorDTO(Sensor sensor) {
        SensorDTO sensorDTO = modelMapper.map(sensor, SensorDTO.class);
        return sensorDTO;
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(SensorNotFoundException sensorNotFoundException){
        ErrorResponse sensorErrorResponse = new ErrorResponse(
                "Sensor with this name was not found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<ErrorResponse>(sensorErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(SensorNotCreatedException sensorNotCreatedException){
        ErrorResponse sensorErrorResponse = new ErrorResponse(
                sensorNotCreatedException.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<ErrorResponse>(sensorErrorResponse, HttpStatus.BAD_REQUEST);
    }
}
