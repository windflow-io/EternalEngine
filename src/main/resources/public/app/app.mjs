import { createApp } from '/vendor/vue3/vue.esm-browser.js';
import VueX from '/vendor/vue3/vuex.esm-browser.js'
import {FlowApplication,ErrorLayout} from '/modules/coreComponents.mjs'
import {
    COMPONENT_TYPES,
    alertNotifier as notifier,
    enableEditMode,
    withErrorHandling,
    withRetryHandling,
    componentService,
    pageService,
} from '/modules/windflowUtils.mjs'

const EDIT_MODE_HASH = 'edit';
const EDIT_MODE = window.location.hash === `#${EDIT_MODE_HASH}`;
if (EDIT_MODE) enableEditMode();

const bypass4xxErrors = error => error.status >= 400 && error.status < 500;
const loadPage = withRetryHandling(pageService.load, { bypass: bypass4xxErrors });

const loadLayout = withRetryHandling(name => componentService.load(name, {
    type: COMPONENT_TYPES.layout,
}));

const loadComponent = withErrorHandling(
    withRetryHandling(componentService.load),
    { notifier },
);

/* @TODO: This still feels weird in here. */
const errorPage = {
    metaData: {
        title: 'Error', /* @TODO: make dynamic */
        description: 'Please try again later', /* @TODO: make dynamic */
        httpStatus: '500',  /* @TODO: make dynamic */
    },
    layout: ErrorLayout.name,
    areas: [],
    data: {
        headline: 'Error', /*TODO: Rename to errorTitle and make dynamic*/
        subHeadline: 'Please try again later',/*TODO: Rename to errorDetail and make dynamic*/
    },
};

const store = new VueX.createStore({
    state: {
        pageHttpStatus: undefined,
        pageEncoding: 'utf-8',
        pageLang: 'en',
        pageTitle: undefined,
        pageMeta: {},
        pageLayout: undefined,
        pageAreas: [],
        pageData: {},
    },
    mutations: {
        setPageHttpStatus(state, value) {
            if (value) state.pageHttpStatus = value;
        },
        setPageEncoding(state, value) {
            if (value) state.pageEncoding = value;
        },
        setPageLang(state, value) {
            if (value) state.pageLang = value;
        },
        setPageTitle(state, value) {
            if (value) state.pageTitle = value;
        },
        setPageMeta(state, value) {
            if (value) state.pageMeta = value;
        },
        setPageLayout(state, value) {
            if (value) state.pageLayout = value;
        },
        setPageAreas(state, value) {
            if (value) state.pageAreas = value;
        },
        setPageData(state, value) {
            if (value) state.pageData = value;
        }

    },
    actions: {
        async fetchPageData({context, commit, state}, payload) {
            let page;

            try {
                page = await loadPage({ host: payload.host, path: payload.path });
                await loadLayout(page.layout);
            } catch (error) {
                /**@TODO: Maybe add error logging with something like Sentry or DataDog (send ALL errors to server at some point later) **/
                console.log ("Failed to load page data or layout template - no error specified by server.")
                /**@TODO: Can we let the error page know what happened? **/
                await componentService.register(ErrorLayout);
                page = errorPage;
            }

            commit('setPageHttpStatus', page.httpStatus)
            commit('setPageEncoding', page.encoding);
            commit('setPageLang', page.lang);
            commit('setPageTitle', page.title);
            commit('setPageHttpStatus', page.httpStatus);
            commit('setPageMeta', page.metaData);
            commit('setPageLayout', page.layout);
            commit('setPageAreas', page.areas);
            commit('setPageData', page.data);

            document.title = page.metaData.title;
            //document.getElementsByTagName("meta").namedItem('description').setAttribute("content", page.metaData.description);
            /**@TODO: Allow the adding of meta data (including charset and viewport) **/

            const allComponents = [];
            page.areas.forEach(section => section.components.forEach(component => allComponents.push(component)));
            await Promise.all(allComponents.map(component => loadComponent(component.name)));

            commit('setPageAreas', page.areas);
        }
    }
})

export const app = createApp(FlowApplication);
app.use(store);
app.mount(`#app`);
