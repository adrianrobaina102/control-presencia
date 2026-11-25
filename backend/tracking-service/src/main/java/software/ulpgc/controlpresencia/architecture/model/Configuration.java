package software.ulpgc.controlpresencia.architecture.model;

import com.google.gson.annotations.SerializedName;

public record Configuration (
        @SerializedName("broker-url") String brokerUrl,
        @SerializedName("broker-token") String brokerToken,
        @SerializedName("employee-service-url") String employeeServiceUrl,
        @SerializedName("building-service-url") String buildingServiceUrl
) {}