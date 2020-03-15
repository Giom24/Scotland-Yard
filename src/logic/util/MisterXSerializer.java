/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;
import logic.Ticket;
import logic.board.Station;
import logic.player.MisterX;

/**
 * A custom serializer for misterX
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public class MisterXSerializer implements JsonSerializer<MisterX> {

    @Override
    public JsonElement serialize(MisterX misterX, Type typeOfSrc,
            JsonSerializationContext context) {
        JsonObject JSONMisterX = new JsonObject();
        JSONMisterX.addProperty("ai", misterX.isAi());
        JSONMisterX.add("possibleTargets", null);
        Station lastShownStation = misterX.getLastSeen();
        int lastShownPos = lastShownStation == null ? 0 : lastShownStation.getIdentifier();
        JSONMisterX.addProperty("lastShownPos", lastShownPos);
        JSONMisterX.addProperty("currPos", misterX.getCurrentStation().getIdentifier());
        JsonArray tickets = new JsonArray();
        tickets.add(misterX.getTicketNum(Ticket.TUBE));
        tickets.add(misterX.getTicketNum(Ticket.BUS));
        tickets.add(misterX.getTicketNum(Ticket.CAB));
        tickets.add(misterX.getTicketNum(Ticket.BLACK));
        JSONMisterX.add("remainingTickets", tickets);
        List<Integer> journeyBoard = misterX.getLogbook().stream().map((ticket) -> ticket.ordinal())
                .collect(Collectors.toList());
        JSONMisterX.add("journeyBoard", context.serialize(journeyBoard));
        return JSONMisterX;
    }

}
