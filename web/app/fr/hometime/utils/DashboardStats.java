package fr.hometime.utils;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class DashboardStats implements Jsonable {
	private int toQuote = 0;
	private int toSend = 0;
	private int toTest = 0;
	
	private DashboardStats(int toQuote, int toSend, int toTest) {
		this.toQuote = toQuote;
		this.toSend = toSend;
		this.toTest = toTest;
	}
	
	public static List<DashboardStats> getStats() {
		return Arrays.asList(new DashboardStats(CustomerWatchHelper.findUnderOurResponsabilityFilteredByStatus(CustomerWatchHelper.CustomerWatchDetailedStatus.TO_QUOTE).size(),
												CustomerWatchHelper.findUnderOurResponsabilityFilteredByStatus(CustomerWatchHelper.CustomerWatchDetailedStatus.QUOTATION_TO_SEND_TO_CUSTOMER).size(),
												CustomerWatchHelper.findUnderOurResponsabilityFilteredByStatus(CustomerWatchHelper.CustomerWatchDetailedStatus.TESTING).size()));
	}

    @Override
    public JsonNode toJson() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("toQuote", toQuote)
        	.put("toSend", toSend)
        	.put("toTest", toTest);

        return json;
    }

}
