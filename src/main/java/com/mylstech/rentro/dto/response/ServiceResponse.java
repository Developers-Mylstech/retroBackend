package com.mylstech.rentro.dto.response;

import com.mylstech.rentro.model.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {
    private Long serviceId;
    private ServiceFieldResponse ots;
    private ServiceFieldResponse mmc;
    private ServiceFieldResponse amcBasic;
    private ServiceFieldResponse amcGold;

    public ServiceResponse(Service service) {
        this.serviceId = service.getServiceId();
        
        if (service.getOts() != null) {
            this.ots = new ServiceFieldResponse(service.getOts());
        }
        
        if (service.getMmc() != null) {
            this.mmc = new ServiceFieldResponse(service.getMmc());
        }
        
        if (service.getAmcBasic() != null) {
            this.amcBasic = new ServiceFieldResponse(service.getAmcBasic());
        }
        
        if (service.getAmcGold() != null) {
            this.amcGold = new ServiceFieldResponse(service.getAmcGold());
        }
    }
}
