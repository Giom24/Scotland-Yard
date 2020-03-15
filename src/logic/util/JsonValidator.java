/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;

/**
 *
 * @author giom
 */
public class JsonValidator {

    public static boolean validateBoard(JsonElement root) {

        if (root == null || !root.isJsonObject() || root.getAsJsonObject().get("stations") == null
                || !root.getAsJsonObject().get("stations").isJsonArray()) {
            return false;
        }
        boolean validate = true;
        for (JsonElement jStation : root.getAsJsonObject().get("stations").getAsJsonArray()) {
            validate &= isObject(jStation);
            if (validate) {
                JsonObject station = jStation.getAsJsonObject();
                JsonElement id = station.get("identifier");
                JsonElement jPosition = station.get("position");
                validate &= isObject(jPosition);
                if (validate) {
                    JsonObject position = jPosition.getAsJsonObject();
                    JsonElement x = position.get("x");
                    JsonElement y = position.get("y");
                    validate &= isNumber(x) && isNumber(y);
                }
                JsonElement jTube = station.get("tube");
                JsonElement jBus = station.get("bus");
                JsonElement jCab = station.get("cab");
                JsonElement jBoat = station.get("boat");

                validate &= isArray(jTube) && isArray(jBus) && isArray(jCab) && isArray(jBoat);
                if (validate) {
                    JsonArray tube = jTube.getAsJsonArray();
                    JsonArray bus = jTube.getAsJsonArray();
                    JsonArray cab = jTube.getAsJsonArray();
                    JsonArray boat = jTube.getAsJsonArray();

                    validate &= isNumericArray(tube) && isNumericArray(bus) && isNumericArray(cab)
                            && isNumericArray(boat);
                }

                validate &= isNumber(id);
            }

        }

        return validate;
    }

    public static boolean validateSaveState(JsonElement root) {

        if (root == null || !root.isJsonObject()) {
            return false;
        }
        JsonObject object = root.getAsJsonObject();
        JsonElement whosTurn = object.get("whosTurn");
        JsonElement currRoundNo = object.get("currRoundNo");

        return validateMisterXSaveSate(object) && validateDetectivesSaveState(object)
                && isNumber(whosTurn) && isNumber(currRoundNo);
    }

    private static boolean validateMisterXSaveSate(JsonObject root) {

        if (root.get("misterX") == null || !root.get("misterX").isJsonObject()) {
            return false;
        }

        JsonObject jMisterX = root.get("misterX").getAsJsonObject();
        JsonElement ai = jMisterX.get("ai");
        JsonElement lastShownPos = jMisterX.get("lastShownPos");
        JsonElement currPos = jMisterX.get("currPos");
        JsonElement remainingTickets = jMisterX.get("remainingTickets");
        JsonElement journeyBoard = jMisterX.get("journeyBoard");

        boolean journeyBoardValide = journeyBoard != null && journeyBoard.isJsonArray();

        if (!journeyBoardValide) {
            return false;
        }
        for (JsonElement element : journeyBoard.getAsJsonArray()) {
            journeyBoardValide &= isNumber(element);
        }

        return isBoolean(ai) && isNumber(lastShownPos) && isNumber(currPos)
                && remainingTickets != null && remainingTickets.isJsonArray()
                && remainingTickets.getAsJsonArray().size() == 4
                && isNumericArray(remainingTickets.getAsJsonArray()) && journeyBoardValide;

    }

    private static boolean validateDetectivesSaveState(JsonObject root) {
        JsonObject jDetectives = root.get("detectives").getAsJsonObject();
        JsonElement ai = jDetectives.get("ai");
        JsonElement noOfDetectives = jDetectives.get("noOfDetectives");
        JsonElement players = jDetectives.get("players");

        boolean playerValid = players != null && players.isJsonArray();

        if (!playerValid) {
            return false;
        }

        for (JsonElement player : players.getAsJsonArray()) {
            if (player.isJsonObject()) {
                JsonObject test = player.getAsJsonObject();
                JsonElement position = test.get("position");
                JsonElement remainingTickets = test.get("remainingTickets");

                playerValid &= isNumber(position) && remainingTickets.getAsJsonArray().size() == 3
                        && remainingTickets.getAsJsonArray().get(0) != null
                        && isNumericArray(remainingTickets.getAsJsonArray());
            }
        }

        return isBoolean(ai) && isNumber(noOfDetectives) && playerValid;
    }

    private static boolean isNumber(JsonElement element) {
        return element != null && element.isJsonPrimitive()
                && element.getAsJsonPrimitive().isNumber();
    }

    private static boolean isBoolean(JsonElement element) {
        return element != null && element.isJsonPrimitive()
                && element.getAsJsonPrimitive().isBoolean();
    }

    private static boolean isObject(JsonElement element) {
        return element != null && element.isJsonObject();
    }

    private static boolean isArray(JsonElement element) {
        return element != null && element.isJsonArray();
    }

    private static boolean isNumericArray(JsonArray array) {
        Iterator<JsonElement> it = array.iterator();

        boolean valid = true;
        while (valid && it.hasNext()) {
            JsonElement element = it.next();
            valid &= isNumber(element);
        }

        return valid;
    }

}
