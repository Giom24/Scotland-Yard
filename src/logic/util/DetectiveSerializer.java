package logic.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import logic.Ticket;
import logic.player.Detective;

/**
 * A custom serializer for a detective
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public class DetectiveSerializer implements JsonSerializer<Detective> {

    @Override
    public JsonElement serialize(Detective detective, Type typeOfSrc,
            JsonSerializationContext context) {
        JsonObject jsonDetective = new JsonObject();
        jsonDetective.addProperty("position", detective.getCurrentStation().getIdentifier());
        JsonArray tickets = new JsonArray();
        tickets.add(detective.getTicketNum(Ticket.TUBE));
        tickets.add(detective.getTicketNum(Ticket.BUS));
        tickets.add(detective.getTicketNum(Ticket.CAB));
        jsonDetective.add("remainingTickets", tickets);
        return jsonDetective;
    }

}
