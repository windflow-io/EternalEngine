import { createApp } from '/vendor/vue3/vue.esm-browser.js';
import VueX from '/vendor/vue3/vuex.esm-browser.js'
import FlowApplication from '/api/components/FlowApplication.mjs'

const registeredComponents = {};

async function loadComponent(url) {
    const module = await import(url);

    if (!registeredComponents[module.default.name]) {
        app.component(module.default.name, module.default);
        registeredComponents[module.default.name] = module.default;
    }

    return module.default.name;
}

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

            const allComponents = [];
            page.components.forEach(section => section.components.forEach(component => allComponents.push(component)));

            const [layout, ...components] = await Promise.all([
                loadComponent('/api/layouts/' + page.layout + '.mjs'),
                ...allComponents.map(component => loadComponent('/api/components/' + component.name + '.mjs')),
            ]);

            commit('setPageLayout', page.layout);
            commit('setPageComponents', page.components);
        }
    }
})

const app = createApp(FlowApplication);
app.use(store);

app.mount(`#app`);
