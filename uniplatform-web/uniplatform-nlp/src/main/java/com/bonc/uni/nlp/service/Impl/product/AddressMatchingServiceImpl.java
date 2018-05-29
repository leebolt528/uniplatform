package com.bonc.uni.nlp.service.Impl.product;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.bonc.text.sdk.client.TextAddressMatchingClient;
import com.bonc.uni.nlp.config.SystemConfig;
import com.bonc.uni.nlp.service.product.IAddressMatchingService;
import com.bonc.usdp.odk.common.string.StringUtil;
import com.bonc.usdp.odk.logmanager.LogManager;

/**
 * @author : GaoQiuyuer
 * @version: 2017年12月23日 下午6:33:15
 */
@Service
public class AddressMatchingServiceImpl implements IAddressMatchingService {

	private TextAddressMatchingClient client = TextAddressMatchingClient.getInstance(SystemConfig.PROCESSOR_DOMAIN);

	/**
	 * 从es数据库的目标地址中获取地址匹配的结果,多个源地址
	 */
	@Override
	public String getMultiAddressMatchResultFromEs(String sourceAddresses) {
		LogManager.Method("Enter the Method : AddressMatchingServiceImpl/getAddressMatchResultFromEs");

		if(StringUtil.isEmpty(sourceAddresses)) {
			return null;
		}
		
		String[] addresses = sourceAddresses.split("&&");
		List<String> adds = new ArrayList<>();
		for(String address : addresses) {
			adds.add(address);
		}
		
		String result = client.matchMultiAddressFromLib(adds, true);
		
		
		LogManager.Method("Out from the Method : AddressMatchingServiceImpl/getAddressMatchResultFromEs");
		return result;
	}

	/**
	 * 将excel表格中的目标地址存入es数据库中
	 */
	@Override
	public String upsertExcelDataToEs(String path, int column) {
		LogManager.Method("Enter the Method : AddressMatchingServiceImpl/upsertExcelDataToEs");

		String result = client.importExcelDataToLib(path, column);

		LogManager.Method("Out from the Method : AddressMatchingServiceImpl/upsertExcelDataToEs");
		return result;
	}

	/**
	 * 将MySQL中的目标地址存入es数据库中
	 * 
	 * @param tableName
	 * @param fieldName
	 * @return
	 */
	@Override
	public String upsertMySQLDataToEs(String tableName, String fieldName) {
		LogManager.Method("Enter the Method : AddressMatchingServiceImpl/upsertDataToEs");

		String result = client.importMySQLDataToLib(tableName, fieldName);

		LogManager.Method("Out from the Method : AddressMatchingServiceImpl/upsertDataToEs");
		return result;
	}

}
