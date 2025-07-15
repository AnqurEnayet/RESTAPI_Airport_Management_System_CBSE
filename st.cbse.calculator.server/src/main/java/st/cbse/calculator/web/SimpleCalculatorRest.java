package st.cbse.calculator.web;

import jakarta.ejb.EJB;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import st.cbse.calculator.data.Result;
import st.cbse.calculator.interfaces.SimpleCalculatorRemote;

@Path("calculator")
public class SimpleCalculatorRest {

    @EJB
    SimpleCalculatorRemote calculator;

    @GET
    @Path("result")
    @Produces("application/json")
    public JsonObject result(){
        return transform2json(calculator.result());
    }

    private JsonObject transform2json(Result result) {
        return Json.createObjectBuilder()
                .add("value", result.getValue())
                .add("sequence", result.getSequence())
                .build();
    }


}
