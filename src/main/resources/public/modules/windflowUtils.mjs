import {app} from '../app/app.mjs'

/** Query String **/

export const mapQueryString = (url) => {
    let o = {}
    let kvs = url.substring(url.indexOf("?") + 1).split("&");
    for (let i in kvs) {
        let kv = kvs[i].split("=");
        o[kv[0]] = kv[1];
    }
    return o;
}

/** Namespacing **/

export const removeNamespace = (name) => {
    return name ? name.substring(name.lastIndexOf(".") + 1) : null;
}

export const namespaceOnly = (name) => {
    return name ? name.substring(0, name.lastIndexOf(".")) : null;
}

/** History API **/

const listeners = [];
let previousRoute = window.location.pathname;

export const pushUrl = (route) => {
    previousRoute = window.location.pathname;
    window.history.pushState(null, null, route);
    listeners.forEach(listener => listener(route, previousRoute))
}

export const addUrlListener = (callback) => {
    listeners.push(callback);
}

window.addEventListener(
    "popstate", (event) => {
        listeners.forEach(listener => listener(window.location.pathname, previousRoute))
    }
);

/** Handle Errors **/

export const withErrorHandling = (callback, { logger = console, notifier }) => {
    return async function callbackWithErrorHandling(...params) {
        try {
            return await callback(...params);
        } catch (error) {
            if (logger) logger.error(error);
            if (notifier) notifier.notify({ title: error.message });
        }
    }
}

/** Retry on Error **/

export const withRetryHandling = (callback, {
    baseDelay = 400,
    logger = console,
    numberOfTries = 3,
} = {}) => {
    return function callbackWithRetryHandling(...params) {
        const retry = async (attempt = 1) => {
            try {
                return await callback(...params);
            } catch (error) {
                if (attempt >= numberOfTries) throw error;

                // Use an increasing delay to prevent flodding the server with
                // requests in case of a short downtime.
                const delay = baseDelay * attempt;

                if (logger) logger.warn('Retry because of', error);

                return new Promise(resolve => setTimeout(() => resolve(retry(attempt + 1)), delay));
            }
        }
        return retry();
    };
}

/** Notifications **/

export const alertNotifier = {
    notify({ title }) {
        alert(title);
    },
};

/** Service: Component */

export const COMPONENT_TYPES = {
    default: Symbol('Identifier for regular components'),
    layout: Symbol('Identifier for layout components'),
};

const componentEndpoints = {
    [COMPONENT_TYPES.default]: '/api/components',
    [COMPONENT_TYPES.layout]: '/api/layouts',
};

const registeredComponents = {};

function assembleComponentUrl({ namespacedName, type }) {
    const endpoint = componentEndpoints[type];
    const parts = [endpoint, namespaceOnly(namespacedName), `${removeNamespace(namespacedName)}.mjs`];

    return parts.join('/');
}

export const componentService = {
    async load(namespacedName, { type = COMPONENT_TYPES.default } = {}) {
        const name = removeNamespace(namespacedName);

        if (registeredComponents[name]) {
            await registeredComponents[name];
            return name;
        }

        const url = assembleComponentUrl({ namespacedName, type });
        const modulePromise = registeredComponents[name] = import(url);
        const module = await modulePromise;

        app.component(name, module.default);

        return name;
    },
};
