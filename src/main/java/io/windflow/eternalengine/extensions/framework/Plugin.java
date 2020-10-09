package io.windflow.eternalengine.extensions.framework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Plugin {

    public Plugin() {}

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

}
