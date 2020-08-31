import {app} from '../app/app.mjs'

/** Errors */

class NetworkError extends Error {
    constructor(message, status) {
        super(message || 'NetworkError');
        this.name = 'NetworkError';
        this.status = status;
    }
}

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

/** Load Script **/

export const loadScript = (url, globalName = null) => {
    return new Promise((resolve, reject) => {
        const script = document.createElement('script');
        script.async = true;
        script.addEventListener('load', () => {
            resolve(globalName ? window[globalName] : true);
        });
        script.addEventListener('error', () => {
            reject(new Error(`Error loading ${url}`));
        });
        script.src = url;
        document.head.appendChild(script);
    });
}

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
    bypass = error => false,
} = {}) => {
    return function callbackWithRetryHandling(...params) {
        const retry = async (attempt = 1) => {
            try {
                return await callback(...params);
            } catch (error) {
                if (bypass(error) || attempt >= numberOfTries) throw error;

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

/** API */

const apiEndpoint = '/api';

export const api = async (endpoint) => {
    const response = await fetch(`${apiEndpoint}${endpoint}`);
    if (!response.ok) throw new NetworkError(response.statusText, response.status);

    return response.json();
};

/** Service: Page */

const pageEndpoint = '/pages';

export const pageService = {
    load({ host, path }) {
        return api(`${pageEndpoint}/${host}${path}`);
    }
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
    async register(component) {
        if (registeredComponents[component.name]) {
            await registeredComponents[component.name];
            return;
        }

        registeredComponents[component.name] = { default: component };
        app.component(component.name, component);
    },
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
