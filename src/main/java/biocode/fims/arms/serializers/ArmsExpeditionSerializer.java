package biocode.fims.arms.serializers;

import biocode.fims.arms.entities.ArmsExpedition;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Custom JSON serializer for {@link ArmsExpedition}. This is to include the @Transient annotated {@link biocode.fims.entities.Expedition}
 * property in the JSON Representation of an {@link ArmsExpedition}
 */
public class ArmsExpeditionSerializer extends JsonSerializer<ArmsExpedition> {
    @Override
    public void serialize(ArmsExpedition armsExpedition, JsonGenerator jgen, SerializerProvider provider)
        throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("principalInvestigator", armsExpedition.getPrincipalInvestigator());
        jgen.writeStringField("contactName", armsExpedition.getContactName());
        jgen.writeStringField("contactEmail", armsExpedition.getContactEmail());
        jgen.writeStringField("fundingSource", armsExpedition.getFundingSource());
        jgen.writeNumberField("envisionedDuration", armsExpedition.getEnvisionedDuration());
        jgen.writeStringField("geographicScope", armsExpedition.getGeographicScope());
        jgen.writeStringField("goald", armsExpedition.getGoals());
        jgen.writeStringField("leadOrganization", armsExpedition.getLeadOrganization());
        jgen.writeObjectField("expedition", armsExpedition.getExpedition());
        jgen.writeEndObject();
    }
}
