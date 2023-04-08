package org.keshav.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.keshav.exception.HolidayApiException;
import org.keshav.model.Holiday;
import org.keshav.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class CalenderController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private HolidayRepository holidayRepository;

	@Value("${holiday.api.url}")
	private String API_URL;

	@GetMapping("/{year}/{month}")
	public ResponseEntity<Map<String, Object>> getCalendar(@PathVariable int year, @PathVariable int month, @RequestParam(required = false) String type,@RequestParam(required = false) String country) {
		Map<String, Object> response = new HashMap<>();
		List<Map<String, Object>> calendarDates = new ArrayList<>();

		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 1);
		int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		Map<String, List<Map<String, String>>> holidaysMap = new HashMap<>();
		
		if(type != null && type.equals("personal")) {
			List<Holiday> personalHolidays = holidayRepository.findByType("personal");
		    for (Holiday holiday : personalHolidays) {
		        String dateStr = holiday.getDate().toString();
		        String name = holiday.getName();
		        String holidayType = holiday.getType();
		        Map<String, String> holidayMap = new HashMap<>();
		        holidayMap.put("name", name);
		        holidayMap.put("type", holidayType);
		        List<Map<String, String>> holidayList = holidaysMap.getOrDefault(dateStr, new ArrayList<>());
		        holidayList.add(holidayMap);
		        holidaysMap.put(dateStr, holidayList);
		    }
		}else {
			if(country == null) {
				country = "india";
			}

			// retrieve holiday list from API
			String url = API_URL+"&country="+country+"&year="+String.valueOf(year);
			if(type != null) {
				url = url + "&type=" + type;
			}
			HttpHeaders headers = new HttpHeaders();
			headers.set("X-Api-Key","YOUR_API_KEY");
			HttpEntity<String> entity = new HttpEntity<>("body", headers);
			ResponseEntity<String> apiResponse;
			try {
				apiResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			} catch (RestClientException e) {
				throw new HolidayApiException("Error fetching holiday data from API", e);
			}
			JSONArray holidaysArray = new JSONArray(apiResponse.getBody());
			for (int i = 0; i < holidaysArray.length(); i++) {
				JSONObject holiday = holidaysArray.getJSONObject(i);
				String dateStr = holiday.getString("date");
				String name = holiday.getString("name");
				String holidayType = holiday.getString("type");
				Map<String, String> holidayMap = new HashMap<>();
				holidayMap.put("name", name);
				holidayMap.put("type", holidayType);
				List<Map<String, String>> holidayList = holidaysMap.getOrDefault(dateStr, new ArrayList<>());
				holidayList.add(holidayMap);
				holidaysMap.put(dateStr, holidayList);
			}
		}

		for (int i = 1; i <= daysInMonth; i++) {
			Map<String, Object> dateObj = new HashMap<>();
			cal.set(year, month - 1, i);
			String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
			dateObj.put("date", dateStr);

			List<Map<String, String>> holidays = holidaysMap.getOrDefault(dateStr, new ArrayList<>());
			dateObj.put("holidays", holidays);

			calendarDates.add(dateObj);
		}

		response.put("calendarDates", calendarDates);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}

	@PostMapping("/holiday")
	public ResponseEntity<?> addHoliday(@RequestBody Holiday holiday) {
		try {
			Holiday savedHoliday = holidayRepository.save(holiday);
			return new ResponseEntity<>(savedHoliday,HttpStatus.OK);
		} catch (Exception e) {
			throw new HolidayApiException("Error saving Holiday Data to database", e);
		}
	}

	@ExceptionHandler(HolidayApiException.class)
	public ResponseEntity<String> handleHolidayApiException(HolidayApiException e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
}
