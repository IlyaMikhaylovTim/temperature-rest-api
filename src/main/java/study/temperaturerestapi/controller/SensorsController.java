package study.temperaturerestapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import study.temperaturerestapi.exception.InvalidSensorBodyException;
import study.temperaturerestapi.exception.SensorAlreadyRegisteredException;
import study.temperaturerestapi.exception.response.SensorErrorResponse;
import study.temperaturerestapi.mapper.SensorMapper;
import study.temperaturerestapi.model.Sensor;
import study.temperaturerestapi.model.dto.SensorDTO;
import study.temperaturerestapi.service.SensorsService;
import study.temperaturerestapi.validator.SensorValidator;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sensors")
public class SensorsController {

    @Autowired
    private SensorsService sensorsService;

    @Autowired
    private SensorValidator sensorValidator;

    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> registerSensor(@RequestBody @Valid SensorDTO sensorDTO, BindingResult bindingResult) {
        sensorValidator.validate(sensorDTO, bindingResult);

        if (bindingResult.hasErrors()) {
            String message = bindingResult.getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(". "));

            throw new InvalidSensorBodyException(message);
        }

        Sensor sensor = SensorMapper.fromDTO(sensorDTO);
        sensorsService.registerSensor(sensor);

        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<SensorErrorResponse> handleException(SensorAlreadyRegisteredException exception) {
        SensorErrorResponse response = new SensorErrorResponse();

        response.setMessage(exception.getMessage());
        response.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    private ResponseEntity<SensorErrorResponse> handleException(InvalidSensorBodyException exception) {
        SensorErrorResponse response = new SensorErrorResponse();

        response.setMessage(exception.getMessage());
        response.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}
