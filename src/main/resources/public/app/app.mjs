import { createApp } from '/vendor/vue3/vue.esm-browser.js';
import VueX from '/vendor/vue3/vuex.esm-browser.js'
import {FlowApplication} from '/modules/coreComponents.mjs'
import {
    bootstrapPage,
    loadEditModeAssets,
} from '/modules/windflowUtils.mjs'

const EDIT_MODE_HASH = 'edit';

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
        editMode: false,
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
        },
        setEditMode(state, value) {
            state.editMode = value;
        },
    },
    actions: {
        async fetchPageData({context, commit, dispatch, state}, payload) {
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

            const isInEditMode = window.location.hash === `#${EDIT_MODE_HASH}`;
            if (isInEditMode) dispatch('enableEditMode');

            /**@TODO: Insert the head elements in here **/
        },
        async enableEditMode({commit}) {
            await loadEditModeAssets();
            commit('setEditMode', true);
        },
    }
})

export const app = createApp(FlowApplication);
app.use(store);
app.mount(`#app`);
