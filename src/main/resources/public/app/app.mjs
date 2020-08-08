import { createApp } from '/vendor/vue3/vue.esm-browser.js';
import VueX from '/vendor/vue3/vuex.esm-browser.js'
import {FlowApplication,ErrorLayout} from '/modules/coreComponents.mjs'
import {
    COMPONENT_TYPES,
    alertNotifier as notifier,
    withErrorHandling,
    withRetryHandling,
    componentService,
    pageService,
} from '/modules/windflowUtils.mjs'

const bypass4xxErrors = error => error.status >= 400 && error.status < 500;
const loadPage = withRetryHandling(pageService.load, { bypass: bypass4xxErrors });

const loadLayout = withRetryHandling(name => componentService.load(name, {
    type: COMPONENT_TYPES.layout,
}));

const loadComponent = withErrorHandling(
    withRetryHandling(componentService.load),
    { notifier },
);

const errorPage = {
    metaData: {
        title: 'Error',
        description: 'Please try again later',
        httpStatus: '500',
    },
    layout: ErrorLayout.name,
    components: [],
    data: {
        headline: 'Error',
        subHeadline: 'Please try again later',
    },
};

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
            let page;

            try {
                page = await loadPage({ host: payload.host, path: payload.path });
                await loadLayout(page.layout);
            } catch (error) {
                /**@TODO: Maybe add error logging with something like Sentry or DataDog **/
                console.log(error);
                await componentService.register(ErrorLayout);
                page = errorPage;
            }

            commit('setPageMeta', page.metaData);
            commit('setPageData', page.data);
            commit('setPageLayout', page.layout);

            document.title = page.metaData.title;
            document.getElementsByTagName("meta").namedItem('description').setAttribute("content", page.metaData.description);
            /**@TODO: Allow the adding of meta data (including charset and viewport) **/

            const allComponents = [];
            page.components.forEach(section => section.components.forEach(component => allComponents.push(component)));
            await Promise.all(allComponents.map(component => loadComponent(component.name)));

            commit('setPageComponents', page.components);
        }
    }
})

export const app = createApp(FlowApplication);
app.use(store);
app.mount(`#app`);
