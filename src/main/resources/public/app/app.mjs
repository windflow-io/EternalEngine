import { createApp } from '/vendor/vue3/vue.esm-browser.js';
import VueX from '/vendor/vue3/vuex.esm-browser.js'
import {FlowApplication} from '/modules/coreComponents.mjs'
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
            } catch (error) {
                /**@TODO: Maybe add error logging with something like Sentry or DataDog **/
                console.log(error);
                page = {
                    metaData: {
                        title: 'Error',
                        description: 'Please try again later',
                        httpStatus: '500',
                    },
                    layout: 'windflowx.CenteredLayout',
                    components: [
                        {
                            area: 'center',
                            components: [{ name: 'windflowx.ErrorMessage', id: '1' }],
                        },
                    ],
                    data: {},
                };
            }

            const allComponents = [];
            page.components.forEach(section => section.components.forEach(component => allComponents.push(component)));
            const componentPromise = Promise.all(allComponents.map(component => loadComponent(component.name)));

            try {
                await loadLayout(page.layout);
            } catch (error) {
                /**@TODO: Maybe add error logging with something like Sentry or DataDog **/
                console.log(error);
                // If we can't resolve the layout, we can't render anything,
                // so we redirect to a plain static HTML page.
                window.location.replace('/error');
            }

            commit('setPageMeta', page.metaData);
            commit('setPageData', page.data);
            commit('setPageLayout', page.layout);

            document.title = page.metaData.title;
            document.getElementsByTagName("meta").namedItem('description').setAttribute("content", page.metaData.description);
            /**@TODO: Allow the adding of meta data (including charset and viewport) **/

            await componentPromise;

            commit('setPageComponents', page.components);
        }
    }
})

export const app = createApp(FlowApplication);
app.use(store);
app.mount(`#app`);
