import {computed, isRef, markRaw, reactive, readonly, ref, toRefs, watch,} from '../vendor/vue3/vue.esm-browser.js';
import useSwr, {mutate} from '../vendor/swrv/index.js';

import {ErrorLayout} from './coreComponents.js'

const COMPONENT_TYPES = {
    component: 'COMPONENT',
    layout: 'LAYOUT',
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

export function arrayMoveIndex(array, fromIndex, toIndex) {
    const result = [];
    const shift = toIndex - fromIndex > 0 ? 1 : -1;
    const start = toIndex < fromIndex ? toIndex : fromIndex;
    const end = start < toIndex ? toIndex : fromIndex;

    for (let index = 0; index < array.length; index++) {
      const offset = index >= start && index <= end ? shift : 0;
      result[index] = array[index + offset];
    }

    result[toIndex] = array[fromIndex];

    return result;
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

const COMPONENT_SERVICE_ENDPOINT = '/api/components';

export function makeServiceComponent({ api }) {
    return {
        create(data) {
            // REFACTOR
            // Generic validator.
            if (!data.name) throw new Error(`"name" is required!`);
            if (!data.sfc) throw new Error(`"sfc" is required!`);

            const newData = {
                ...data,
                type: data.type || COMPONENT_TYPES.component,
            };

            return api.create(COMPONENT_SERVICE_ENDPOINT, newData);
        },
        // REFACTOR
        // API endpoint for loading multiple components.
        find(ids) {
            return Promise.all(ids.map(id => this.findOne(id)));
        },
        findOne: withRetryHandling(async (id) => {
            return api.get(`${COMPONENT_SERVICE_ENDPOINT}/${id}`);
        }),
        update(id, data) {
            if (!data.name) throw new Error(`"name" is required!`);
            if (!data.sfc) throw new Error(`"sfc" is required!`);

            const newData = {
                ...data,
                type: data.type || COMPONENT_TYPES.component,
            };

            //return api.update(`${COMPONENT_SERVICE_ENDPOINT}/${id}`, newData);
            return api.update(`${COMPONENT_SERVICE_ENDPOINT}`, newData);
        },
    };
}

/** Service: Page */

const PAGE_SERVICE_ENDPOINT = '/api/pages';

const pageServiceAdapter = {
    adaptForApi({ data }) {
        const areas = [];
        const componentsData = {};

        for (const [areaName, area] of Object.entries(data.areas)) {
            const apiArea = {
                area: areaName,
                components: [],
            };

            for (const chapter of area.chapters) {
                componentsData[chapter.id] = chapter.data;
                apiArea.components.push({
                    id: chapter.id,
                    name: chapter.component.id,
                });
            }

            areas.push(apiArea);
        }

        return {
            areas,
            data: {
                components: componentsData,
            },
            layout: data.layout.id,
            metaInfo: data.metaInfo,
        };
    },
    adaptForApp({ data, id, included }) {
        const areas = {};

        for (const { area: areaName, components } of data.areas) {
            areas[areaName] = {
                chapters: components.map(areaComponent => ({
                    component: {
                        id: areaComponent.name,
                        type: COMPONENT_TYPES.component,
                        ...(included && included.components && included.components[areaComponent.name]),
                    },
                    data: data.data && data.data.components && data.data.components[areaComponent.id],
                    id: areaComponent.id,
                })),
                id: areaName,
            };
        }

        return {
            areas,
            id,
            layout: {
                id: data.layout,
            },
            metaInfo: data.metaInfo,
        };
    },
};

export function makeServicePage({ api, serviceComponent }) {
    const resolveIncludes = async ({ data, include }) => {
        const included = {};

        if (include.includes('components')) {
            const componentIds = [];
            for (const area of data.areas) {
                for (const component of area.components) {
                    componentIds.push(component.name);
                }
            }

            included.components = {};
            // REFACTOR
            // Load in parallel.
            for (const component of await serviceComponent.find(componentIds)) {
                included.components[component.id] = component;
            }
        }

        return included;
    }

    return {
        findOne: withRetryHandling(async (id, { include = [] } = {}) => {

            const apiData = await api.get(`${PAGE_SERVICE_ENDPOINT}/${id}`);

            return pageServiceAdapter.adaptForApp({
                data: apiData,
                id,
                included: await resolveIncludes({ data: apiData, include }),
            });
        }),
        findCurrent(options) {
            const id = window.location.pathname;
            return this.findOne(id, options);
        },
        async update(id, data, { include = [] }) {
            const newApiData = pageServiceAdapter.adaptForApi({ data });
            const apiData = await api.update(`${PAGE_SERVICE_ENDPOINT}/${id}`, newApiData);

            return pageServiceAdapter.adaptForApp({
                data: apiData,
                id,
                included: await resolveIncludes({ data: apiData, include }),
            });
        },
    };
}

/** Context: Page Cache **/

export function makeContextCachePage({ servicePage }) {
    const loadPage = (options) => {
        // REFACTOR
        // - Make this generic and add an ID for each service.
        const params = isRef(options) ? options : computed(typeof options === 'function' ? options : () => options);

        return useSwr(() => params.value && JSON.stringify(params.value), paramString => servicePage.findOne(JSON.parse(paramString)));
    };

    const mutatePage = (id, data) => {
        // REFACTOR
        // - Make this generic and add an ID for each service.
        mutate(JSON.stringify({ id }), data);
    };

    return {
        loadPage,
        mutatePage,
    };
}

/** Context: Component Registry **/

const COMPONENT_ASSET_ENDPOINT = '/components';

export function makeContextComponentRegistry() {
    const components = reactive({
        [ErrorLayout.name]: markRaw(ErrorLayout),
    });

    return {
        components: readonly(components),
        async registerComponent(id, { bustCache = false } = {}) {
            const cacheBustSuffix = bustCache ? `?cache-buster=${Date.now()}` : '';
            const url = `${COMPONENT_ASSET_ENDPOINT}/${id}.js${cacheBustSuffix}`;
            components[id] = markRaw((await import(url)).default);
        },
    };
}

/** Context: Edit Mode **/

const makeNewSfc = (name) => {
    return  `
<script>
export default {
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
        }
    }
}
</script>
<template>
    <div class="max-w-screen-xl mx-auto py-12 px-4 sm:px-6 lg:py-16 lg:px-8">
        <h2 class="leading-9 font-extrabold tracking-tight text-gray-400 sm:leading-10 text-3xl">
            {{ heading }}
        </h2>
        <p class="mt-2 text-base text-gray-500 sm:mt-5 sm:text-lg sm:max-w-xl sm:mx-auto md:mt-5 md:text-xl lg:mx-0">
            {{ paragraph }}
        </p>
    </div>
</template>`;
}

export function makeContextEditMode({
    contextCachePage,
    contextComponentRegistry,
    contextRouter,
    loadStylesheet,
    serviceComponent,
    servicePage,
}) {
    const state = reactive({
        editedChapterId: null,
        editedPage: null,
        isInEditMode: null,
    });
    const editedChapter = computed(() => {
        if (!state.editedChapterId) return null;

        // REFACTOR
        // Abstraction for such for of loops everywhere.
        for (const { chapters } of Object.values(state.editedPage.areas)) {
            for (const chapter of chapters) {
                if (chapter.id === state.editedChapterId) return chapter;
            }
        }
        console.error(`Chapter with ID "${state.editedChapterId}" not found!`)
    });

    const loadEditModeAssets = async () => {
        await Promise.all([
            loadEditor(),
            loadStylesheet('/vendor/tailwindcss/tailwind.editor.min.css'),
        ]);
    }

    const setEditedChapter = (chapter) => {
        state.editedChapterId = chapter && chapter.id;
    };

    const updateChapter = (chapter) => {
        for (const area of Object.values(state.editedPage.areas)) {
            const originalChapter = area.chapters.find(({ id }) => id === chapter.id);
            if (!originalChapter) continue;

            Object.assign(originalChapter, chapter);
            return;
        }
    };

    const addComponent = async (componentPartial) => {
        const component = await serviceComponent.create(componentPartial);
        await contextComponentRegistry.registerComponent(component.id);

        return component;
    };

    const addChapter = async (areaName, chapterPartial) => {
        const chapter = {
            data: null,
            id: `NEW_CHAPTER_${Date.now()}`,
            ...chapterPartial,
        };

        if (!chapterPartial.component.id) {
            chapter.component = await addComponent({
                sfc: makeNewSfc(chapterPartial.component.name),
                type: COMPONENT_TYPES.component,
                ...chapterPartial.component,
            });
        }

        state.editedPage.areas[areaName].chapters.push(chapter);
    };

    const reorderChapters = (areaName, chapterIds) => {
        const area = state.editedPage.areas[areaName];
        area.chapters = chapterIds.map(chapterId => area.chapters.find(chapter => chapter.id === chapterId));
    };

    const removeChapter = (areaName, chapterId) => {
        state.editedChapterId = null;
        console.log("Removing chapter id " + chapterId)
        let area = state.editedPage.areas[areaName];
        area.chapters = area.chapters.filter(chapter => chapter.id !== chapterId)
        console.log("Setting state.editedChapterId to null")
    }

    const deselectChapter = (event) => {
        console.log (event.target.classList);
        if (event.target.classList.contains("windflow-layout")) {
            console.log("DESELECTING")
            state.editedChapterId = null;
            event.preventDefault();
        }
    }

    const saveComponent = async (data) => {
        const component = await serviceComponent.update(data.id, data);

        for (const { chapters } of Object.values(state.editedPage.areas)) {
            for (const chapter of chapters) {
                if (chapter.component.id !== component.id) continue;
                Object.assign(chapter.component, component);
            }
        }

        await contextComponentRegistry.registerComponent(component.id, { bustCache: true });
    };

    const savePage = async (data) => {
        contextCachePage.mutatePage(data.id, data);
        state.editedPage = await servicePage.update(data.id, data, { include: ['components'] });
    };

    const loadEditedPage = async () => {
        state.editedPage = await servicePage.findCurrent({ include: ['components'] });
        state.editedChapterId = null;
    };

    const enableEditMode = async () => {
        const isEnabledForTheFirstTime = state.isInEditMode === null;
        if (isEnabledForTheFirstTime) {
            contextRouter.beforeEach(loadEditedPage);
            await loadEditModeAssets();
        }

        window.location.hash = editModeHash;
        await loadEditedPage();
        state.isInEditMode = true;
    };

    const disableEditMode = async () => {
        history.replaceState(null, null, ' ');
        // REFACTOR
        // Show wraning before dismissing unsaved changes.
        state.editedPage = null;
        state.isInEditMode = false;
    };

    const editModeHash = 'edit';
    const isInEditModeInitially = window.location.hash === `#${editModeHash}`;
    if (isInEditModeInitially) enableEditMode();

    window.addEventListener('hashchange', () => {
        if (window.location.hash === `#${editModeHash}`) enableEditMode();
    }, true);

    return {
        ...toRefs(state),
        addChapter,
        disableEditMode,
        editedChapter,
        enableEditMode,
        reorderChapters,
        removeChapter,
        deselectChapter,
        saveComponent,
        savePage,
        setEditedChapter,
        updateChapter,
    };
}

/** Context: Router **/

export function makeContextRouter({
    contextCachePage,
    contextComponentRegistry,
}) {
    const listeners = [];
    let previousPath = null;
    const currentPath = ref(window.location.pathname);
    const currentPageId = computed(() => currentPath.value.replace('/', '') || '/');

    const goTo = (path) => {
        previousPath = window.location.pathname;
        currentPath.value = path;
        listeners.forEach(listener => listener(path, previousPath));
    };

    const push = (path) => {
        window.history.pushState(null, null, path);
        goTo(path);
    };

    const beforeEach = callback => listeners.push(callback);

    window.addEventListener('popstate', () => {
        goTo(window.location.pathname);
    });

    const getAllComponentsForPage = (page) => {
        const componentDescriptions = [{
            id: page.layout.id,
            type: COMPONENT_TYPES.layout,
        }];

        for (const area of Object.values(page.areas)) {
            for (const chapter of area.chapters) {
                componentDescriptions.push({
                    id: chapter.component.id,
                    type: COMPONENT_TYPES.component,
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

    const updateMetaData = (metaInfo) => {
        document.title = metaInfo.title;

        // for (const [attribute, value] of Object.entries(metaInfo.htmlAttrs)) {
        //     document.documentElement.setAttribute(attribute, value);
        // }
        //
        // for (const [attribute, value] of Object.entries(metaInfo.bodyAttrs)) {
        //     document.body.setAttribute(attribute, value);
        // }

        const $head = document.getElementsByTagName('head')[0];
        const renderId = `${Date.now()}`;

        // REFACTOR
        // - Dedupe logic.
        for (const meta of metaInfo.meta) {
            const $existing = $head.querySelector(Object.entries(meta).map(([attribute, value]) => `[${attribute}="${value}"]`).join(''));
            if ($existing) {
                $existing.dataset.renderId = renderId;
                continue;
            }

            const element = document.createElement('meta');
            element.dataset.renderId = renderId;

            for (const [attribute, value] of Object.entries(meta)) {
                element.setAttribute(attribute, value);
            }

            $head.appendChild(element);
        }

        for (const link of metaInfo.link) {
            const $existing = $head.querySelector(Object.entries(link).map(([attribute, value]) => `[${attribute}="${value}"]`).join(''));
            if ($existing) {
                $existing.dataset.renderId = renderId;
                continue;
            }

            const element = document.createElement('link');
            element.dataset.renderId = renderId;

            for (const [attribute, value] of Object.entries(link)) {
                element.setAttribute(attribute, value);
            }

            $head.appendChild(element);
        }

        const $obsoleteElements = $head.querySelectorAll(`[data-render-id]:not([data-render-id="${renderId}"])`)
        $obsoleteElements.forEach($el => $head.removeChild($el));
    };

    // REFACTOR
    // - Make use of promises and wait until resolved and registered before returning data.
    const { data, error } = contextCachePage.loadPage(() => currentPageId.value);
    const registerError = ref(null);

    watch(data, () => {
        if (!data.value) return;

        try {
            registerError.value = null;
            const components = getAllComponentsForPage(data.value);
            components.forEach(({ id }) => contextComponentRegistry.registerComponent(id));
            updateMetaData(data.value.metaInfo);
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
        beforeEach,
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
