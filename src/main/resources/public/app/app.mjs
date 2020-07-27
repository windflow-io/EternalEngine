import { createApp } from '/vendor/vue3/vue.esm-browser.js';
import VueX from '/vendor/vue3/vuex.esm-browser.js'
import {FlowApplication} from '/modules/coreComponents.mjs'
import * as componentService from '/services/component.mjs'
import {
    alertNotifier as notifier,
    withErrorHandling,
    withRetryHandling,
} from '/modules/windflowUtils.mjs'

const loadLayout = withErrorHandling(
    withRetryHandling(name => componentService.load(name, {
        type: componentService.COMPONENT_TYPES.layout,
    })),
    { notifier },
);

const loadComponent = withErrorHandling(
    withRetryHandling(componentService.load),
    { notifier },
);

const store = new VueX.createStore({
    state: {
        pageMeta: {
            title: null,
            description: null,
            httpStatus: null
        },
        pageLayout: null,
        pageComponents: [],
        pageData: {},
    },
    mutations: {
        setPageMeta(state, value) {
            state.pageMeta = value;
        },
        setPageLayout(state, value) {
            state.pageLayout = value;
        },
        setPageComponents(state, value) {
            state.pageComponents = value;
        },
        setPageData(state, value) {
            state.pageData = value;
        }

    },
    actions: {
        async fetchPageData({context, commit, state}, payload) {
            const page = await fetch('/api/pages/' + payload.host + payload.path)
                .then((response) => response.json());

            commit('setPageMeta', page.metaData);
            commit('setPageData', page.data);

            document.title = page.metaData.title;



            document.getElementsByTagName("meta").namedItem('description').setAttribute("content", page.metaData.description);
            /**@TODO: Allow the adding of meta data (including charset and viewport) **/

            const allComponents = [];
            page.components.forEach(section => section.components.forEach(component => allComponents.push(component)));

            const [layout, ...components] = await Promise.all([
                loadLayout(page.layout),
                ...allComponents.map(component => loadComponent(component.name)),
            ]);

            commit('setPageLayout', page.layout);
            commit('setPageComponents', page.components);
        }
    }
})

export const app = createApp(FlowApplication);
app.use(store);
app.mount(`#app`);
