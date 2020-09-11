import {app} from '../app/app.mjs'
import {ErrorLayout} from '/modules/coreComponents.mjs'

/** Errors */

class NetworkError extends Error {
    constructor(message, status, data = null) {
        super(message || 'NetworkError');
        this.name = 'NetworkError';
        this.status = status;
        this.data = data;
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
        if (window[globalName]) {
            resolve(window[globalName]);
            return;
        }

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

/** Load Editor **/

export async function loadEditor() {
    if (window.monaco) return window.monaco;

    const require = await loadScript('/vendor/monacoEditor/loader.js', 'require');
    require.config({ paths: { 'vs': '/vendor/monacoEditor/vs' }});

    return new Promise((resolve) => {
        require(['vs/editor/editor.main'], () => resolve(window.monaco));
    });
}

/** Load Stylesheet **/

export const loadStyleshet = (url) => {
    return new Promise((resolve, reject) => {
        if (document.querySelector(`link[href="${url}"]`)) {
            resolve();
            return;
        }

        const link = document.createElement('link');
        link.rel = 'stylesheet';
        link.addEventListener('load', () => {
            resolve();
        });
        link.addEventListener('error', () => {
            reject(new Error(`Error loading ${url}`));
        });
        link.href = url;
        document.head.appendChild(link);
    });
}

/** Edit Mode **/

export const loadEditModeAssets = async () => {
    await Promise.all([
        loadEditor(),
        loadStyleshet('/vendor/tailwindcss/tailwind.min.css'),
    ]);
}

/** Make Error Page **/

export const makeErrorPage = ({
    errorTitle = 'Oops!',
    errorDescription = 'An unexpected error occurred.',
    errorDetail = null,
    httpStatus = 500,
} = {}) => ({
    title: errorTitle,
    metaData: {
        title: errorTitle,
        description: errorDescription,
        httpStatus,
    },
    layout: ErrorLayout.name,
    areas: [],
    data: {
        errorTitle,
        errorDescription,
        errorDetail,
    },
});

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

/** API */

const apiEndpoint = '/api';
const apiDefaultOptions = {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' },
};

export const api = async (endpoint, customOptions) => {
    const options = {
        ...apiDefaultOptions,
        ...customOptions,
    }
    const response = await fetch(`${apiEndpoint}${endpoint}`, options);
    const data = await response.json();
    if (!response.ok) throw new NetworkError(response.statusText, response.status, data);

    return data;
};

/** Service: Page */

const pageEndpoint = '/pages';

export const pageService = {
    load({ host, path }) {
        return api(`${pageEndpoint}/${host}${path}`);
    },
    update({ data, host, path }) {
        return api(`${pageEndpoint}/${host}${path}`, {
            method: 'PUT',
            body: JSON.stringify(data),
        });
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
    async register(component) {
        if (registeredComponents[component.name]) {
            await registeredComponents[component.name];
            return;
        }

        registeredComponents[component.name] = { default: component };
        app.component(component.name, component);
    },
    async importAndRegister(namespacedName, { type = COMPONENT_TYPES.default } = {}) {
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
    async load(namespacedName, { type = COMPONENT_TYPES.default } = {}) {
        const url = assembleComponentUrl({ namespacedName, type });
        return fetch(url).then(response => response.text());
    },
};

/** Page **/

const bypassIrrelevantErrors = error => error.status <= 500;
const loadPage = withRetryHandling(pageService.load, { bypass: bypassIrrelevantErrors });

const loadLayout = withRetryHandling(name => componentService.importAndRegister(name, {
    type: COMPONENT_TYPES.layout,
}));

const loadComponent = withRetryHandling(componentService.importAndRegister);

export const bootstrapPage = async ({ host, path }) => {
    let page;

    try {
        page = await loadPage({ host, path });

        const allComponents = [];
        page.areas.forEach(section => section.components.forEach(component => allComponents.push(component)));

        await Promise.all([
            loadLayout(page.layout),
            ...allComponents.map(component => loadComponent(component.name)),
        ]);
    } catch (error) {
        /**@TODO: Maybe add error logging with something like Sentry or DataDog (send ALL errors to server at some point later) **/
        console.error(error);

        await componentService.register(ErrorLayout);
        page = makeErrorPage(error.data);
    }

    return page;
};
