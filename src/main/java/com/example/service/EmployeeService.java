package com.example.service;

import com.example.soapwebservice.interfaces.SoapWebServiceExampleHttp;
import org.apache.camel.component.cxf.jaxws.CxfEndpoint;
import org.apache.camel.component.cxf.spring.jaxws.CxfSpringEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private String EMPLOYEE_ENDPOINT = "http://localhost:2026/soapServiceExample";

    @Bean
    public CxfEndpoint AddEmployeeEndpoint() {
        CxfSpringEndpoint endpoint = new CxfSpringEndpoint();
        endpoint.setServiceClass(SoapWebServiceExampleHttp.class);
        endpoint.setAddress(EMPLOYEE_ENDPOINT);
        endpoint.setWsdlURL("http://localhost:2026/soapServiceExample/employees.wsdl");
        endpoint.setDefaultOperationName("addEmployee");
        return endpoint;
    }

}
