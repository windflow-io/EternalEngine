import {
    computed,
    createApp,
} from '../vendor/vue3/vue.esm-browser.js';

import {
    FlowToolbar,
} from '../modules/coreComponents.mjs';
import {
    http,
    loadStylesheet,
    makeApi,
    makeComponentService,
    makeContextCacheComponent,
    makeContextCachePage,
    makeContextComponentRegistry,
    makeContextEditMode,
    makeContextRouter,
    makePageService,
} from '../modules/windflowUtils.mjs';

export const CONTEXT_COMPONENT_REGISTRY = Symbol();
export const CONTEXT_CACHE_COMPONENT = Symbol();
export const CONTEXT_CACHE_PAGE = Symbol();
export const CONTEXT_ROUTER = Symbol();
export const CONTEXT_EDIT_MODE = Symbol();

export const FlowApplication = {
    name: 'FlowApplication',
    components: {
        FlowToolbar,
    },
    setup() {
        const urlParams = new URLSearchParams(window.location.search);
        const host = urlParams.get('host') || location.host;

        const api = makeApi({ http });
        const componentService = makeComponentService({ api, host });
        const pageService = makePageService({ api, host });

        const contextComponentRegistry = makeContextComponentRegistry();
        app.provide(CONTEXT_COMPONENT_REGISTRY, contextComponentRegistry);

        const { registerUpdatedComponent } = contextComponentRegistry;
        const contextCacheComponent = makeContextCacheComponent({
            componentService,
            registerUpdatedComponent,
        });
        app.provide(CONTEXT_CACHE_COMPONENT, contextCacheComponent);

        const contextCachePage = makeContextCachePage({ pageService });
        app.provide(CONTEXT_CACHE_PAGE, contextCachePage);

        const contextEditMode = makeContextEditMode({ loadStylesheet });
        app.provide(CONTEXT_EDIT_MODE, contextEditMode);

        const { loadPage } = contextCachePage;
        const { registerComponent } = contextComponentRegistry;
        const contextRouter = makeContextRouter({
            loadPage,
            registerComponent,
        });
        app.provide(CONTEXT_ROUTER, contextRouter);

        const { currentPath, page } = contextRouter;
        const { isInEditMode } = contextEditMode;

        const { components } = contextComponentRegistry;
        const layoutComponent = computed(() => {
            return page.value ? components[page.value.layout] : null;
        });

        const areas = computed(() => {
            if (!page.value) return null;

            const areas = {};

            for (const area of page.value.areas) {
                areas[area.area] = {
                    components: area.components,
                };
            }

            return areas;
        });

        return {
            areas,
            currentPath,
            isInEditMode,
            layoutComponent,
        }
    },
    template: `
        <div>
            <flow-toolbar v-if="isInEditMode"/>
            <component
                :is="layoutComponent"
                :key="currentPath"
                :areas="areas"
            />
        </div>
    `,
};

const app = createApp(FlowApplication);
app.mount(`#app`);
