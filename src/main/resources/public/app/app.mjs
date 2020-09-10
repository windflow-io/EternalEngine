import { createApp } from '/vendor/vue3/vue.esm-browser.js';
import VueX from '/vendor/vue3/vuex.esm-browser.js'
import {FlowApplication} from '/modules/coreComponents.mjs'
import {
    bootstrapPage,
    loadEditModeAssets,
} from '/modules/windflowUtils.mjs'

const EDIT_MODE_HASH = 'edit';

export const app = createApp(FlowApplication);

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
        editComponent: null,
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
        setEditComponent(state, value = null) {
            state.editComponent = value;
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
            if (isInEditMode) {
                dispatch('enableEditMode');
            } else {
                commit('setEditMode', false);
            }

            /**@TODO: Insert the head elements in here **/
        },
        addComponent({ commit, state }, { area, name = `NewComponent` }) {
            const id = Date.now();
            app.component(name, {
                name,
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
                template: `
                    <div class="max-w-screen-xl mx-auto py-12 px-4 sm:px-6 lg:py-16 lg:px-8">
                        <h2 class="leading-9 font-extrabold tracking-tight text-gray-900 sm:leading-10 text-3xl">
                            {{ heading }}
                        </h2>
                        <p class="mt-2 text-base text-gray-500 sm:mt-5 sm:text-lg sm:max-w-xl sm:mx-auto md:mt-5 md:text-xl lg:mx-0">
                            {{ paragraph }}
                        </p>
                    </div>
                `,
            });

            const newAreas = state.pageAreas.map((pageArea) => {
                if (pageArea.area !== area) return pageArea;

                return {
                    ...pageArea,
                    components: [
                        ...pageArea.components,
                        { name: `localhost.${name}`, id },
                    ],
                }
            });
            commit('setPageAreas', newAreas);

            return id;
        },
        updateComponent({}, { code, content, id }) {
            /**@TODO: Persist to database **/
        },
        async enableEditMode({commit}) {
            await loadEditModeAssets();
            commit('setEditMode', true);
        },
    }
})

app.use(store);
app.mount(`#app`);
