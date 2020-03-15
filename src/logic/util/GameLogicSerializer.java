package logic.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import logic.GameLogic;
import logic.player.Detective;

/**
 * A custom serializer for the Gamelogic. Serializes MisterX and all Detectives.
 *
 * @author Guillaume Fournier-Mayer (tinf101922)
 */
public class GameLogicSerializer implements JsonSerializer<GameLogic> {

    @Override
    public JsonElement serialize(GameLogic logic, Type typeOfSrc,
            JsonSerializationContext context) {
        JsonObject jLogic = new JsonObject();
        // Set the field and serialize MisterX
        jLogic.add("misterX", context.serialize(logic.getMisterX()));
        List<Detective> detectives = logic.getDetectives();
        // Create the Field and serialize all detectives
        JsonObject jDetectives = new JsonObject();
        jDetectives.addProperty("noOfDetectives", detectives.size());
        jDetectives.addProperty("ai", detectives.get(0).isAi());
        jDetectives.add("players", context.serialize(detectives));
        jLogic.add("detectives", jDetectives);
        jLogic.addProperty("whosTurn", logic.getWhosTurn());
        jLogic.addProperty("currRoundNo", logic.getGameRound());
        jLogic.addProperty("gameIsWon", logic.isGameWon() != GameLogic.WinState.NO_WIN);
        return jLogic;
    }

}
