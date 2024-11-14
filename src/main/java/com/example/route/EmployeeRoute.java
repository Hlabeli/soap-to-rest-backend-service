package com.example.route;

import com.example.soapwebservice.interfaces.AddEmployeeRequest;
import com.example.soapwebservice.interfaces.AddEmployeeResponse;
import com.example.soapwebservice.interfaces.EmployeeInfo;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.cxf.message.MessageContentsList;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {

//        restConfiguration()
//                .component("netty-http")
//                .host("localhost").bindingMode(RestBindingMode.json)
//                // and output using pretty print
//                .dataFormatProperty("prettyPrint", "true")
//                // setup context path and port number that netty will use
//                .contextPath("/").port(8090)
//                // add OpenApi api-doc out of the box
//                .apiContextPath("/api-doc")
//                .apiProperty("api.title", "Exit Gate Management API")
//                .apiProperty("api.version", "1.2.3");

        rest()
                .get("/hello-world")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .description("Hello World Response")
                .to("direct:hello-world")

                .post("/add-employee")
                .produces(MediaType.APPLICATION_JSON_VALUE)
                .description("Add Employee")
                .to("direct:add-employee");

        from("direct:hello-world")
                .routeId("hello-world-api")
                .log("calling getHelloWorld")
                .setBody(constant("{\"hello\":\"world\"}"));

        from("direct:add-employee")
                .routeId("add-employee-api")
                .log("Calling add-employee to wsdl")
                .process(exchange -> {
                    // Get employee details from headers or body
                    String name = (String) exchange.getIn().getHeader("name");
                    String address = (String) exchange.getIn().getHeader("address");
                    String phone = (String) exchange.getIn().getHeader("phone");
                    String department = (String) exchange.getIn().getHeader("department");

                    // Clear headers before setting new request
                    exchange.getMessage().getHeaders().clear();

                    // Create and populate the AddEmployee request
                    AddEmployeeRequest addEmployeeRequest = new AddEmployeeRequest();
                    EmployeeInfo employeeInfo = new EmployeeInfo();

                    employeeInfo.setName(name);
                    employeeInfo.setPhone(phone);
                    employeeInfo.setAddress(address);
                    employeeInfo.setDepartment(department);

                    addEmployeeRequest.setEmployeeInfo(employeeInfo);

                    exchange.getMessage().setBody(addEmployeeRequest);
                })
                // Send SOAP request to the specified CXF endpoint for adding an employee
                .to("cxf:bean:AddEmployeeEndpoint")
                // Process the response
                .process(exchange -> {
                    exchange.getMessage().getHeaders().clear();
                    AddEmployeeResponse addEmployeeResponse = (AddEmployeeResponse) ((MessageContentsList) exchange.getIn().getBody()).get(0);
                    exchange.getMessage().setBody(addEmployeeResponse);
                })
                .log("sending response to add an employee to frontend as json")
                .marshal()
                .json(JsonLibrary.Jackson)
                .log("REST Service Response: ${body}");

    }

}
