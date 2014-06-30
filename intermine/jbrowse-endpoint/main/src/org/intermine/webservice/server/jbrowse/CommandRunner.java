package org.intermine.webservice.server.jbrowse;

/*
 * Copyright (C) 2002-2014 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.intermine.api.InterMineAPI;

public abstract class CommandRunner {

    private InterMineAPI api;

    private Set<MapListener<String, Object>> listeners = new HashSet<MapListener<String, Object>>();

    public CommandRunner(InterMineAPI api) {
        this.api = api;
    }

    protected InterMineAPI getAPI() {
        return api;
    }

    public static CommandRunner getRunner(String className, InterMineAPI im) {
        CommandRunner runner;
        try {
            @SuppressWarnings("unchecked")
            Class<CommandRunner> runnerCls = (Class<CommandRunner>) Class.forName(className);
            Constructor<CommandRunner> ctr = runnerCls.getConstructor(InterMineAPI.class);
            runner = ctr.newInstance(im);
        } catch (ClassCastException e) {
            throw new RuntimeException("Configuration is incorrect.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot find " + className);
        } catch (SecurityException e) {
            throw new RuntimeException("Not allowed to access " + className);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot access constructor for " + className);
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot instantiate runner", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access runner", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot invoke constructor of " + className, e);
        }
        return runner;
    }

    public String getIntro(Command command) {
        switch (command.getAction()) {
        case STATS:
        case DENSITIES:
            return null;
        case REFERENCE:
        case FEATURES:
            return "\"features\":[";
        default:
            throw new IllegalArgumentException("Unknown action: " + command.getAction());
        }
    }

    public String getOutro(Command command) {
        switch (command.getAction()) {
        case STATS:
        case DENSITIES:
            return null;
        case REFERENCE:
        case FEATURES:
            return "]";
        default:
            throw new IllegalArgumentException("Unknown action: " + command.getAction());
        }
    }

    public void run(Command command) {
        switch (command.getAction()) {
        case STATS:
            stats(command);
            break;
        case REFERENCE:
            reference(command);
            break;
        case FEATURES:
            features(command);
            break;
        case DENSITIES:
            densities(command);
            break;
        default:
            throw new IllegalArgumentException("Unknown action: " + command.getAction());
        }
    }

    public abstract void stats(Command command);

    public abstract void reference(Command command);

    public abstract void features(Command command);

    public abstract void densities(Command command);

    protected void onData(Map<String, Object> datum, boolean hasMore) {
        for (MapListener<String, Object> listener: listeners) {
            listener.add(datum, hasMore);
        }
    }

    protected void onData(Entry<String, Object> datum, boolean hasMore) {
        for (MapListener<String, Object> listener: listeners) {
            listener.add(datum, hasMore);
        }
    }

    public void addListener(MapListener<String, Object> listener) {
        listeners.add(listener);
    }
}
