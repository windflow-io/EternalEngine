import {
    computed,
    createApp,
} from '../vendor/vue3/vue.esm-browser.js';

import {
    FlowToolbar,
} from '../modules/coreComponents.js';
import {
    http,
    loadStylesheet,
    makeApi,
    makeContextCachePage,
    makeContextComponentRegistry,
    makeContextEditMode,
    makeContextRouter,
    makeServiceComponent,
    makeServicePage,
} from '../modules/windflowUtils.js';

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
        const api = makeApi({ http });
        const serviceComponent = makeServiceComponent({ api });
        const servicePage = makeServicePage({ api, serviceComponent });

        const contextComponentRegistry = makeContextComponentRegistry();
        app.provide(CONTEXT_COMPONENT_REGISTRY, contextComponentRegistry);

        const contextCachePage = makeContextCachePage({ servicePage });
        app.provide(CONTEXT_CACHE_PAGE, contextCachePage);

        const contextRouter = makeContextRouter({
            contextCachePage,
            contextComponentRegistry,
        });
        app.provide(CONTEXT_ROUTER, contextRouter);

        const contextEditMode = makeContextEditMode({
            contextCachePage,
            contextComponentRegistry,
            contextRouter,
            loadStylesheet,
            serviceComponent,
            servicePage,
        });
        app.provide(CONTEXT_EDIT_MODE, contextEditMode);

        const { currentPath, page: currentPage } = contextRouter;
        const {
            activateChapter,
            addChapter,
            disableEditMode,
            editedChapter,
            editedPage,
            isInEditMode,
            reorderChapters,
            removeChapter,
            saveComponent,
            savePage,
            setEditedChapter,
            updateChapter,
        } = contextEditMode;
        const page = computed(() => isInEditMode.value ? editedPage.value : currentPage.value);
        const { components } = contextComponentRegistry;
        const layoutComponent = computed(() => components[page.value.layout.id]);

        return {
            activateChapter,
            addChapter,
            currentPath,
            disableEditMode,
            editedChapter,
            isInEditMode,
            layoutComponent,
            page,
            reorderChapters,
            removeChapter,
            saveComponent,
            savePage,
            setEditedChapter,
            updateChapter,
        }
    },
    template: `
        <div>
            <flow-toolbar
                v-if="isInEditMode"
                :chapter="editedChapter"
                @disable-chapter-edit-mode="setEditedChapter(null)"
                @disable-edit-mode="disableEditMode"
                @save-component="saveComponent"
                @save-page="savePage(page)"
                @update-chapter="updateChapter"
            />
            <component
                v-if="page"
                :is="layoutComponent"
                :key="currentPath"
                :areas="page.areas"
                @add-chapter="addChapter"
                @enable-chapter-edit-mode="setEditedChapter"
                @reorder-chapters="reorderChapters"
                @remove-chapter="removeChapter"
            />
        </div>
    `,
};

const app = createApp(FlowApplication);
app.mount(`#app`);
