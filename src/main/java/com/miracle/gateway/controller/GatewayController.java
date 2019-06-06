package com.miracle.gateway.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miracle.api.service.utils.ReleaseUtil;
import com.miracle.api.service.utils.RetrieveReleaseFeatureRequest;
import com.miracle.common.api.bean.Feature;
import com.miracle.common.bean.APIMicroServiceBean;
import com.miracle.common.bean.FeatureWithEstimates;
import com.miracle.common.controller.APIMicroService;
import com.miracle.common.response.FeatureResponse;
import com.miracle.exception.APIFrameworkException;
import com.miracle.gateway.exception.GatewayErrorCode;

@RestController
@RequestMapping(value = "/masterBot/project")
public class GatewayController extends APIMicroService {
	@Autowired
	public ReleaseUtil releaseUtil;
	public static final Logger logger = LoggerFactory.getLogger(GatewayController.class);

	@Value("${masterbot.extractFeaturesEndpoint}")
	protected String extractFeaturesEndpoint;
	@Value("${masterbot.retrieveReleaseFeaturesEndpoint}")
	protected String retrieveReleaseFeaturesEndpoint;

	@PostMapping("/releasePlan")
	public List<FeatureWithEstimates> releasePlanningService(@RequestBody FeatureResponse featureResponse)
//			@RequestBody APIMicroServiceBean apiMicroServiceBean)
			throws APIFrameworkException {
		List<FeatureWithEstimates> releaseFeatures = null;
		try {
			if (featureResponse.isSuccess()) {
				APIMicroServiceBean apiMicroServiceBean = (APIMicroServiceBean) featureResponse.getObject();
				RetrieveReleaseFeatureRequest retrieveReleaseFeatureRequest = new RetrieveReleaseFeatureRequest();
				retrieveReleaseFeatureRequest.setCustomFeatures(retrieveFilteredFeatures(apiMicroServiceBean));
				retrieveReleaseFeatureRequest.setFilterType(apiMicroServiceBean.getFilterType());
				retrieveReleaseFeatureRequest.setMaxStoryPoint(apiMicroServiceBean.getMaxStoryPoint());
				retrieveReleaseFeatureRequest.setProjectName(apiMicroServiceBean.getProjectName());
				// retrieveReleaseFeatureRequest.setStoryStates(apiMicroServiceBean.getStoryStates());
				retrieveReleaseFeatureRequest.setStoryStates(apiMicroServiceBean.getStoryStateList());
				// retrieveReleaseFeatureRequest.setFeatureStates(apiMicroServiceBean.getFeatureStates());
				retrieveReleaseFeatureRequest.setFeatureStates(apiMicroServiceBean.getFeatureStateList());
				releaseFeatures = releaseUtil.retrieveReleaseFeatures(retrieveReleaseFeatureEndpoint(),
						retrieveReleaseFeatureRequest, commonUtil.getHeaderDetails(),
						commonUtil.getAcceptableMediaTypes());
			} else {
				// TODO error response
			}

//		} catch (APIFrameworkException gatewayServiceException) {
//			logger.error("Getting exception in release plan service , Exception Description :: "
//					+ gatewayServiceException.getMessage(), gatewayServiceException);
//			throw gatewayServiceException;
		} catch (Exception exception) {
			logger.error(
					"Getting exception in release plan service , Exception Description :: " + exception.getMessage());
			throw new APIFrameworkException(
					"Getting exception in release plan service , Exception Description :: " + exception.getMessage(),
					exception, GatewayErrorCode.GATEWAY_CONTROLLER_UNKNOWN_EXCEPTION);
		}
		return releaseFeatures;
	}

	// Invoker API_M1
	private List<Feature> retrieveFilteredFeatures(APIMicroServiceBean apiMicroServiceBean) throws Exception {

		List<Feature> filteredFeatures = releaseUtil.getUnorderedFeatures(getFilteredFeturesEndpoint(),
				apiMicroServiceBean, commonUtil.getHeaderDetails(), commonUtil.getAcceptableMediaTypes());
		return filteredFeatures;
	}

	private String getFilteredFeturesEndpoint() {
		StringBuilder url = new StringBuilder("");
		url.append(loadBalence_URLPrefix).append(extractFeaturesEndpoint);
		return url.toString();
	}

	private String retrieveReleaseFeatureEndpoint() {
		StringBuilder url = new StringBuilder("");
		url.append(loadBalence_URLPrefix).append(retrieveReleaseFeaturesEndpoint);
		return url.toString();
	}

}