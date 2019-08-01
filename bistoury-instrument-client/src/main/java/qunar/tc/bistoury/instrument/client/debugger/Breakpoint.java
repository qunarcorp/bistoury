package qunar.tc.bistoury.instrument.client.debugger;

import qunar.tc.bistoury.instrument.client.location.Location;
import qunar.tc.bistoury.instrument.client.spring.el.Expression;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author zhenyu.nie created on 2018 2018/9/21 14:23
 */
public class Breakpoint {

    private final String id;

    private final Location location;

    private final Expression condition;

    private final AtomicBoolean trigger = new AtomicBoolean(false);

    public Breakpoint(String id, Location location, Expression condition) {
        this.id = id;
        this.location = location;
        this.condition = condition;
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public Expression getCondition() {
        return condition;
    }

    public boolean trigger() {
        return trigger.compareAndSet(false, true);
    }
}
