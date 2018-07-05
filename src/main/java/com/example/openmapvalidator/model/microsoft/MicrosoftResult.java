package com.example.openmapvalidator.model.microsoft;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class MicrosoftResult {
    @JsonIgnore
    private String authenticationResultCode;
    @JsonIgnore
    private String brandLogoUri;
    @JsonIgnore
    private String copyright;

    @JsonIgnore
    private String statusCode;

    @JsonIgnore
    private String statusDescription;

    @JsonIgnore
    private String traceId;

    private List<ResourceSet> resourceSets;

    public static void main(String[] args) {
        //new MicrosoftResult().getResourceSets().get(0).
    }

    public List<ResourceSet> getResourceSets() {
        return resourceSets;
    }

    public void setResourceSets(List<ResourceSet> resourceSets) {
        this.resourceSets = resourceSets;
    }

    public String getAuthenticationResultCode() {
        return authenticationResultCode;
    }

    public void setAuthenticationResultCode(String authenticationResultCode) {
        this.authenticationResultCode = authenticationResultCode;
    }

    public String getBrandLogoUri() {
        return brandLogoUri;
    }

    public void setBrandLogoUri(String brandLogoUri) {
        this.brandLogoUri = brandLogoUri;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public static class ResourceSet {
        @JsonIgnore
        private String estimatedTotal;
        private List<Resource> resources;

        public String getEstimatedTotal() {
            return estimatedTotal;
        }

        public void setEstimatedTotal(String estimatedTotal) {
            this.estimatedTotal = estimatedTotal;
        }

        public List<Resource> getResources() {
            return resources;
        }

        public void setResources(List<Resource> resources) {
            this.resources = resources;
        }
    }

    public static class Resource {
        @JsonIgnore
        private String __type;

        private String isPrivateResidence;

        @JsonIgnore
        private List<AddressAtLocation> addressOfLocation;
        @JsonIgnore
        private List<NaturalPOIAtLocation> naturalPOIAtLocation;

        private List<BusinessesAtLocation> businessesAtLocation;

        public List<NaturalPOIAtLocation> getNaturalPOIAtLocation() {
            return naturalPOIAtLocation;
        }

        public void setNaturalPOIAtLocation(List<NaturalPOIAtLocation> naturalPOIAtLocation) {
            this.naturalPOIAtLocation = naturalPOIAtLocation;
        }

        public String getIsPrivateResidence() {
            return isPrivateResidence;
        }

        public void setIsPrivateResidence(String isPrivateResidence) {
            this.isPrivateResidence = isPrivateResidence;
        }

        public String get__type() {
            return __type;
        }

        public void set__type(String __type) {
            this.__type = __type;
        }

        public List<AddressAtLocation> getAddressOfLocation() {
            return addressOfLocation;
        }

        public void setAddressOfLocation(List<AddressAtLocation> addressOfLocation) {
            this.addressOfLocation = addressOfLocation;
        }

        public List<BusinessesAtLocation> getBusinessesAtLocation() {
            return businessesAtLocation;
        }

        public void setBusinessesAtLocation(List<BusinessesAtLocation> businessesAtLocation) {
            this.businessesAtLocation = businessesAtLocation;
        }
    }

    private static class AddressAtLocation {
    }

    public static class BusinessesAtLocation {
        @JsonIgnore
        private BusinessAddress businessAddress;

        private BusinessInfo businessInfo;

        public BusinessAddress getBusinessAddress() {
            return businessAddress;
        }

        public void setBusinessAddress(BusinessAddress businessAddress) {
            this.businessAddress = businessAddress;
        }

        public BusinessInfo getBusinessInfo() {
            return businessInfo;
        }

        public void setBusinessInfo(BusinessInfo businessInfo) {
            this.businessInfo = businessInfo;
        }
    }

    private static class BusinessAddress {
    }

    public static class BusinessInfo {
        private String entityName;
        private String url;
        private String phone;
        private String type;
        @JsonIgnore
        private List<OtherTypes> otherTypes;
        @JsonIgnore
        private String wheelchairAccessible;

        public String getWheelchairAccessible() {
            return wheelchairAccessible;
        }

        public void setWheelchairAccessible(String wheelchairAccessible) {
            this.wheelchairAccessible = wheelchairAccessible;
        }

        public String getEntityName() {
            return entityName;
        }

        public void setEntityName(String entityName) {
            this.entityName = entityName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<OtherTypes> getOtherTypes() {
            return otherTypes;
        }

        public void setOtherTypes(List<OtherTypes> otherTypes) {
            this.otherTypes = otherTypes;
        }
    }

    private static class OtherTypes {
    }

    private static class NaturalPOIAtLocation {
    }
}
