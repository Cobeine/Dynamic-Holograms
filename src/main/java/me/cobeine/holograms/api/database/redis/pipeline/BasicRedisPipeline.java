package me.cobeine.holograms.api.database.redis.pipeline;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */

public abstract class BasicRedisPipeline extends AbstractRedisPipeline<JsonObject> {


    public BasicRedisPipeline(ChannelDirection direction, String channel) {
        super(direction, channel);
    }

    @Override
    public void processMessage(String message) {
        JsonObject object = (JsonObject) new JsonParser().parse(message);
        handle(object);
    }
}
