package com.miracle.gateway.controller;

import java.util.List;

import org.mongojack.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miracle.common.api.bean.APIMicroServiceBean;
import com.miracle.common.api.bean.Feature;
import com.miracle.common.controller.APIMicroService;
import com.miracle.common.database.mongo.MongoDBUtility;
import com.miracle.exception.GatewayServiceException;
import com.miracle.gateway.exception.GatewayErrorCode;
import com.miracle.release.bean.FeatureWithEstimates;
import com.miracle.release.bean.RetrieveReleaseFeatureRequest;
import com.miracle.release.util.ReleaseUtil;

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
	public List<FeatureWithEstimates> releasePlanningService(@RequestBody APIMicroServiceBean apiMicroServiceBean)throws GatewayServiceException
	{	List<FeatureWithEstimates> releaseFeatures = null;
			try	{
				RetrieveReleaseFeatureRequest retrieveReleaseFeatureRequest = new RetrieveReleaseFeatureRequest();
				retrieveReleaseFeatureRequest.setCustomFeatures(retrieveFilteredFeatures(apiMicroServiceBean));
				retrieveReleaseFeatureRequest.setFilterType(apiMicroServiceBean.getFilterType());
				retrieveReleaseFeatureRequest.setMaxStoryPoint(apiMicroServiceBean.getMaxStoryPoint());
				retrieveReleaseFeatureRequest.setProjectName(apiMicroServiceBean.getProjectName());
				//retrieveReleaseFeatureRequest.setStoryStates(apiMicroServiceBean.getStoryStates());
				retrieveReleaseFeatureRequest.setStoryStates(mongoDBUtility.getStoryStates(apiMicroServiceBean.getStoryStates()));
			//	retrieveReleaseFeatureRequest.setFeatureStates(apiMicroServiceBean.getFeatureStates());
				retrieveReleaseFeatureRequest.setFeatureStates(mongoDBUtility.getFeatureStates(apiMicroServiceBean.getFeatureStates()));
			    releaseFeatures = releaseUtil.retrieveReleaseFeatures(retrieveReleaseFeatureEndpoint(), retrieveReleaseFeatureRequest, 
						commonUtil.getHeaderDetails(), commonUtil.getAcceptableMediaTypes());
		}catch(GatewayServiceException gatewayServiceException){
			logger.error("Getting exception in release plan service , Exception Description :: "+gatewayServiceException.getMessage(),gatewayServiceException);
			throw gatewayServiceException;
		}catch (Exception exception) {
			logger.error("Getting exception in release plan service , Exception Description :: "+exception.getMessage());
			throw new GatewayServiceException("Getting exception in release plan service , Exception Description :: "+exception.getMessage(),
					exception,GatewayErrorCode.GATEWAY_CONTROLLER_UNKNOWN_EXCEPTION);
		}		
		return releaseFeatures;
	}

	//Invoker API_M1
	private List<Feature> retrieveFilteredFeatures(APIMicroServiceBean apiMicroServiceBean)throws Exception
	{
		
		List<Feature> filteredFeatures = releaseUtil.getUnorderedFeatures(getFilteredFeturesEndpoint(), apiMicroServiceBean,  
				commonUtil.getHeaderDetails(), commonUtil.getAcceptableMediaTypes());		
		return filteredFeatures;
	}
	
	private String getFilteredFeturesEndpoint()
	{
		StringBuilder url = new StringBuilder("");
		url.append(loadBalence_URLPrefix).append(extractFeaturesEndpoint);
		return url.toString();
	}
	private String retrieveReleaseFeatureEndpoint()
	{
		StringBuilder url = new StringBuilder("");
		url.append(loadBalence_URLPrefix).append(retrieveReleaseFeaturesEndpoint);
		return url.toString();
	}

	
}