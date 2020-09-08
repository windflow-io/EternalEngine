import { createApp } from '/vendor/vue3/vue.esm-browser.js';
import VueX from '/vendor/vue3/vuex.esm-browser.js'
import {FlowApplication} from '/modules/coreComponents.mjs'
import {
    enableEditMode,
    bootstrapPage,
} from '/modules/windflowUtils.mjs'

const EDIT_MODE_HASH = 'edit';
const EDIT_MODE = window.location.hash === `#${EDIT_MODE_HASH}`;
if (EDIT_MODE) enableEditMode();

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
        error: {},
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
            const page = await bootstrapPage({ host: payload.host, path: payload.path });

            commit('setPageHttpStatus', page.httpStatus)
            commit('setPageEncoding', page.encoding);
            commit('setPageLang', page.lang);
            commit('setPageTitle', page.title);
            commit('setPageMeta', page.metaData);
            commit('setPageLayout', page.layout);
            commit('setPageAreas', page.areas);
            commit('setPageData', page.data);

            document.title = page.title;
            document.documentElement.lang = page.lang;

            /**@TODO: Insert the head elements in here **/
        }
    }
})

export const app = createApp(FlowApplication);
app.use(store);
app.mount(`#app`);
