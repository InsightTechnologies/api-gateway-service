package com.miracle.gateway.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miracle.feature.response.FeatureResponse;

@RestController
@RequestMapping("/releasePlanning")
public class GatewayController {

	@GetMapping(value = "/gatewayService")
	public ResponseEntity<FeatureResponse> releaseFeatures(
			@RequestParam(value = "featureStates") List<Object> featureStates,
			@RequestParam(value = "storyStates") List<Object> storyStates,
			@RequestParam(value = "maxStoryPoints") Integer maxStoryPoints,
			@RequestParam(value = "filter") String filter, @RequestParam(value = "projectName") String projectName) {
		// Invoke Feature service to retrieve custom feature JSON
		// Invoke Release service to retrieve features to be released with efforts
		FeatureResponse response = new FeatureResponse();
		// send release feature JSON using FeatureResponse

		return new ResponseEntity<FeatureResponse>(response, HttpStatus.OK);
	}
}
