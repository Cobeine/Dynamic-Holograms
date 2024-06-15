package me.cobeine.holograms.api.database.redis.pipeline;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The interface Redis pipeline.
 *
 * @param <T> the type parameter
 * @Author <a href="https://github.com/Cobeine">Cobeine</a>
 */
@Getter
public abstract class AbstractRedisPipeline<T> {
    private final String channel;
    private final ChannelDirection direction;
    public AbstractRedisPipeline(ChannelDirection direction, String channel) {
        this.direction = direction;
        this.channel = channel;
    }

    /**
     * Handle The processed message.
     *
     * @param message the message
     */
   public abstract void handle(T message);


    /**
     * Process message to the datatype preferred.
     *
     * @param message the message
     */
    public abstract void processMessage(String message);

    public String getChannelWithDirection() {
        return direction.getDirection() + channel;
    }


    @AllArgsConstructor
    @Getter
    public enum ChannelDirection{
        SERVER_TO_PROXY("STP:"),
        PROXY_TO_SERVER("PTS:"),
        PROXY_TO_PROXY("PTP:"),
        SERVER_TO_SERVER("STS:");

        private final String direction;
    }



}
