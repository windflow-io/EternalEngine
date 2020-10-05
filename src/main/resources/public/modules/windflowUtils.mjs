import {
    computed,
    isRef,
    markRaw,
    reactive,
    readonly,
    ref,
    toRefs,
    watch,
} from '../vendor/vue3/vue.esm-browser.js';
import useSwr, { mutate } from '../vendor/swrv/index.mjs';

import { ErrorLayout } from './coreComponents.mjs'

const COMPONENT_TYPES = {
    default: Symbol('Identifier for regular components'),
    layout: Symbol('Identifier for layout components'),
};

const COMPONENT_ENDPOINTS = {
    [COMPONENT_TYPES.default]: '/api/components',
    [COMPONENT_TYPES.layout]: '/api/layouts',
};

/** Errors */

class NetworkError extends Error {
    constructor(message, status, data = null) {
        super(message || 'NetworkError');
        this.name = 'NetworkError';
        this.status = status;
        this.data = data;
    }
}

/** Namespacing **/

export const removeNamespace = (name) => {
    return name ? name.substring(name.lastIndexOf('.') + 1) : null;
}

export const namespaceOnly = (name) => {
    return name ? name.substring(0, name.lastIndexOf('.')) : null;
}

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

/** Load Stylesheet **/

export const loadStylesheet = (url) => {
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

/** Load Editor **/

export async function loadEditor() {
    if (window.monaco) return window.monaco;

    const require = await loadScript('/vendor/monacoEditor/loader.js', 'require');
    require.config({ paths: { 'vs': '/vendor/monacoEditor/vs' }});

    return new Promise((resolve) => {
        require(['vs/editor/editor.main'], () => resolve(window.monaco));
    });
}

/** Retry on Error **/

const bypassIrrelevantErrors = error => error.status <= 500;

export const withRetryHandling = (callback, {
    baseDelay = 400,
    logger = console,
    numberOfTries = 3,
    bypass = bypassIrrelevantErrors,
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

/** Assemble component URL **/

function assembleComponentUrl({ namespacedName, type }) {
    const endpoint = COMPONENT_ENDPOINTS[type];
    const parts = [endpoint, namespaceOnly(namespacedName), `${removeNamespace(namespacedName)}.mjs`];

    return parts.join('/');
}

/** http **/

// REFACTOR
// - Dedupe logic.
export const http = {
    async get(url, options) {
        const response = await fetch(url, options);
        const contentType = response.headers.get('content-type');
        let data;

        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        if (!response.ok) throw new NetworkError(response.statusText, response.status, data);

        return data;
    },
    async post(url, options) {
        const response = await fetch(url, {
            ...options,
            method: 'POST',
        });
        const contentType = response.headers.get('content-type');
        let data;

        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        if (!response.ok) throw new NetworkError(response.statusText, response.status, data);

        return data;
    },
    async put(url, options) {
        const response = await fetch(url, {
            ...options,
            method: 'PUT',
        });
        const contentType = response.headers.get('content-type');
        let data;

        if (contentType && contentType.includes('application/json')) {
            data = await response.json();
        } else {
            data = await response.text();
        }

        if (!response.ok) throw new NetworkError(response.statusText, response.status, data);

        return data;
    },
};

/** API */

const apiDefaultOptions = {
    method: 'GET',
    headers: { 'Content-Type': 'application/json' },
};

// REFACTOR
// - Dedupe logic.
export function makeApi({ http }) {
    return {
        create(endpoint, data, customOptions) {
            const options = {
                ...apiDefaultOptions,
                ...customOptions,
                body: typeof data === 'string' ? data : JSON.stringify(data),
            };

            return http.post(`${endpoint}`, options);
        },
        get(endpoint, customOptions) {
            const options = {
                ...apiDefaultOptions,
                ...customOptions,
            };

            return http.get(`${endpoint}`, options)
        },
        update(endpoint, data, customOptions) {
            const options = {
                ...apiDefaultOptions,
                ...customOptions,
                body: typeof data === 'string' ? data : JSON.stringify(data),
            };

            return http.put(`${endpoint}`, options);
        },
    };
};

/** Service: Component */

// REFACTOR
// - Rename "makeServiceComponent" so make functions are grouped by type.
export function makeComponentService({ api, host }) {
    return {
        create({ data, name, type = COMPONENT_TYPES.default }) {
            const namespacedName = `${host}.${name}`;
            const url = assembleComponentUrl({ namespacedName, type });
            return api.create(url, data);
        },
        load: withRetryHandling(({ name: namespacedName, type = COMPONENT_TYPES.default }) => {
            const url = assembleComponentUrl({ namespacedName, type });
            return api.get(url);
        }),
        update({ data, name: namespacedName, type = COMPONENT_TYPES.default }) {
            const url = assembleComponentUrl({ namespacedName, type });
            return api.update(url, data);
        },
    };
}

/** Service: Page */

const PAGE_SERVICE_ENDPOINT = '/api/pages';

export function makePageService({ api, host }) {
    return {
        load: withRetryHandling(({ path }) => {
            return api.get(`${PAGE_SERVICE_ENDPOINT}/${host}${path}`);
        }),
        update({ data, path }) {
            return api.update(`${PAGE_SERVICE_ENDPOINT}/${host}${path}`, data);
        },
    };
}

/** Context: Component Cache **/

export function makeContextCacheComponent({ componentService, registerUpdatedComponent }) {
    // REFACTOR
    // - Make this bullet proof.
    const createComponentFromCode = (code) => {
        const component = eval(`${code.replace('export default ', '(() => (')}))()`);
        return component;
    }

    const makeNewComponent = ({ name }) => {
        return `export default {
    name: '${name}',
    props: {
        heading: {
            default: 'Hello World',
            type: String,
        },
        paragraph: {
            default: 'Lorem Ipsum',
            type: String,
        },
    },
    schema: {
        heading: {
            type: 'text',
            label: 'Heading',
        },
        paragraph: {
            type: 'textarea',
            label: 'Paragraph',
        },
    },
    template: \`
        <div class="max-w-screen-xl mx-auto py-12 px-4 sm:px-6 lg:py-16 lg:px-8">
            <h2 class="leading-9 font-extrabold tracking-tight text-gray-900 sm:leading-10 text-3xl">
                {{ heading }}
            </h2>
            <p class="mt-2 text-base text-gray-500 sm:mt-5 sm:text-lg sm:max-w-xl sm:mx-auto md:mt-5 md:text-xl lg:mx-0">
                {{ paragraph }}
            </p>
        </div>
    \`,
}`;
    }

    const addComponent = ({ data, name }) => {
        mutateComponent({ data, name });
        const component = createComponentFromCode(data)
        registerUpdatedComponent({ component, name });

        return componentService.create({ data, name });
    };

    const addDefaultComponent = ({ name }) => {
        const data = makeNewComponent({ name });
        return addComponent({ data, name });
    };

    const loadComponent = (options) => {
        // REFACTOR
        // - Make this generic and add an ID for each service.
        const params = isRef(options) ? options : computed(typeof options === 'function' ? options : () => options);
        return useSwr(() => params.value.name && JSON.stringify({ name: params.value.name }), paramString => componentService.load(JSON.parse(paramString)));
    };

    const mutateComponent = ({ data, name }) => {
        // REFACTOR
        // - Make this generic and add an ID for each service.
        mutate(JSON.stringify({ name }), data);
    };

    const updateComponent = ({ data, name }) => {
        mutateComponent({ data, name });
        const component = createComponentFromCode(data)
        registerUpdatedComponent({ component, name });

        return componentService.update({ data, name });
    };

    return {
        addDefaultComponent,
        loadComponent,
        mutateComponent,
        updateComponent,
    };
}

/** Context: Page Cache **/

export function makeContextCachePage({ pageService }) {
    const stagedComponentData = reactive({});

    const loadPage = (options) => {
        // REFACTOR
        // - Make this generic and add an ID for each service.
        const params = isRef(options) ? options : computed(typeof options === 'function' ? options : () => options);
        const cache = useSwr(() => params.value.path && JSON.stringify({ path: params.value.path }), paramString => pageService.load(JSON.parse(paramString)));

        return {
            ...cache,
            data: computed(() => {
                if (!cache.data.value) return cache.data.value;

                return {
                    ...cache.data.value,
                    data: {
                        ...cache.data.value.data,
                        components: {
                            ...cache.data.value.data.components,
                            ...stagedComponentData,
                        },
                    },
                };
            }),
        };
    };

    const mutatePage = ({ data, path }) => {
        // REFACTOR
        // - Make this generic and add an ID for each service.
        mutate(JSON.stringify({ path }), data);
    };

    const updatePage = ({ data, path }) => {
        mutatePage({ data, path });
        return pageService.update({ data, path });
    };

    const loadComponentData = (options) => {
        // REFACTOR
        // - Make this generic and add an ID for each service.
        const params = isRef(options) ? options : computed(typeof options === 'function' ? options : () => options);
        const cache = loadPage(params);
        const componentData = computed(() => {
            return cache.data.value && cache.data.value.data && cache.data.value.data.components;
        });
        const data = computed(() => componentData.value && componentData.value[params.value.id]);

        return {
            ...cache,
            data,
        };
    };

    const updateComponentData = async ({ path }) => {
        const oldData = await pageService.load({ path });
        const data = {
            ...oldData,
            data: {
                ...oldData.data,
                components: {
                    ...oldData.data.components,
                    ...stagedComponentData,
                },
            },
        };
        const result = await updatePage({ data, path });

        for (const componentDataId of Object.keys(data.data.components)) {
            delete stagedComponentData[componentDataId];
        }

        return result;
    };

    const commitComponentData = ({ data, id }) => {
        stagedComponentData[id] = data;
    };

    const addAreaComponent = async ({ areaComponent, areaName, path }) => {
        const oldData = await pageService.load({ path });
        const areasWithNew = oldData.areas.map((area) => {
            if (area.area !== areaName) return area;

            return {
                ...area,
                components: [
                    ...area.components,
                    areaComponent,
                ]
            }
        });
        const data = {
            ...oldData,
            areas: areasWithNew,
        };

        return updatePage({ data, path });
    };

    return {
        addAreaComponent,
        commitComponentData,
        loadComponentData,
        loadPage,
        mutatePage,
        updateComponentData,
    };
}

/** Context: Component Registry **/

export function makeContextComponentRegistry() {
    const components = reactive({
        [ErrorLayout.name]: markRaw(ErrorLayout),
    });

    return {
        components: readonly(components),
        async registerComponent({ name, type }) {
            const url = assembleComponentUrl({
                namespacedName: name,
                type,
            });

            components[name] = markRaw((await import(url)).default);
        },
        registerUpdatedComponent({ component, name }) {
            components[name] = markRaw(component);
        },
    };
}

/** Context: Edit Mode **/

export function makeContextEditMode({ loadStylesheet }) {
    const state = reactive({
        activeAreaComponent: null,
        isInEditMode: null,
    });

    const loadEditModeAssets = async () => {
        await Promise.all([
            loadEditor(),
            loadStylesheet('/vendor/tailwindcss/tailwind.min.css'),
        ]);
    }

    const editAreaComponent = (areaComponent) => {
        state.activeAreaComponent = areaComponent;
    };

    const enableEditMode = async () => {
        if (state.isInEditMode === null) await loadEditModeAssets();

        window.location.hash = editModeHash;
        state.isInEditMode = true;
    };

    const disableEditMode = async () => {
        window.location.hash = '';
        state.isInEditMode = false;
    };

    const editModeHash = 'edit';
    const isInEditModeInitially = window.location.hash === `#${editModeHash}`;
    if (isInEditModeInitially) enableEditMode();

    return {
        ...toRefs(state),
        disableEditMode,
        editAreaComponent,
        enableEditMode,
    };
}

/** Context: Router **/

export function makeContextRouter({ loadPage, registerComponent }) {
    const listeners = [];
    let previousPath = null;
    const currentPath = ref(window.location.pathname);

    const push = (path) => {
        previousPath = window.location.pathname;
        window.history.pushState(null, null, path);
        currentPath.value = path;
        listeners.forEach(listener => listener(path, previousPath));
    };

    window.addEventListener('popstate', () => {
        listeners.forEach(listener => listener(window.location.pathname, previousPath))
    });

    const getComponentDescriptionsForPage = (page) => {
        const componentDescriptions = [{
            name: page.layout,
            type: COMPONENT_TYPES.layout,
        }];

        for (const area of page.areas) {
            for (const component of area.components) {
                componentDescriptions.push({
                    name: component.name,
                    type: COMPONENT_TYPES.default,
                });
            }
        }

        return componentDescriptions;
    };

    const makeErrorPage = ({
        errorTitle = 'Oops!',
        errorDescription = 'An unexpected error occurred.',
        errorDetail = null,
        httpStatus = 500,
    } = {}) => ({
        metaInfo: {
            title: errorTitle,
            meta: [
                { charset :  'utf-8' },
                {
                    content:  errorDescription,
                    name: 'description',
                },
            ],
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

    // REFACTOR
    // - Make use of promises and wait until resolved and registered before returning data.
    const { data, error } = loadPage(() => ({ path: currentPath.value }));
    const registerError = ref(null);

    watch(data, () => {
        if (!data.value) return;

        try {
            // REFACTOR
            // - Make this work with all meta tags, like vue-meta.
            // - Make this work with error layout.
            document.title = data.value.metaInfo.title;
            document.documentElement.lang = data.value.metaInfo.htmlAttrs.lang;

            registerError.value = null;
            const componentDescriptions = getComponentDescriptionsForPage(data.value);
            componentDescriptions.forEach(registerComponent);
        } catch (error) {
            console.error(error);
            registerError.value = markRaw(error);
        }
    }, { deep: true, immediate: true });

    const page = computed(() => {
        if (!error.value && !registerError.value) return data.value;

        return makeErrorPage();
    })

    return {
        currentPath,
        page,
        push,
    };
}

/** Composable: Drag **/

export function useDrag({ $el }) {
    const isDragging = ref(false);
    const offsetX = ref(0);
    const offsetY = ref(0);

    const doDrag = (event) => {
        if (!isDragging.value) return;

        $el.value.style.top = `${event.clientY - offsetY.value}px`;
        $el.value.style.left = `${event.clientX - offsetX.value}px`;
    };

    const endDrag = () => {
        isDragging.value = false;
        window.removeEventListener('mousemove', doDrag);
        window.removeEventListener('mouseup', endDrag);
    };

    const startDrag = (event) => {
        if (isDragging.value) return;

        offsetY.value = event.clientY - $el.value.getBoundingClientRect().top;
        offsetX.value = event.clientX - $el.value.getBoundingClientRect().left;
        isDragging.value = true;
        window.addEventListener('mousemove', doDrag);
        window.addEventListener('mouseup', endDrag);
    };

    return {
        startDrag,
    };
}
