package com.sahaj.farecalcengine;

import com.opencsv.exceptions.CsvValidationException;
import com.sahaj.farecalcengine.data.TripDetails;
import com.sahaj.farecalcengine.rules.RuleExecutionService;
import com.sahaj.farecalcengine.utils.CSVDataReadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@Slf4j
public class Application {

	@Autowired
	RuleExecutionService ruleExecutionService;


	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			Arrays.stream(args).forEach(System.out::println);
            calculateFare(args,ctx);

		};
	}

	private void calculateFare(String[] args, ApplicationContext ctx) throws CsvValidationException, IOException {
		String fileWithAbsPath = null;
		if (args.length > 0) {
			System.out.println("Input File to be used is: "+args[0]);
			fileWithAbsPath = args[0];
    	}else{
			System.out.println("No command line arguments found.");
			return;
		}


		List<TripDetails> tripDetailsList = CSVDataReadUtil.getTravelInfoListFromCSVWithAbsPath(fileWithAbsPath);
		List<TripDetails> previousTrips = new ArrayList<>();
		for(TripDetails tripDetails : tripDetailsList){
			System.out.println("############################################################");

			TripDetails resTripDetails = ruleExecutionService.executeFareCalcRules(previousTrips, tripDetails);
			previousTrips.add(tripDetails);
			System.out.println("OUTPUT:: New Trip With Updated Fare:"+resTripDetails);


		}
	}

}